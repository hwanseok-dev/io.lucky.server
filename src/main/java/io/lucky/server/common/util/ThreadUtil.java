package io.lucky.server.common.util;

public class ThreadUtil {

    private ThreadUtil(){}

    public static void sleep(long time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            ExceptionUtil.ignore(e);
        }
    }
}
