package com.example.fitlove.services;

import com.example.fitlove.dto.GroupClassDTO;
import com.example.fitlove.models.GroupClasses;
import com.example.fitlove.models.Instructors;
import com.example.fitlove.repositories.GroupClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class GroupClassesService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final GroupClassesRepository groupClassesRepository;
    private final EnrollmentsService enrollmentsService;


    @Autowired
    public GroupClassesService(GroupClassesRepository groupClassesRepository, EnrollmentsService enrollmentsService, JdbcTemplate jdbcTemplate) {
        this.groupClassesRepository = groupClassesRepository;
        this.enrollmentsService = enrollmentsService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<GroupClasses> getAllGroupClasses() {
        String query = """
        SELECT gc.id, gc.name, gc.description, gc.class_date, gc.start_time, i.name AS instructor_name, 
               COUNT(e.client_id) AS enrollment_count
        FROM Group_Classes gc
        LEFT JOIN Enrollments e ON gc.id = e.class_id
        LEFT JOIN Instructors i ON gc.instructor_id = i.id
        WHERE gc.class_date >= CURRENT_DATE
        GROUP BY gc.id, gc.name, gc.description, gc.class_date, gc.start_time, i.name
        ORDER BY gc.class_date, gc.start_time;
    """;

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            GroupClasses groupClass = new GroupClasses();
            groupClass.setId(rs.getInt("id"));
            groupClass.setName(rs.getString("name"));
            groupClass.setDescription(rs.getString("description"));
            groupClass.setClassDate(rs.getDate("class_date").toLocalDate());
            groupClass.setStartTime(rs.getTime("start_time").toLocalTime());
            groupClass.setInstructor(new Instructors(rs.getString("instructor_name"))); // Обновите под свой класс Instructor
            groupClass.setEnrollmentCount(rs.getInt("enrollment_count"));
            return groupClass;
        });
    }


    public Optional<GroupClasses> getGroupClassById(int id) {
        return groupClassesRepository.findById(id);
    }

    @Transactional
    public GroupClasses saveGroupClass(GroupClasses groupClass) {
        return groupClassesRepository.save(groupClass);
    }

    @Transactional
    public GroupClasses updateGroupClass(GroupClasses groupClass) {
        return groupClassesRepository.findById(groupClass.getId())
                .map(existingGroupClass -> {
                    existingGroupClass.setName(groupClass.getName());
                    existingGroupClass.setDescription(groupClass.getDescription());
                    existingGroupClass.setClassDate(groupClass.getClassDate());
                    existingGroupClass.setStartTime(groupClass.getStartTime());
                    existingGroupClass.setInstructor(groupClass.getInstructor());
                    return groupClassesRepository.save(existingGroupClass);
                })
                .orElseThrow(() -> new RuntimeException("Group class not found with id " + groupClass.getId()));
    }

    @Transactional
    public void deleteGroupClass(int groupClassId) {
        // Удаляем связанные записи в таблице Enrollments
        enrollmentsService.deleteEnrollmentsByClassId(groupClassId);

        // Удаляем групповое занятие
        groupClassesRepository.deleteById(groupClassId);
    }

    // не для груп контроллер
    public List<LocalDate> getWeekDates() {
        // Возвращаем список дат на неделю начиная с сегодняшнего дня
        LocalDate startDate = LocalDate.now();
        return List.of(
                startDate,
                startDate.plusDays(1),
                startDate.plusDays(2),
                startDate.plusDays(3),
                startDate.plusDays(4),
                startDate.plusDays(5),
                startDate.plusDays(6)
        );
    }

    // не для груп контроллер
    public List<String> getWeekDays(List<LocalDate> weekDates) {
        Locale russianLocale = new Locale("ru"); // Устанавливаем локаль на русский
        return weekDates.stream()
                .map(date -> date.getDayOfWeek().getDisplayName(TextStyle.FULL, russianLocale).toLowerCase()) // Получаем название дня недели на русском в нижнем регистре
                .collect(Collectors.toList());
    }


    public List<LocalTime> getTimes() {
        // Временные интервалы занятий (пример)
        return List.of(
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0),
                LocalTime.of(12, 0),
                LocalTime.of(13, 0),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                LocalTime.of(16, 0)
        );
    }


    @Transactional(readOnly = true)
    public boolean isTimeSlotOccupied(LocalDate classDate, LocalTime startTime) {
        return groupClassesRepository.existsByClassDateAndStartTime(classDate, startTime);
    }


    public List<GroupClasses> getUpcomingClasses() {
        LocalDate today = LocalDate.now();
        return groupClassesRepository.findByClassDateAfterOrderByClassDateAsc(today);
    }






}






