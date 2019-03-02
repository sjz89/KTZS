package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.Attendance;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;

import java.util.Collection;
import java.util.List;

/**
 * @author daylight
 * @date 2019/03/01 00:08
 */
public interface AttendanceService {
    Attendance save(Attendance attendance);

    void saveAll(Collection<Attendance> attendances);

    Page<Attendance> findByCourse(Course course,int page,int limit);

    List<Attendance> findByStudent(User student);
}
