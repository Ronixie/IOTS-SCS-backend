package org.csu.ai.repository;

import org.csu.ai.entity.po.ChatMemory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatMemoryRepository extends MongoRepository<ChatMemory, String> {
    @Query(value = "{ 'sessionId' : ?0}", sort = "{ 'createdAt' : 1}")
    List<ChatMemory> findBySessionIdOrderByCreatedAtAsc(String sessionId);

    @Query(value = "{ 'sessionId' : ?0}", delete = true)
    long deleteBySessionId(String conversationId);

    @Query(value = "{ 'sessionId' : ?0}", sort = "{ 'createdAt' : 1}")
    ChatMemory findFirstBySessionId(String sessionId);
}
