package io.lucky.server.common.config;

import io.lucky.server.common.util.ExceptionUtil;
import io.lucky.server.common.util.FileUtil;
import io.lucky.server.common.util.StringUtil;
import io.lucky.server.common.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

@Slf4j
public abstract class ConfigReload extends Thread {

    private static final long NOT_INITIALIZED = 0L;
    private static final long RELOAD_INTERVAL = 3000;

    protected Properties property = new Properties();
    private String confPath = ".";
    private long lastReloadTime = NOT_INITIALIZED;
    private long lastModifiedTime = NOT_INITIALIZED;

    @Override
    public void run() {
        while (true) {
            reload();
            ThreadUtil.sleep(RELOAD_INTERVAL);
        }
    }

    protected void reload() {
        long now = System.currentTimeMillis();
        if (now - lastReloadTime < RELOAD_INTERVAL) {
            return;
        }
        File file = getConfFile();
        if (lastReloadTime == NOT_INITIALIZED) {
            log.info("conf reload. confPath : {}", file.getAbsolutePath());
        }
        lastReloadTime = now;

        long nowModifiedTime = file.lastModified();
        if (nowModifiedTime == lastModifiedTime) {
            return;
        }
        lastModifiedTime = nowModifiedTime;
        Properties temp = new Properties();

        if (file.canRead()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                temp.load(in);
            } catch (IOException e) {
                ExceptionUtil.ignore(e);
            } catch (Exception e) {
                ExceptionUtil.ignore(e);
            } finally {
                FileUtil.close(in);
            }
        }
        property = temp;
        apply();
    }

    public File getConfFile(){
        this.confPath = System.getProperty("lucky.conf.path", "./conf");
        return new File(confPath, getFileName());
    }

    public String getConfPath() {
        return confPath;
    }

    protected abstract String getFileName();

    protected abstract void apply();

    protected String getValue(String key) {
        return StringUtil.trim(property.getProperty(key));
    }

    protected String getValue(String key, String def) {
        return StringUtil.trim(property.getProperty(key, def));
    }

    protected int getInt(String key, int def) {
        try {
            String v = getValue(key);
            if (v != null)
                return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            ExceptionUtil.ignore(e);
        } catch (Exception e) {
            ExceptionUtil.ignore(e);
        }
        return def;
    }

    protected long getLong(String key, long def) {
        try {
            String v = getValue(key);
            if (v != null)
                return Long.parseLong(v);
        } catch (NumberFormatException e) {
            ExceptionUtil.ignore(e);
        } catch (Exception e) {
            ExceptionUtil.ignore(e);
        }
        return def;
    }

    protected boolean getBoolean(String key, boolean def) {
        try {
            String v = getValue(key);
            if (v != null)
                return Boolean.parseBoolean(v);
        } catch (NumberFormatException e) {
            ExceptionUtil.ignore(e);
        } catch (Exception e) {
            ExceptionUtil.ignore(e);
        }
        return def;
    }
}
