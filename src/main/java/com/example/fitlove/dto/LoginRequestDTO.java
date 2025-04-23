package com.example.fitlove.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    @NotEmpty(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotEmpty(message = "Пароль обязателен")
    private String password;

    public LoginRequestDTO() {}

    public LoginRequestDTO( String email, String password) {
        this.email = email;
        this.password = password;
    }
}
