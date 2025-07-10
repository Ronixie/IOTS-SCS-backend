package org.csu.exam.repository;

import org.bson.types.ObjectId;
import org.csu.exam.entity.po.TestPapers;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 试卷数据访问层接口
 * 继承MongoRepository提供基础的CRUD操作
 */
@Repository
public interface TestPapersRepository extends MongoRepository<TestPapers, String> {
    /**
     * 根据问题id判断问题是否存在
     *
     * @param paperId 试卷id
     * @param questionId 问题id
     * @return 是否存在
     */
    @Query(value = "{ '_id': ?0,'questions.questionId' : ?1 }")
    TestPapers existQuestion(ObjectId paperId, String questionId);

    @Query(value = "{ '_id': ?0}")
    TestPapers findPaperById(String paperId);

    @Query(value = "{ 'courseId': ?0}")
    List<TestPapers> getTestPapersByCourseId(Long courseId);
}
