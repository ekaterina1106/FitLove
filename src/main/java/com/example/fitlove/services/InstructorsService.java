package com.example.fitlove.services;

import com.example.fitlove.dto.InstructorDTO;
import com.example.fitlove.models.Instructors;
import com.example.fitlove.repositories.InstructorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fitlove.repositories.GroupClassesRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class InstructorsService {

    private final JdbcTemplate jdbcTemplate;
    private final InstructorsRepository instructorsRepository;
    private final GroupClassesRepository groupClassesRepository;

    @Autowired
    public InstructorsService(JdbcTemplate jdbcTemplate,
                              InstructorsRepository instructorsRepository,
                              GroupClassesRepository groupClassesRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.instructorsRepository = instructorsRepository;
        this.groupClassesRepository = groupClassesRepository;
    }

    public List<Instructors> getAllInstructors() {
        return instructorsRepository.findAll();
    }

    public Optional<Instructors> getInstructorById(int id) {
        return instructorsRepository.findById(id);
    }

    public List<InstructorDTO> getAllInstructorDTOs() {
        return getAllInstructors().stream()
                .map(instructor -> new InstructorDTO(instructor.getId(), instructor.getName(), instructor.isBlocked()))
                .collect(Collectors.toList());
    }


    @Transactional
    public Instructors saveInstructor(Instructors instructor) {
        return instructorsRepository.save(instructor);
    }

    @Transactional
    public Instructors updateInstructor(Instructors instructor) {
        return instructorsRepository.findById(instructor.getId())
                .map(existingInstructor -> {
                    existingInstructor.setName(instructor.getName());
                    // Здесь можно обновить и другие поля
                    return instructorsRepository.save(existingInstructor);
                })
                .orElseThrow(() -> new RuntimeException("Instructor not found with id " + instructor.getId()));
    }

    @Transactional
    public void blockInstructor(int instructorId) {
        // Находим инструктора по ID
        Instructors instructor = instructorsRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id " + instructorId));

        // Помечаем инструктора как заблокированного
        instructor.setBlocked(true);
        instructorsRepository.save(instructor); // Сохраняем изменения в базе

        // Удаление будущих занятий (с датой больше текущей)
        String deleteFutureClassesQuery =
                "DELETE FROM Group_Classes WHERE instructor_id = ? AND class_date > CURRENT_DATE";
        jdbcTemplate.update(deleteFutureClassesQuery, instructorId);

        // Удаление записей о записях клиентов на будущие занятия
        String deleteFutureEnrollmentsQuery =
                "DELETE FROM Enrollments WHERE class_id IN (SELECT id FROM Group_Classes WHERE instructor_id = ? AND class_date > CURRENT_DATE)";
        jdbcTemplate.update(deleteFutureEnrollmentsQuery, instructorId);
    }

    @Transactional
    public void unblockInstructor(int instructorId) {
        Instructors instructor = instructorsRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id " + instructorId));

        instructor.setBlocked(false);  // Помечаем инструктора как разблокированного
        instructorsRepository.save(instructor); // Сохраняем изменения в базе
    }

    @Transactional
    public void deleteInstructor(int instructorId) {
        Instructors instructor = instructorsRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found with id " + instructorId));

        // Удаляем записи клиентов на занятия, связанные с инструктором
        String deleteEnrollmentsQuery =
                "DELETE FROM Enrollments WHERE class_id IN (SELECT id FROM Group_Classes WHERE instructor_id = ?)";
        jdbcTemplate.update(deleteEnrollmentsQuery, instructorId);

        // Удаляем все связанные занятия
        String deleteFutureClassesQuery =
                "DELETE FROM Group_Classes WHERE instructor_id = ?";
        jdbcTemplate.update(deleteFutureClassesQuery, instructorId);

        // Удаляем инструктора
        instructorsRepository.delete(instructor);  // Удаляем инструктора из базы данных
    }


//    @Transactional
//    public void deleteInstructor(int instructorId) {
//        // Удаление записей из Enrollments
//        String deleteEnrollmentsQuery =
//                "DELETE FROM Enrollments WHERE class_id IN (SELECT id FROM Group_Classes WHERE instructor_id = ?)";
//        jdbcTemplate.update(deleteEnrollmentsQuery, instructorId);
//
//        // Удаление связанных занятий
//        String deleteClassesQuery = "DELETE FROM Group_Classes WHERE instructor_id = ?";
//        jdbcTemplate.update(deleteClassesQuery, instructorId);
//
//        // Удаление инструктора
//        instructorsRepository.deleteById(instructorId);
//    }
}
