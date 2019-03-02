package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.CourseRepository;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Major;
import me.daylight.ktzs.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/02/28 15:03
 */
@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseRepository courseRepository;

    @Override
    public Course saveCourse(Course course) {
        return courseRepository.saveAndFlush(course);
    }

    @Override
    public boolean isCourseExist(Long id) {
        return courseRepository.existsCourseById(id);
    }

    @Override
    public void delCourse(Long courseId) {
        courseRepository.delete(courseRepository.getOne(courseId));
    }

    @Override
    public Course findCourseById(Long id) {
        return courseRepository.getOne(id);
    }

    @Override
    public Page<Course> getAllCourse(String semester,int page, int limit) {
        Pageable pageable= PageRequest.of(page-1,limit);
        return courseRepository.findCoursesBySemester(semester,pageable);
    }

    @Override
    public List<Course> findCoursesByUserIdAndSemester(String semester, Long userId) {
        return courseRepository.findCoursesByUserIdAndSemester(userId, semester);
    }

    @Override
    public List<Course> findCoursesByMajorAndSemester(String semester, Major major) {
        return courseRepository.findCoursesBySemesterAndMajor(semester, major);
    }
}
