package com.task.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO{
    private String token;
    private ObjectId userId;
}
