package ir.mirrajabi.okhttpjsonmock.helpers;


import java.util.ArrayDeque;
import java.util.List;
import java.util.concurrent.Callable;

public class ResponsesQueue {
    private ResponseListener listener;

    public void push(ResponseHandler responseHandler) {
        if(listener != null) {
            listener.onStateChange(responseHandler);
        } else  {
            responseHandler.setFileName("");
        }
    }

    private static ResponsesQueue ourInstance = new ResponsesQueue();

    public static ResponsesQueue getInstance() {
        return ourInstance;
    }


    private ResponsesQueue() {
    }

    public void setListener(ResponseListener listener) {
        this.listener = listener;
    }
}
