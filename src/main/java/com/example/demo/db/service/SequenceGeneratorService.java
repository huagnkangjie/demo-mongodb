package com.example.demo.db.service;

import com.example.demo.db.data.DatabaseSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class SequenceGeneratorService
 */
@Service
public class SequenceGeneratorService {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public SequenceGeneratorService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * 获得id序列
     *
     * @param seqName 队列名称
     * @return 最新的id
     */
    public long generateSequence(String seqName) {
        DatabaseSequence counter = mongoTemplate.findAndModify(query(where("_id").is(seqName)),
                new Update().inc("seq", 1), options().returnNew(true).upsert(true),
                DatabaseSequence.class);
        return !Objects.isNull(counter) ? counter.getSeq() : 1;
    }
}
