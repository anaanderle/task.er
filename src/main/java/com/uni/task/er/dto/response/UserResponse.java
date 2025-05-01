package com.uni.task.er.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class UserResponse {
    private long id;
    private String name;
    private LocalDate birthday;
    private String cellphone;
    private String email;
}