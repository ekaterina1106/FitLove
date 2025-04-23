package com.example.fitlove.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "Instructors")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Instructors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "is_blocked")
    private boolean isBlocked = false;

    public Instructors(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Instructors(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Instructors{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}'; // Читаемое представление объекта
    }
}

