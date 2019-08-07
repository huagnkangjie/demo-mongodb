package com.example.demo.db.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class User1
 */
@Document(collection = "Users")
@Data
public class User1 implements Serializable {
    private static final long serialVersionUID = -6677546954866463063L;
    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @Id
    private Long id;

    private String title;

    private String description;

    private String by;

    private String url;

    private Map<String, Object> details;
}
