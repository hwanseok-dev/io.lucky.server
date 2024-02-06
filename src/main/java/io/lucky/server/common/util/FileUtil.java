package io.lucky.server.common.util;

import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    private FileUtil(){}

    public static void close(InputStream is){
        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            ExceptionUtil.ignore(e);
        }
    }
}
