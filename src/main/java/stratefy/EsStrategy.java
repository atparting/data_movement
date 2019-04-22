package stratefy;

import com.alibaba.fastjson.JSON;
import config.CommonConf;
import config.ResourceConf;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import util.database.EsUtil;
import util.log.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EsStrategy implements Strategy {

    private RestHighLevelClient client;
    private String scrollId;

    @Override
    public void init(ResourceConf config) {
        client = EsUtil.connectEsClient(config.getEsHosts());
        clear();
    }

    @Override
    public void clear() {
        scrollId = null;
    }

    @Override
    public List<String> batchGet(String indexName) {
        try {
            SearchResponse response = EsUtil.searchScroll(indexName, scrollId, CommonConf.BATCH_NUM, client);
            scrollId = response.getScrollId();
            SearchHits hits = response.getHits();
            List<String> list = new ArrayList<>(CommonConf.BATCH_NUM);
            for (SearchHit hit : hits) {
                list.add(hit.getSourceAsString());
            }
            return list;
        } catch (IOException e) {
            Log.error("索引" + indexName + "查询失败 " + e.toString());
            return new ArrayList<>();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public int batchSet(String indexName, List<String> jsonList) {
        BulkRequest request = new BulkRequest();
        int size = 0;
        for (String json : jsonList) {
            try {
                Map<String, Object> map = JSON.parseObject(json, Map.class);
                request.add(new IndexRequest(indexName).source(map));
                size++;
            } catch (ClassCastException e) {
                Log.error("json字符串转map发生异常，json = " + json);
            }
        }
        try {
            BulkResponse response = client.bulk(request, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                Log.warn(response.buildFailureMessage());
            }
            return size;
        } catch (IOException e) {
            Log.error("索引" + indexName + "入库失败 " + e.toString());
            return 0;
        }
    }

    @Override
    public List<String> allResourceNames() {
        String[] allIndices = new String[0];
        try {
            allIndices = EsUtil.getAllIndices(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(Arrays.asList(allIndices));
    }

    @Override
    public boolean resourceExists(String indexName) {
        try {
            return EsUtil.indexExists(indexName, client);
        } catch (IOException e) {
            Log.error("判断索引" + indexName + "是否存在发生异常 " + e.toString());
            return false;
        }
    }

    @Override
    public boolean createNewResource(String indexName) {
        try {
            EsUtil.createNewIndex(indexName, client);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (IOException e) {
            Log.error("ElasticSearch连接关闭失败");
        }
    }
}
