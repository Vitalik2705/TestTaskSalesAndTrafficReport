package com.task.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document
@Data
public class Role {
    private @MongoId ObjectId id;
    private String name;
}
