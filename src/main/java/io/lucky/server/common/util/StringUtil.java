package io.lucky.server.common.util;

import java.io.IOException;
import java.io.InputStream;

public class StringUtil {

    private StringUtil(){}

    public static String trim(String s){
        return s == null ? "" : s.trim();
    }
}
