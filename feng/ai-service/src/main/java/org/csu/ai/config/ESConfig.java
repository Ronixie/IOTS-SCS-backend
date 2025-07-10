package org.csu.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class ESConfig {
    @Value("${csu.ai.es-ip}")
    private String esIP;
    @Value("${csu.ai.es-port}")
    private int esPort;

    @Bean
    public RestHighLevelClient esRestClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(esIP, esPort, "http")));
        try {
            createIndex(client);
        } catch (IOException e) {
            log.error("创建索引失败", e);
        }
        return client;
    }

    private void createIndex(RestHighLevelClient client) throws IOException {
        String indexName = "rag_documents";
        // 1. 检查索引是否存在
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

        // 2. 如果索引存在则直接返回
        if (exists) {
            DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
            client.indices().delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("已删除旧索引: {}", indexName);
            //log.info("索引已存在: {}", indexName);
        }

        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0));

        String mappingJson = "{" +
                "\"mappings\": {" +
                "\"properties\": {" +
                "\"text\": { \"type\": \"text\" }," +
                "\"vector\": { \"type\": \"dense_vector\", \"dims\": 1536 }," +
                "\"session_id\": { \"type\": \"keyword\" }" +
                "}" +
                "}" +
                "}";

        request.source(mappingJson, XContentType.JSON);

        client.indices().create(request, RequestOptions.DEFAULT);
    }

}
