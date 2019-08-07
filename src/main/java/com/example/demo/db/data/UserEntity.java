package com.example.demo.db.data;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author xuliduo
 * @date 06/03/2018
 * @description user
 */
@Data
@Table(name = "t_user")
public class UserEntity {
    @Column(name = "user_id")
    @GeneratedValue(generator = "JDBC")
    @Id
    public Long userId;

    @Column(name = "name")
    public String name;

    public String password;

    public String phone;

    public Integer age;

    public Integer gender;

    public String birthday;

    @Column(name = "last_change_user")
    public Long lastChangeUser;

    @Column(name = "create_time")
    public Date createTime;

    @Column(name = "update_time")
    public Date updateTime;
}
