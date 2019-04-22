package util.common;

/**
 * 获得程序执行目录的绝对路径
 * IDE模式: 项目根目录
 * jar包模式: jar包所在目录
 */
public class Path {
    public static String basePath;

    static {
        basePath = System.getProperty("user.dir");
        System.out.println("[INFO] basePath: " + basePath);
    }
}
