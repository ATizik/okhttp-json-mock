package ir.mirrajabi.okhttpjsonmock.helpers;



import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ResponseHandler {

    private String methodName;
    private List<String> files;
    //response
    private CountDownLatch latch;
    private String fileName;

    public ResponseHandler(String methodName, List<String> files, CountDownLatch latch) {
        this.methodName = methodName;
        this.files = files;
        this.latch = latch;
    }



    public List<String> getFiles() {
        return files;
    }

    public void pushResponse(ListCallback listCallback) {
        setFileName(listCallback.onResult(files));
    }

    public String getMethodName() {
        return methodName;
    }

    public interface ListCallback {
        public String onResult(List<String> responseHandler);
    }


    public void dismissed() {
        latch.countDown();
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        latch.countDown();
    }

    public String getFileName() {
        return fileName;
    }
}
