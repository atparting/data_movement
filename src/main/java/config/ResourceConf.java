package config;

import org.apache.commons.configuration.XMLConfiguration;
import util.Path;
import util.common.FileUtil;
import util.common.StringUtil;

public class ResourceConf {

    private static ResourceConf sourceConfig = new ResourceConf("source.xml");
    private static ResourceConf targetConfig = new ResourceConf("target.xml");

    /**
     * file路径配置
     */
    private final String filePath;

    /**
     * es集群配置
     */
    private final String[] esHosts;

    /**
     * mongo集群配置
     */
    private final String[] mongoHosts;
    private final String mongoUsername;
    private final String mongoPassword;
    private final String mongoDatabase;

    private ResourceConf(String fileName) {
        XMLConfiguration configuration = FileUtil.getXmlConfig(fileName);
        // file配置读取
        String filePathConf = configuration.getString("file.path");
        if (StringUtil.isEmpty(filePathConf) || "default".equals(filePathConf)) {
            filePath = Path.PATH + "/data/";
        } else {
            filePath = filePathConf;
        }
        // es配置读取
        esHosts = configuration.getStringArray("es.hosts");
        // mongo配置读取
        mongoHosts = configuration.getStringArray("mongo.hosts");
        mongoUsername = configuration.getString("mongo.username");
        mongoPassword = configuration.getString("mongo.password");
        mongoDatabase = configuration.getString("mongo.database");
    }

    public static ResourceConf getSourceConfig() {
        return sourceConfig;
    }

    public static ResourceConf getTargetConfig() {
        return targetConfig;
    }

    public String getFilePath() {
        return filePath;
    }

    public String[] getEsHosts() {
        return esHosts;
    }

    public String[] getMongoHosts() {
        return mongoHosts;
    }

    public String getMongoUsername() {
        return mongoUsername;
    }

    public String getMongoPassword() {
        return mongoPassword;
    }

    public String getMongoDatabase() {
        return mongoDatabase;
    }
}
