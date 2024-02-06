package io.lucky.server.common;

import io.lucky.server.common.config.ConfigReload;

public class Configure extends ConfigReload {
    private static Configure instance;
    public synchronized static final Configure getInstance(){
        if (instance == null) {
            instance = new Configure();
            instance.setDaemon(true);
            instance.setName("Configure");
            instance.reload();
            instance.start();
        }
        return instance;
    }

    private Configure(){
        super();
    }
    public int web_port = 8080;
    public String gmt = "GMT"; // "GMT" | "Asia/Seoul"

    public String db_url = "jdbc:h2:tcp://localhost/~/lucky-developer/h2-2.2.224/test";
    public String db_username = "sa";
    public String db_password = "";

    @Override
    protected String getFileName() {
        return "lucky.conf";
    }

    @Override
    protected void apply() {
        /**
         * WEB
         */
        this.web_port = getInt("web_port", 8080);

        /**
         * Database
         */
        this.db_url = getValue("db_url", "jdbc:h2:tcp://localhost/~/lucky-developer/h2-2.2.224/test");
        this.db_username = getValue("db_username", "sa");
        this.db_password = getValue("db_password", "");
    }
}
