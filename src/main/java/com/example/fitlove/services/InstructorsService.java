package com.example.fitlove.services;

import com.example.fitlove.models.Instructors;
import com.example.fitlove.repositories.InstructorsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.fitlove.repositories.GroupClassesRepository;

import java.util.List;
import java.util.Optional;

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
    public void deleteInstructor(int instructorId) {
        // Удаление записей из Enrollments
        String deleteEnrollmentsQuery =
                "DELETE FROM Enrollments WHERE class_id IN (SELECT id FROM Group_Classes WHERE instructor_id = ?)";
        jdbcTemplate.update(deleteEnrollmentsQuery, instructorId);

        // Удаление связанных занятий
        String deleteClassesQuery = "DELETE FROM Group_Classes WHERE instructor_id = ?";
        jdbcTemplate.update(deleteClassesQuery, instructorId);

        // Удаление инструктора
        instructorsRepository.deleteById(instructorId);
    }
}
