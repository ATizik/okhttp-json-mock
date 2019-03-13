package ir.mirrajabi.okhttpjsonmock.providers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface InputStreamProvider {
    InputStream provide(String path);
    List<String> list(String path) throws IOException;
}
