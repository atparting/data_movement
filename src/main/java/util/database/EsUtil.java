package util.database;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class EsUtil {

    /**
     * 连接并返回一个新的es客户端
     */
    public static RestHighLevelClient connectEsClient(String[] hosts) {
        HttpHost[] httpHosts = new HttpHost[hosts.length];
        for (int i = 0; i < hosts.length; i++) {
            String[] split = hosts[i].split(":");
            httpHosts[i] = new HttpHost(split[0], Integer.parseInt(split[1]), "http");
        }
        return new RestHighLevelClient(RestClient.builder(httpHosts));
    }

    /**
     * 检测索引是否存在
     */
    public static boolean indexExists(String indexName, RestHighLevelClient client) throws IOException {
        return client.indices().exists(new GetIndexRequest(indexName), RequestOptions.DEFAULT);
    }

    /**
     * 创建新索引 若存在则覆盖
     */
    public static void createNewIndex(String indexName, RestHighLevelClient client) throws IOException {
        if (indexExists(indexName, client)) {
            client.indices().delete(new DeleteIndexRequest(indexName), RequestOptions.DEFAULT);
        }
        client.indices().create(new CreateIndexRequest(indexName), RequestOptions.DEFAULT);
    }

    /**
     * 获得所有索引名称
     */
    public static String[] getAllIndices(RestHighLevelClient client) throws IOException {
        return client.indices().get(new GetIndexRequest("*"), RequestOptions.DEFAULT).getIndices();
    }

    /**
     * 滚动搜索
     */
    public static SearchResponse searchScroll(
            String indexName, String scrollId, int batchNum, RestHighLevelClient client) throws IOException {
        return scrollId == null ? search(indexName, batchNum, client) : searchScroll(scrollId, client);
    }

    /**
     * 滚动搜索 首次搜索
     */
    private static SearchResponse search(String indexName, int batchNum, RestHighLevelClient client) throws IOException {
        SearchRequest searchRequest = new SearchRequest(indexName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(batchNum);
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        searchRequest.source(searchSourceBuilder);
        searchRequest.scroll(TimeValue.timeValueMinutes(1));
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * 滚动搜索 非首次搜索
     */
    private static SearchResponse searchScroll(String scrollId, RestHighLevelClient client) throws IOException {
        SearchScrollRequest request = new SearchScrollRequest(scrollId);
        Scroll scroll = new Scroll(TimeValue.timeValueMinutes(1));
        request.scroll(scroll);
        return client.scroll(request, RequestOptions.DEFAULT);
    }
}
