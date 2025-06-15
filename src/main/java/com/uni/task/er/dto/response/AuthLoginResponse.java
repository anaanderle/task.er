package com.uni.task.er.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthLoginResponse {
    private String token;
    private Long userId;
    private String userName; // Opcional: pode ser útil para o frontend
}
