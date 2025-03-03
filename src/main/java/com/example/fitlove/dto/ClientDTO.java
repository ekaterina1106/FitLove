package com.example.fitlove.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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


    // Конструкторы
    public ClientDTO() {
    }

    public ClientDTO(int id, String name, String email, String phone, String role, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
