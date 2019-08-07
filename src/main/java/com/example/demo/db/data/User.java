package com.example.demo.db.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class User1
 */
@Document(collection = "Users")
@Data
public class User implements Serializable {
    private static final long serialVersionUID = -6677546954866463063L;
    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";
    @Transient
    private AtomicBoolean canWrite = new AtomicBoolean(false);

    @Id
    private Long id;

    private String title;

    private String description;

    private String by;

    private String url;

    private Map<String, Object> details;

    @DBRef
    private List<User> parents;

    /**
     * 添加扩展属性
     *
     * @param field field
     * @param value value
     * @return this
     */
    public User addDetails(String field, Object value) {
        if (details == null) {
            if (canWrite.compareAndSet(false, true)) {
                details = new HashMap<>();
            }
        }
        details.put(field, value);
        return this;
    }
}
