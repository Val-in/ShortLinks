package core;

import java.io.InputStream;
import java.util.Properties;

public class Config {
    private final Properties props = new Properties();

    public Config() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) throw new RuntimeException("config.properties not found in classpath");
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load config: " + e.getMessage(), e);
        }
    }

    public int getMaxUrlLength() {
        return Integer.parseInt(props.getProperty("max.url.length", "2048"));
    }

    public int getShortCodeLength() {
        return Integer.parseInt(props.getProperty("shortcode.length", "6"));
    }

    public String getShortCodeCharset() {
        return props.getProperty("shortcode.charset",
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
    }

    public long getDefaultTTLMinutes() {
        return Long.parseLong(props.getProperty("default.ttl.minutes", "60"));
    }

    public long getCleanerIntervalSeconds() {
        return Long.parseLong(props.getProperty("cleaner.interval.seconds", "60"));
    }

    public String getUuidStorePath() {
        String path = props.getProperty("uuid.store.path", "${user.home}/.shortener_uuid");
        return path.replace("${user.home}", System.getProperty("user.home"));
    }

    public int getMaxClicksDefault() {
        return Integer.parseInt(props.getProperty("max.clicks.default", "0"));
    }

    public boolean isNotifyEnabled() {
        return Boolean.parseBoolean(props.getProperty("notify.enabled", "true"));
    }

    public String getNotifyType() {
        return props.getProperty("notify.type", "console");
    }
}
