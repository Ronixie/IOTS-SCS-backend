package org.csu.ai.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csu.ai.service.DocumentService;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final RestHighLevelClient esClient;
    private final EmbeddingModel embeddingModel; // 使用 Spring AI提供的嵌入模型

    /**
     * 将文本转为向量写入 Elasticsearch
     *
     * @param documents 文档
     * @throws IOException 写入es时的异常
     */
    public void indexDocuments(List<String> documents, String chatId) throws IOException {
        int batchSize = 20; // 可根据实际情况调整
        for (int i = 0; i < documents.size(); i += batchSize) {
            int end = Math.min(i + batchSize, documents.size());
            List<String> batch = documents.subList(i, end);

            BulkRequest bulkRequest = new BulkRequest();
            for (String doc : batch) {
                float[] vector = embeddingModel.embed(doc);
                IndexRequest request = new IndexRequest("rag_documents");
                request.source(XContentType.JSON,
                        "text", doc,
                        "vector", vector,
                        "session_id", chatId);
                bulkRequest.add(request);
            }
            esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            // 帮助GC，主动断开batch引用
            batch = null;
        }
    }

    /**
     * 搜索相似文档
     *
     * @param query 查询
     * @return
     * @throws IOException
     */
    public List<String> searchSimilarDocs(String query, String chatId) throws IOException {
        float[] queryVector = embeddingModel.embed(query);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery("session_id", chatId))
                .query(QueryBuilders.scriptScoreQuery(
                        QueryBuilders.matchAllQuery(),
                        new Script(ScriptType.INLINE, "painless",
                                "cosineSimilarity(params.query_vector, 'vector') + 1.0",
                                Map.of("query_vector", queryVector))
                ));
        sourceBuilder.size(3); // 返回最相关的3个文档

        SearchRequest searchRequest = new SearchRequest("rag_documents");
        searchRequest.source(sourceBuilder);
        SearchResponse response = esClient.search(searchRequest, RequestOptions.DEFAULT);

        return Arrays.stream(response.getHits().getHits())
                .map(s -> {
                    Map<String, Object> sourceAsMap = s.getSourceAsMap();
                    return (String) sourceAsMap.get("text");
                })
                .collect(Collectors.toList());
    }

    /**
     * 单条文档写入ES
     */
    public void indexSingleDocument(String doc, String chatId) throws IOException {
        float[] vector = embeddingModel.embed(doc);
        IndexRequest request = new IndexRequest("rag_documents");
        request.source(XContentType.JSON,
                "text", doc,
                "vector", vector,
                "session_id", chatId);
        esClient.index(request, RequestOptions.DEFAULT);
    }
}
