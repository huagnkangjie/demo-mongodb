package com.example.demo.db.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class DatabaseSequence
 */
@Document(collection = "database_sequences")
@Data
public class DatabaseSequence {
    @Id
    private String id;

    private long seq;
}
