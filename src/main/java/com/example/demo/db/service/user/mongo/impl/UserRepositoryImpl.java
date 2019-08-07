package com.example.demo.db.service.user.mongo.impl;

import com.example.demo.db.data.User;
import com.example.demo.db.service.user.mongo.UserOperations;
import com.mongodb.DBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author xuliduo
 * @date 2019/7/24
 * @description class UserRepositoryImpl
 */
@Service
public class UserRepositoryImpl implements UserOperations {
    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Page<User> findAllPage(Example<User> example, DBObject fields, Pageable pageable) {
        Query query = new BasicQuery(fields);
        query.addCriteria((new Criteria()).alike(example));
        query.with(pageable);
        List<User> list = mongoTemplate.find(query, example.getProbeType());
        return PageableExecutionUtils.getPage(list, pageable, () -> mongoTemplate.count(query, example.getProbeType()));
    }
}
