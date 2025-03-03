package com.example.fitlove.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "Group_Classes")
public class GroupClasses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    @NotBlank(message = "Имя обязательно")
    @Size(max = 60, message = "Имя не должно превышать 60 символов")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "class_date")
    @NotNull(message = "Дата занятия обязательна")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate classDate;

    @Transient
    @Column(name = "day_of_week")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time")
    @NotNull(message = "Время начала обязательно")
    private LocalTime startTime;

    @ManyToOne
    @JoinColumn(name = "instructor_id", nullable = false)
    private Instructors instructor;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "Enrollments",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName="id"),
            inverseJoinColumns = @JoinColumn(name = "class_id", referencedColumnName="id"))
    private Set<Clients> clients = new HashSet<>();

    public GroupClasses() {
    }

    public GroupClasses(String name, String description, LocalDate classDate, LocalTime startTime, Instructors instructor) {
        this.name = name;
        this.description = description;
        this.classDate = classDate;
        this.dayOfWeek = classDate.getDayOfWeek();
        this.startTime = startTime;
        this.instructor = instructor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // проверка на идентичность
        if (!(o instanceof GroupClasses)) return false; // проверка на класс
        GroupClasses that = (GroupClasses) o; // приведение типа
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(classDate, that.classDate) &&
                Objects.equals(instructor, that.instructor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, classDate, instructor); // уникальный хеш
    }




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
        this.dayOfWeek = classDate.getDayOfWeek();
    }

//    public DayOfWeek getDayOfWeek() {
//        return dayOfWeek;
//    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Instructors getInstructor() {
        return instructor;
    }

    public void setInstructor(Instructors instructor) {
        this.instructor = instructor;
    }

    public Set<Clients> getClients() {
        return clients;
    }

    public void setClients(Set<Clients> clients) {
        this.clients = clients;
    }

    @Override
    public String toString() {
        return "GroupClasses{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", classDate=" + classDate +
                ", dayOfWeek=" + dayOfWeek +
                ", startTime=" + startTime +
                ", instructor=" + instructor +
                ", clients=" + clients +
                '}';
    }


    @Transient
    private int enrollmentCount; // новое поле для количества записей

    // геттер и сеттер для enrollmentCount
    public int getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(int enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public DayOfWeek getDayOfWeek() {
        return classDate.getDayOfWeek(); // Возвращаем день недели на основе classDate
    }


}
