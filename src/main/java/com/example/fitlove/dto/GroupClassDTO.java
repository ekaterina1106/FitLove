package com.example.fitlove.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class GroupClassDTO {
    private int id;

    @NotBlank(message = "Имя обязательно")
    @Size(max = 60, message = "Имя не должно превышать 60 символов")
    private String name;

    private String description;

    @NotNull(message = "Дата занятия обязательна")
    @DateTimeFormat(pattern = "yyyy-MM-dd") // Правильный формат даты
    private LocalDate classDate;

    private DayOfWeek dayOfWeek;

    @NotNull(message = "Время начала обязательно")
    private LocalTime startTime;

    private int instructorId; // Можно хранить только ID инструктора

    // Конструкторы
    public GroupClassDTO() {
    }

    public GroupClassDTO(int id, String name, String description, LocalDate classDate,
                         LocalTime startTime, int instructorId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.classDate = classDate;
        this.startTime = startTime;
        this.instructorId = instructorId;
        this.dayOfWeek = classDate != null ? classDate.getDayOfWeek() : null;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getClassDate() {
        return classDate;
    }

    public void setClassDate(LocalDate classDate) {
        this.classDate = classDate;
        this.dayOfWeek = classDate != null ? classDate.getDayOfWeek() : null;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }
}
