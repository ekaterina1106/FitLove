package com.example.fitlove.services;

import com.example.fitlove.models.GroupClasses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class EnrollmentsService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public EnrollmentsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Value("${class.max-capacity}") // Загружаем значение из конфигурации
    private int maxCapacity;


    public void enrollClient(int clientId, int classId) {
        // Получаем текущее количество записавшихся на занятие
        String countQuery = "SELECT COUNT(*) FROM Enrollments WHERE class_id = ?";
        Integer enrollmentCount = jdbcTemplate.queryForObject(countQuery, Integer.class, classId);

        // Проверяем, есть ли свободные места
        if (enrollmentCount != null && enrollmentCount >= maxCapacity) {
            throw new IllegalStateException("Все места на занятие заняты.");
        }

        // Проверяем, не записан ли уже клиент
        String checkSql = "SELECT COUNT(*) FROM Enrollments WHERE client_id = ? AND class_id = ?";
        Integer clientEnrollmentCount = jdbcTemplate.queryForObject(checkSql, Integer.class, clientId, classId);

        if (clientEnrollmentCount != null && clientEnrollmentCount > 0) {
            throw new IllegalStateException("Клиент уже записан на это занятие.");
        }

        // Записываем клиента
        String insertSql = "INSERT INTO Enrollments (client_id, class_id) VALUES (?, ?)";
        jdbcTemplate.update(insertSql, clientId, classId);
    }


    public int getEnrollmentCountForClass(int classId) {
        String query = "SELECT COUNT(*) FROM Enrollments WHERE class_id = ?";
        return jdbcTemplate.queryForObject(query, Integer.class, classId);
    }


//    public void enrollClient(int clientId, int classId) {
//        // Проверяем, существует ли уже такая запись
//        String checkSql = "SELECT COUNT(*) FROM Enrollments WHERE client_id = ? AND class_id = ?";
//        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, clientId, classId);
//
//        if (count != null && count == 0) {
//            // Если записи нет, добавляем новую
//            String insertSql = "INSERT INTO Enrollments (client_id, class_id) VALUES (?, ?)";
//            jdbcTemplate.update(insertSql, clientId, classId);
//        } else {
//            throw new IllegalStateException("Client is already enrolled in this class.");
//        }
//    }
//

    public void cancelEnrollment(int classId, int clientId) {
        String sql = "DELETE FROM Enrollments WHERE class_id = ? AND client_id = ?";
        jdbcTemplate.update(sql, classId, clientId);
    }


    //ДОБАВИЛА ЭТО
    public List<GroupClasses> getEnrollmentsByClientId(int clientId) {
        String sql = "SELECT gc.*, i.name AS instructor_name FROM Group_Classes gc " +
                "JOIN Enrollments e ON gc.id = e.class_id " +
                "JOIN Instructors i ON gc.instructor_id = i.id " +
                "WHERE e.client_id = ?";

        return jdbcTemplate.query(sql, new GroupClassesRowMapper(), clientId);
    }


    public List<GroupClasses> getUpcomingEnrollmentsByClientId(int clientId) {
        String sql = "SELECT gc.*, i.name AS instructor_name FROM Group_Classes gc " +
                "JOIN Enrollments e ON gc.id = e.class_id " +
                "JOIN Instructors i ON gc.instructor_id = i.id " +
                "WHERE e.client_id = ? AND gc.class_date >= CURRENT_DATE";

        return jdbcTemplate.query(sql, new GroupClassesRowMapper(), clientId);
    }

    public List<GroupClasses> getPastEnrollmentsByClientId(int clientId) {
        String sql = "SELECT gc.*, i.name AS instructor_name FROM Group_Classes gc " +
                "JOIN Enrollments e ON gc.id = e.class_id " +
                "JOIN Instructors i ON gc.instructor_id = i.id " +
                "WHERE e.client_id = ? AND gc.class_date < CURRENT_DATE";

        return jdbcTemplate.query(sql, new GroupClassesRowMapper(), clientId);
    }


    public List<GroupClasses> getUpcomingClassesWithEnrollmentCount() {
        String query = """
        SELECT gc.id, gc.name, gc.class_date, gc.start_time, COUNT(e.client_id) AS enrollment_count
        FROM Group_Classes gc
        LEFT JOIN Enrollments e ON gc.id = e.class_id
        WHERE gc.class_date >= CURRENT_DATE
        GROUP BY gc.id, gc.name, gc.class_date, gc.start_time
        ORDER BY gc.class_date, gc.start_time;
    """;

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            GroupClasses groupClass = new GroupClasses();
            groupClass.setId(rs.getInt("id"));
            groupClass.setName(rs.getString("name"));
            groupClass.setClassDate(rs.getDate("class_date").toLocalDate());
            groupClass.setStartTime(rs.getTime("start_time").toLocalTime());
            groupClass.setEnrollmentCount(rs.getInt("enrollment_count"));
            return groupClass;
        });
    }

    public List<GroupClasses> getPastClassesWithEnrollmentCount() {
        String query = """
        SELECT gc.id, gc.name, gc.class_date, gc.start_time, COUNT(e.client_id) AS enrollment_count
        FROM Group_Classes gc
        LEFT JOIN Enrollments e ON gc.id = e.class_id
        WHERE gc.class_date < CURRENT_DATE
        GROUP BY gc.id, gc.name, gc.class_date, gc.start_time
        ORDER BY gc.class_date DESC, gc.start_time DESC;
    """;

        return jdbcTemplate.query(query, (rs, rowNum) -> {
            GroupClasses groupClass = new GroupClasses();
            groupClass.setId(rs.getInt("id"));
            groupClass.setName(rs.getString("name"));
            groupClass.setClassDate(rs.getDate("class_date").toLocalDate());
            groupClass.setStartTime(rs.getTime("start_time").toLocalTime());
            groupClass.setEnrollmentCount(rs.getInt("enrollment_count"));
            return groupClass;
        });
    }

    public List<Integer> getClientEnrollments(int clientId) {
        String sql = "SELECT class_id FROM Enrollments WHERE client_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, clientId);
    }


    public void deleteEnrollmentsByClassId(int classId) {
        String sql = "DELETE FROM Enrollments WHERE class_id = ?";
        jdbcTemplate.update(sql, classId);
    }



}