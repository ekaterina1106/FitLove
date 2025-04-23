package com.example.fitlove.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDTO {
    private int id;

    @NotBlank(message = "Имя обязательно")
    @Size(max = 60, message = "Имя не должно превышать 60 символов")
    private String name;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Телефон обязателен")
    private String phone;
    private final boolean blocked;

    private boolean isBlocked;

    public ClientDTO(int id, String name, String email, String phone, boolean blocked) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.blocked = blocked;
    }



}
