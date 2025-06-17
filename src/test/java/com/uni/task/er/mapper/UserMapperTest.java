package com.uni.task.er.mapper;

import com.uni.task.er.dto.request.UserCreateRequest;
import com.uni.task.er.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserMapperTest {

    @Test
    void userMapper_shouldConvertCreateRequestToUser() {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("João");
        request.setBirthday(LocalDate.of(1990, 1, 1));
        request.setCellphone("11999999999");
        request.setEmail("joao@test.com");
        request.setPassword("password123");

        User user = UserMapper.toModel(request);

        assertThat(user.getName()).isEqualTo("João");
        assertThat(user.getBirthday()).isEqualTo(LocalDate.of(1990, 1, 1));
        assertThat(user.getCellphone()).isEqualTo("11999999999");
        assertThat(user.getEmail()).isEqualTo("joao@test.com");
        assertThat(user.getPassword()).isEqualTo("password123");
    }
}