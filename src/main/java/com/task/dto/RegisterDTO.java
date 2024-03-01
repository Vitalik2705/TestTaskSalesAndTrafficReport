package com.task.dto;

import com.task.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    private @MongoId ObjectId id;
    private String username;
    private String password;
    private Set<Role> roles;
}
