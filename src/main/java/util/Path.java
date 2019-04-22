package util;

public class Path {
    /**
     * IDE：项目根目录绝对路径
     * jar: jar包所在目录绝对路径
     */
    public static final String PATH;

    static {
        PATH = System.getProperty("user.dir");
    }
}
