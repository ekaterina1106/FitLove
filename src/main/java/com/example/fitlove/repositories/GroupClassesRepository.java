package com.example.fitlove.repositories;

import com.example.fitlove.models.GroupClasses;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface GroupClassesRepository extends JpaRepository<GroupClasses, Integer> {

    boolean existsByClassDateAndStartTime(LocalDate classDate, LocalTime startTime);

    List<GroupClasses> findByClassDateAfterOrderByClassDateAsc(LocalDate date);

    void deleteByInstructorId(int instructorId);

    //    @Modifying
//    @Transactional
//    @Query("DELETE FROM GroupClasses gc WHERE gc.instructor.id = :instructorId")
//    void deleteByInstructorId(@Param("instructorId") int instructorId);
}
