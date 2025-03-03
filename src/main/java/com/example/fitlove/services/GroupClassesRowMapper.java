package com.example.fitlove.services;
import com.example.fitlove.models.GroupClasses;
import com.example.fitlove.models.Instructors;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GroupClassesRowMapper implements RowMapper<GroupClasses> {
    @Override
    public GroupClasses mapRow(ResultSet rs, int rowNum) throws SQLException {
        GroupClasses groupClass = new GroupClasses();
        groupClass.setId(rs.getInt("id"));
        groupClass.setName(rs.getString("name"));
        groupClass.setDescription(rs.getString("description"));
        groupClass.setClassDate(rs.getDate("class_date").toLocalDate());
        groupClass.setStartTime(rs.getTime("start_time").toLocalTime());

        // Создаем объект инструктора и устанавливаем имя
        Instructors instructor = new Instructors();
        instructor.setName(rs.getString("instructor_name"));
        groupClass.setInstructor(instructor);

        return groupClass;
    }
}
