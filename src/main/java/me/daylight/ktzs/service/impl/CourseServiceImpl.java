package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.AttendanceRepository;
import me.daylight.ktzs.model.dao.CourseRepository;
import me.daylight.ktzs.model.dao.MajorRepository;
import me.daylight.ktzs.model.dao.UserRepository;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Major;
import me.daylight.ktzs.model.entity.User;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private AttendanceRepository attendanceRepository;

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
        Course course=courseRepository.getOne(courseId);
        attendanceRepository.deleteAttendancesByCourse(course);
        courseRepository.delete(course);
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
    public List<Course> getAllCourse() {
        return courseRepository.findAll();
    }

    @Override
    public List<Course> getCoursesByMajor(Long majorId) {
        return courseRepository.findCoursesByMajor(majorRepository.getOne(majorId));
    }

    @Override
    public List<User> findStudentUnChooseCourse(Long courseId) {
        return userRepository.findStudentUnChoose(courseId);
    }

    @Override
    public List<Course> findCoursesByStudentAndSemester(String semester, Long studentId) {
        return courseRepository.findCoursesByStudentAndSemester(studentId, semester);
    }

    @Override
    public List<Course> findCoursesByTeacherAndSemester(String semester, Long teacherId) {
        return courseRepository.findCoursesByTeacherAndSemester(teacherId,semester);
    }

    @Override
    public List<Course> findCoursesByMajorAndSemester(String semester, Major major) {
        return courseRepository.findCoursesBySemesterAndMajor(semester, major);
    }

    @Override
    public void delStudent(Long courseId, Long studentId) {
        courseRepository.deleteStudent(courseId, studentId);
    }

    @Override
    public List<Course> findCoursesBySemesterAndWeekDay(String semester, String weekDay) {
        return courseRepository.findAllBySemesterAndTimeLikeOrderByTime(semester,weekDay);
    }

    @Override
    public List<Course> findCoursesBySemesterAndWeekDayAndMajor(String semester, String weekDay, Major major) {
        return courseRepository.findAllBySemesterAndTimeLikeAndMajorOrderByTime(semester, weekDay, major);
    }
}
