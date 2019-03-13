package ir.mirrajabi.okhttpjsonmock.providers;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.*;
import java.util.Collections;
import java.util.List;

public class DefaultInputStreamProvider implements InputStreamProvider {
    @Override
    public InputStream provide(String path) {
        try {
            return new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> list(String path) throws IOException {
        //TODO
        throw new NotImplementedException();
    }
}
