package com.example.demo.db.service;

import com.example.demo.db.data.User;
import com.example.demo.db.data.UserEntity;
import com.example.demo.db.service.user.mongo.MongoUserService;
import com.example.demo.db.service.user.mysql.MysqlUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xuliduo
 * @date 2019/7/8
 * @description class TestServices
 */
@Service
public class TestService {
    private final MongoUserService mongoMongoUserService;
    private final MysqlUserService mysqlMysqlUserService;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    public TestService(MongoUserService mongoMongoUserService, MysqlUserService mysqlMysqlUserService, SequenceGeneratorService sequenceGeneratorService) {
        this.mongoMongoUserService = mongoMongoUserService;
        this.mysqlMysqlUserService = mysqlMysqlUserService;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void Error2SaveUser1() {
        UserEntity userEntity = new UserEntity();
        userEntity.setAge(199999);
        userEntity.setName("11111111111111");
        User user = new User();
        user.setTitle("TTTTTTTTTTTTT");

        mysqlMysqlUserService.insert(userEntity);
        mongoMongoUserService.save(user);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void Error2SaveUser2() {
        UserEntity userEntity = new UserEntity();
        userEntity.setAge(2222);
        userEntity.setName("2222");
        User user = new User();
        user.setId(sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME));
        user.setTitle("22222");

        mongoMongoUserService.save(user);
        mysqlMysqlUserService.insert(userEntity);
    }
}
