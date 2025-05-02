package com.uni.task.er.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequest {
    private String name;
    private LocalDate birthDate;
    private String cellphone;
    private String email;
}