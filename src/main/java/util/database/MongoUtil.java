package util.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

public class MongoUtil {

    /**
     * 连接并返回一个新的mongo数据库
     */
    public static MongoClient connectMongoClient(String[] hosts, String userName, String password) {
        List<ServerAddress> addrs = new ArrayList<>();
        for (String host : hosts) {
            String[] split = host.split(":");
            addrs.add(new ServerAddress(split[0], Integer.parseInt(split[1])));
        }
        MongoCredential credential =
                MongoCredential.createScramSha1Credential(userName, "admin", password.toCharArray());
        return new MongoClient(addrs, credential, MongoClientOptions.builder().build());
    }

    /**
     * 检测集合是否存在
     */
    public static boolean collectionExists(String collectionName, MongoDatabase database) {
        return database.listCollectionNames().into(new ArrayList<>()).contains(collectionName);
    }

    /**
     * 创建新集合 若存在则覆盖
     */
    public static boolean createNewCollection(String collectionName, MongoDatabase database) {
        try {
            if (collectionExists(collectionName, database))
                database.getCollection(collectionName).drop();
            database.createCollection(collectionName);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 获得所有集合名称
     */
    public static List<String> getAllCollection(MongoDatabase database) {
        return database.listCollectionNames().into(new ArrayList<>());
    }
}
