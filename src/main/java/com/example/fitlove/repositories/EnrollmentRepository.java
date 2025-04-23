package com.example.fitlove.repositories;

import com.example.fitlove.models.Enrollment;
import com.example.fitlove.models.Enrollments;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

}

