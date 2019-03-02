package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Attendance;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author daylight
 * @date 2019/03/01 00:07
 */
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
    Page<Attendance> findAttendancesByCourse(Course course, Pageable pageable);

    List<Attendance> findAttendancesByStudent(User student);
}
