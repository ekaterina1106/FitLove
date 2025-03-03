package com.example.fitlove.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Objects;

@Entity
@Table(name = "Instructors")
public class Instructors {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotBlank(message = "Name is required")
    @Size(max = 60, message = "Name must not exceed 60 characters")
    private String name;

    public Instructors() {
    }

    public Instructors(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Проверка на идентичность
        if (!(o instanceof Instructors)) return false; // Проверка на класс
        Instructors that = (Instructors) o; // Приведение типа
        return id == that.id && Objects.equals(name, that.name); // Сравнение по полям
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name); // Генерация хеша
    }

    @Override
    public String toString() {
        return "Instructors{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}'; // Читаемое представление объекта
    }
}
