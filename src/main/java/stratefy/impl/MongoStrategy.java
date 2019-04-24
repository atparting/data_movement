package stratefy.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import config.CommonConf;
import config.ResourceConf;
import org.bson.Document;
import stratefy.Strategy;
import util.database.MongoUtil;
import util.log.Log;

import java.util.ArrayList;
import java.util.List;

public class MongoStrategy implements Strategy {

    private MongoClient client;
    private MongoDatabase database;
    private Integer current;

    @Override
    public void init(ResourceConf conf) {
        client = MongoUtil.connectMongoClient(conf.getMongoHosts(), conf.getMongoUsername(), conf.getMongoPassword());
        database = client.getDatabase(conf.getMongoDatabase());
        clear();
    }

    @Override
    public void clear() {
        current = 1;
    }

    @Override
    public List<String> batchGet(String tableName) {
        MongoCollection<Document> collection = database.getCollection(tableName);
        FindIterable<Document> documents =
                collection.find().skip((current - 1) * CommonConf.BATCH_NUM).limit(CommonConf.BATCH_NUM);
        List<String> list = new ArrayList<>();
        for (Document document : documents) {
            document.remove("_id");
            list.add(JSON.toJSONString(document));
        }
        current++;
        return list;
    }

    @Override
    public int batchSet(String tableName, List<String> dataList) {
        MongoCollection<Document> collection = database.getCollection(tableName);
        List<Document> documentList = new ArrayList<>(dataList.size());
        for (String json : dataList) {
            try {
                Document document = JSON.parseObject(json, Document.class);
                documentList.add(document);
            } catch (JSONException e) {
                Log.error("jsonStr to map exception, jsonStr = " + json);
            }
        }
        collection.insertMany(documentList);
        return documentList.size();
    }

    @Override
    public List<String> allResourceNames() {
        return MongoUtil.getAllCollection(database);
    }

    @Override
    public boolean resourceExists(String collectionName) {
        return MongoUtil.collectionExists(collectionName, database);
    }

    @Override
    public boolean createNewResource(String collectionName) {
        return MongoUtil.createNewCollection(collectionName, database);
    }

    @Override
    public void close() {
        client.close();
    }
}
