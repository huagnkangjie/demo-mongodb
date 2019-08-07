package com.example.demo.db.service.user.mysql;

import com.example.demo.mapper.UserMapper;
import com.example.demo.db.data.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author xuliduo
 * @date 16/03/2018
 * @description class 用户操作服务
 */
@Service
@Transactional
public class MysqlUserService {

    private UserMapper mapper;

    @Autowired
    public MysqlUserService(UserMapper userMapper) {
        this.mapper = userMapper;
    }

    /**
     * 插入方法
     *
     * @param user 需要插入的对象
     * @return 包含id的user
     */
    @Transactional
    public UserEntity insert(UserEntity user) {
        this.mapper.insert(user);
        return user;
    }

    /**
     * 通过id查询
     *
     * @param id userId
     * @return user or null
     */
    public UserEntity getOne(long id) {
        return this.mapper.selectByPrimaryKey(id);
    }

    /**
     * 通过主键更新数据
     *
     * @param userEntity 需要更新的对象
     * @return 发生变化的列
     */
    public int update(UserEntity userEntity) {
        // 使用 updateByPrimaryKeySelective 不会更新null
        // 如果要强制更新 null ，那么使用updateByPrimaryKey
        return this.mapper.updateByPrimaryKeySelective(userEntity);
    }

}
