package com.example.fitlove.repositories;

import com.example.fitlove.models.Instructors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructorsRepository extends JpaRepository<Instructors, Integer> {

}
