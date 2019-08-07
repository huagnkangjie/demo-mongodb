package com.example.demo.db.service.user.mongo;

import com.example.demo.db.data.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class UserRepository
 */
public interface UserRepository extends MongoRepository<User, Long>, UserOperations {
    @Query("{ ?0 : ?1 }")
    List<User> findByDynamicField(String field, Object value);

    @Query("{ 'details.name' : ?0, 'details.password' : ?1 }")
    List<User> findByNameAndPassword(String name, String password);

    //@Query("{ 'title' : ?0 }")
    List<User> findByTitleLike(String name, Sort sort);

    /**
     * 分页查询
     * @param title
     * @param pageable
     * @return
     */
    Page<User> findByTitleLike(String title, Pageable pageable);


}
