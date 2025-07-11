package org.csu.knowledge.config;

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

@Slf4j
@Configuration
public class ESConfig {
    @Value("${csu.knowledge.es-ip}")
    private String esIP;
    @Value("${csu.knowledge.es-port}")
    private int esPort;
    @Value("${csu.knowledge.index-name}")
    private String indexName;

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
        // 1. 检查索引是否存在
        GetIndexRequest getIndexRequest = new GetIndexRequest(indexName);
        boolean exists = client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);

        // 2. 如果索引存在则直接返回
        if (exists) {
            /*DeleteIndexRequest deleteRequest = new DeleteIndexRequest(indexName);
            client.indices().delete(deleteRequest, RequestOptions.DEFAULT);
            log.info("已删除旧索引: {}", indexName);*/
            log.info("索引已存在: {}", indexName);
            return;
        }

        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0));

        String mappingJson = "{\n" +
                "  \"mappings\": {\n" +
                "    \"properties\": {\n" +
                "      \"kpId\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"title\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ik_max_word\",\n" +
                "        \"fields\": {\n" +
                "          \"keyword\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"ignore_above\": 256\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"content\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ik_max_word\"\n" +
                "      },\n" +
                "      \"authorId\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"status\": {\n" +
                "        \"type\": \"keyword\"\n" +
                "      },\n" +
                "      \"authorName\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ik_smart\",\n" +
                "        \"fields\": {\n" +
                "          \"keyword\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"ignore_above\": 256\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"tags\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"analyzer\": \"ik_smart\",\n" +
                "        \"fields\": {\n" +
                "          \"keyword\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"ignore_above\": 256\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"createdAt\": {\n" +
                "        \"type\": \"date\",\n" +
                "        \"format\": \"strict_date_optional_time||epoch_millis\"\n" +
                "      },\n" +
                "      \"updatedAt\": {\n" +
                "        \"type\": \"date\",\n" +
                "        \"format\": \"strict_date_optional_time||epoch_millis\"\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"settings\": {\n" +
                "    \"analysis\": {\n" +
                "      \"analyzer\": {\n" +
                "        \"ik_max_word\": {\n" +
                "          \"type\": \"custom\",\n" +
                "          \"tokenizer\": \"ik_max_word\"\n" +
                "        },\n" +
                "        \"ik_smart\": {\n" +
                "          \"type\": \"custom\",\n" +
                "          \"tokenizer\": \"ik_smart\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        request.source(mappingJson, XContentType.JSON);

        client.indices().create(request, RequestOptions.DEFAULT);
    }
}
