package com.example.demo.db.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author xuliduo
 * @date 2019/7/3
 * @description class User2
 */
@Document(collection = "Users")
@Data
public class User2 implements Serializable {
    private static final long serialVersionUID = -5074758278722228160L;
    @Id
    private Long id;

    private String title;

    private String description;

    private String by;

    private String url;

    private String headImg;
}
