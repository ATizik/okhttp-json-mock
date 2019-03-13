package ir.mirrajabi.okhttpjsonmock.sample;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ir.mirrajabi.okhttpjsonmock.OkHttpMockInterceptor;
import ir.mirrajabi.okhttpjsonmock.helpers.ResponseHandler;
import ir.mirrajabi.okhttpjsonmock.helpers.ResponsesQueue;
import ir.mirrajabi.okhttpjsonmock.providers.InputStreamProvider;
import ir.mirrajabi.okhttpjsonmock.sample.services.UsersService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://example.com";

    RecyclerView recyclerView;

    private UsersAdapter usersAdapter;
    private UsersService usersService;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeComponents();


        ResponsesQueue.getInstance().setListener((ResponseHandler responseHandler) -> {
            List<String> list = responseHandler.getFiles();
            CharSequence[] cs = list.toArray(new CharSequence[list.size()]);
            runOnUiThread(() -> {
                AlertDialog alert = new AlertDialog.Builder(this)
                        .setTitle(responseHandler.getMethodName())
                        .setItems(cs, (dialog, which) -> responseHandler.setFileName(list.get(which))).create();
                alert.setOnDismissListener(dialog -> {
                    responseHandler.dismissed();
                });
                alert.show();
            });

        });

        requestUsers();
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.clear();
        compositeDisposable.dispose();
        super.onDestroy();
    }

    private void initializeComponents() {
        usersService = constructService();

        usersAdapter = new UsersAdapter(this);
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(usersAdapter);
    }

    private UsersService constructService() {
        OkHttpClient okHttpClient = constructClient();
        Retrofit retrofit = constructRetrofit(okHttpClient);
        return retrofit.create(UsersService.class);
    }

    private Retrofit constructRetrofit(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build();
    }

    private OkHttpClient constructClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new OkHttpMockInterceptor(getAndroidProvider(), 5))
                .build();
    }

    private InputStreamProvider getAndroidProvider() {
        return new InputProvider();
    }

    private class InputProvider implements InputStreamProvider {

        @Override
        public InputStream provide(String path) {
            try {
                return getAssets().open(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public List<String> list(String path) throws IOException {
            ArrayList<String> array = new ArrayList<>();
            String[] list = getAssets().list(path);

            if (list == null) {
                return array;
            }
            Collections.addAll(array, list);

            Iterator<String> it = array.iterator();
            while (it.hasNext()) {
                String foo = it.next();
                if (!foo.endsWith(".json")) it.remove();
            }
            return array;
        }
    }

    private void requestUsers() {
        Disposable subscription = usersService.getUsers(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .retry(3)
                .subscribe(userModels -> usersAdapter.addData(userModels),
                        Throwable::printStackTrace);
        compositeDisposable.add(subscription);
    }
}
