package ir.mirrajabi.okhttpjsonmock;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.*;

import ir.mirrajabi.okhttpjsonmock.helpers.ResourcesHelper;
import ir.mirrajabi.okhttpjsonmock.helpers.ResponseHandler;
import ir.mirrajabi.okhttpjsonmock.helpers.ResponsesQueue;
import ir.mirrajabi.okhttpjsonmock.providers.DefaultInputStreamProvider;
import ir.mirrajabi.okhttpjsonmock.providers.InputStreamProvider;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkHttpMockInterceptor implements Interceptor {
    private final static String DEFAULT_BASE_PATH = "";
    private final static int DELAY_DEFAULT_MIN = 500;
    private final static int DELAY_DEFAULT_MAX = 15000;

    private int failurePercentage;
    private String basePath;
    private InputStreamProvider inputStreamProvider;
    private int minDelayMilliseconds;
    private int maxDelayMilliseconds;

    public OkHttpMockInterceptor(int failurePercentage) {
        this(new DefaultInputStreamProvider(), failurePercentage, DEFAULT_BASE_PATH,
                DELAY_DEFAULT_MIN, DELAY_DEFAULT_MAX);
    }

    public OkHttpMockInterceptor(InputStreamProvider inputStreamProvider, int failurePercentage) {
        this(inputStreamProvider, failurePercentage, DEFAULT_BASE_PATH,
                DELAY_DEFAULT_MIN, DELAY_DEFAULT_MAX);
    }

    public OkHttpMockInterceptor(
            InputStreamProvider inputStreamProvider,
            int failurePercentage,
            int minDelayMilliseconds,
            int maxDelayMilliseconds) {
        this(inputStreamProvider, failurePercentage, DEFAULT_BASE_PATH,
                minDelayMilliseconds, maxDelayMilliseconds);
    }

    public OkHttpMockInterceptor(
            InputStreamProvider inputStreamProvider,
            int failurePercentage,
            String basePath,
            int minDelayMilliseconds,
            int maxDelayMilliseconds) {
        this.inputStreamProvider = inputStreamProvider;
        this.failurePercentage = failurePercentage;
        this.basePath = basePath;
        this.minDelayMilliseconds = minDelayMilliseconds;
        this.maxDelayMilliseconds = maxDelayMilliseconds;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        HttpUrl url = chain.request().url();
        String assetsPath = url.encodedPath().replaceFirst("/", "");
        List<String> listOfResponses = inputStreamProvider.list(assetsPath);
        int size = listOfResponses.size();
        if (size == 0) {
            return chain.proceed(chain.request());
        }
        String file = "";
        try {
            CountDownLatch latch = new CountDownLatch(1);
            ResponseHandler handler = new ResponseHandler(
                    chain.request().method() + " " + chain.request().url().encodedPath() + "/"
                            + (chain.request().url().encodedQuery() != null ? chain.request().url().encodedQuery() : "")
                            + (chain.request().body() != null ? " BODY: " + chain.request().body() : ""),
                    listOfResponses, latch);
            ResponsesQueue.getInstance().push(handler);
            latch.await();//latch.await(maxDelayMilliseconds,TimeUnit.MILLISECONDS);

            file = handler.getFileName();
        } catch (InterruptedException e) {
            return chain.proceed(chain.request());
        }
        if (file == null) {
            return chain.proceed(chain.request());
        }
        String responseString =
                ResourcesHelper.loadFileAsString(
                        inputStreamProvider,
                        assetsPath + file
                );

        String result = responseString != null ? responseString : "";

        int code;
        try {
            code = Integer.parseInt(file.substring(4, 6));
        } catch (Exception e) {
            code = 200;
        }
        return new Response.Builder()
                .code(code)
                .message(responseString)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_0)
                .body(ResponseBody.create(MediaType.parse("application/json"), result))
                .addHeader("content-type", "application/json")
                .build();
    }

    public int getFailurePercentage() {
        return failurePercentage;
    }

    public OkHttpMockInterceptor setFailurePercentage(int failurePercentage) {
        this.failurePercentage = failurePercentage;
        return this;
    }

    public String getBasePath() {
        return basePath;
    }

    public OkHttpMockInterceptor setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    public int getMinDelayMilliseconds() {
        return minDelayMilliseconds;
    }

    public OkHttpMockInterceptor setMinDelayMilliseconds(int minDelayMilliseconds) {
        this.minDelayMilliseconds = minDelayMilliseconds;
        return this;
    }

    public int getMaxDelayMilliseconds() {
        return maxDelayMilliseconds;
    }

    public OkHttpMockInterceptor setMaxDelayMilliseconds(int maxDelayMilliseconds) {
        this.maxDelayMilliseconds = maxDelayMilliseconds;
        return this;
    }
}