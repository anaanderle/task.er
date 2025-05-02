package com.uni.task.er.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "system_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    private LocalDate birthDate;

    private String cellphone;

    @Column(nullable = false, unique = true)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false)
    @NotBlank
    private String password;

    @NotNull
    private Boolean deleted = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<Task> tasks;

    public User(String name, LocalDate birthDate, String cellphone, String email, String password) {
        this.name = name;
        this.birthDate = birthDate;
        this.cellphone = cellphone;
        this.email = email;
        this.password = password;
    }
}
