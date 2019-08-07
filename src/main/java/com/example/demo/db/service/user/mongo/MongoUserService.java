package com.example.demo.db.service.user.mongo;

import com.example.demo.db.data.User;
import com.example.demo.db.data.User1;
import com.example.demo.db.data.User2;
import com.mongodb.DBObject;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class UserImpl
 */
@Service
@Transactional
public class MongoUserService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MongoTemplate mongoTemplate;

    @Autowired
    public MongoUserService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Transactional
    public void save(Object obj) {
        mongoTemplate.save(obj);
    }

    public void save1(User1 user1) {
        mongoTemplate.save(user1);
    }

    public void save2(User2 user2) {
        mongoTemplate.save(user2);
    }

    @SuppressWarnings(value = "unchecked")
    public Map<String, Object> findById(Long id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, HashMap.class, "Users");
    }

    public User1 findUser1ById(Long id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, User1.class);
    }

    public User2 findUser2ById(Long id) {
        Query query = new Query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, User2.class);
    }

    public List<User> findUserByName(String name) {
        Query query = new Query(Criteria.where("details.name").is(name));
        return mongoTemplate.find(query, User.class);
    }

}
