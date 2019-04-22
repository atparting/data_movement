package util.log;

import config.CommonConf;

public class Log {

    private static int level = getLevel();

    public static void debug(String msg) {
        if (level < 2)
            System.out.println("[DEBUG] " + msg);
    }

    public static void info(String msg) {
        if (level < 3)
            System.out.println("[INFO ] " + msg);
    }

    public static void warn(String msg) {
        if (level < 4)
            System.out.println("[WARN ] " + msg);
    }

    public static void error(String msg) {
        if (level < 5)
            System.out.println("[ERROR] " + msg);
    }

    private static int getLevel() {
        return "debug".equalsIgnoreCase(CommonConf.LOG_LEVEL) ?
                1 : "info".equalsIgnoreCase(CommonConf.LOG_LEVEL) ?
                2 : "warn".equalsIgnoreCase(CommonConf.LOG_LEVEL) ?
                3 : "error".equalsIgnoreCase(CommonConf.LOG_LEVEL) ?
                4 : 2;
    }
}
