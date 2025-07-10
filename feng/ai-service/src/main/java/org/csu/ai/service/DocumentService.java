package org.csu.ai.service;

import java.io.IOException;
import java.util.List;

public interface DocumentService {
    /**
     * 将文档转换为es中的索引
     * @param documents 文档
     */
    void indexDocuments(List<String> documents,String chatId) throws IOException;

    /**
     * 搜索相似文档
     * @param query 查询
     * @return
     * @throws IOException
     */
    List<String> searchSimilarDocs(String query,String chatId) throws IOException;

    void indexSingleDocument(String doc, String chatId) throws IOException;
}
