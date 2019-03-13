package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Major;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/02/28 15:03
 */
public interface CourseService {
    Course saveCourse(Course course);

    boolean isCourseExist(Long id);

    void delCourse(Long courseId);

    Course findCourseById(Long id);

    Page<Course> getAllCourse(String semester,int page,int limit);

    List<Course> findCoursesByStudentAndSemester(String semester, Long studentId);

    List<Course> findCoursesByTeacherAndSemester(String semester, Long teacherId);

    List<Course> findCoursesByMajorAndSemester(String semester, Major major);
}
