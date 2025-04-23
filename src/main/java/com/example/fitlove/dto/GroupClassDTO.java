package com.example.fitlove.dto;

import com.example.fitlove.models.GroupClasses;
import com.example.fitlove.models.Instructors;
import com.example.fitlove.services.InstructorsService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GroupClassDTO {
    private int id;

    @NotBlank(message = "Имя обязательно")
    @Size(max = 60, message = "Имя не должно превышать 60 символов")
    private String name;

    private String description;

    @NotNull(message = "Дата занятия обязательна")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate classDate;

    @NotNull(message = "Время начала обязательно")
    private LocalTime startTime;

    @NotNull(message = "Инструктор обязателен")
    private int instructorId;

    private int enrollmentCount;

    public GroupClassDTO(GroupClasses groupClass) {
        this.id = groupClass.getId();
        this.name = groupClass.getName();
        this.description = groupClass.getDescription();
        this.classDate = groupClass.getClassDate();
        this.startTime = groupClass.getStartTime();
        this.instructorId = groupClass.getInstructor().getId();
        this.enrollmentCount = groupClass.getClients().size();
    }
}
