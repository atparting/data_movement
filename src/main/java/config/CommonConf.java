package config;

import org.apache.commons.configuration.XMLConfiguration;
import util.common.FileUtil;

public class CommonConf {

    /**
     * 日志等级
     * debug info warn error
     */
    public static final String LOG_LEVEL;

    /**
     * 资源提供者
     * file、es、mongo
     */
    public static final String SOURCE;

    /**
     * 资源接收者
     * file、es、mongo
     */
    public static final String TARGET;

    /**
     * 每次导出/导入条数
     */
    public static final Integer BATCH_NUM;

    /**
     * 需要转移的资源标识
     * *: 匹配所有
     * ^$name: 匹配开头/结尾
     * ^name: 匹配开头
     * $name: 匹配结尾
     * name: 全匹配
     * 后四项可配合使用
     */
    public static final String[] RESOURCES;

    /**
     * 导入/导出时，对待已存在文件的策略
     * cover: 覆盖
     * skip: 跳过
     */
    public static final String ALREADY_EXISTS;

    static {
        XMLConfiguration configuration = FileUtil.getXmlConfig("common.xml");

        LOG_LEVEL = configuration.getString("log_level");
        SOURCE = configuration.getString("source");
        TARGET = configuration.getString("target");
        BATCH_NUM = configuration.getInt("batch_num");
        RESOURCES = configuration.getStringArray("resources");
        ALREADY_EXISTS = configuration.getString("already_exists");
    }
}
