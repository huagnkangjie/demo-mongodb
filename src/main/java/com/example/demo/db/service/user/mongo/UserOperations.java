package com.example.demo.db.service.user.mongo;

import com.example.demo.db.data.User;
import com.mongodb.DBObject;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author xuliduo
 * @date 2019/7/24
 * @description class UserOperations
 */
public interface UserOperations {
    Page<User> findAllPage(Example<User> example, DBObject fields, Pageable pageable);
}
