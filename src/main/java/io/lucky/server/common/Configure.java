package io.lucky.server.common;

public class Configure {
    private static Configure instance;
    public synchronized static final Configure getInstance(){
        if (instance == null) {
            instance = new Configure();
        }
        return instance;
    }

    private Configure(){}
    public final int web_port = 8080;
    public final String gmt = "GMT"; // "Asia/Seoul"
    public final String db_url = "jdbc:h2:tcp://localhost/~/lucky-developer/h2-2.2.224/test";
    public final String db_username = "sa";
    public final String db_password = "";

}
