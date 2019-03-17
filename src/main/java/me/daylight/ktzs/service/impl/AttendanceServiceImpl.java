package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.AttendanceRepository;
import me.daylight.ktzs.model.entity.Attendance;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author daylight
 * @date 2019/03/01 00:08
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public Attendance save(Attendance attendance) {
        return attendanceRepository.saveAndFlush(attendance);
    }

    @Override
    public void saveAll(Collection<Attendance> attendances) {
        attendanceRepository.saveAll(attendances);
    }

    @Override
    public Page<Attendance> findByCourse(Course course,int page,int limit) {
        Pageable pageable= PageRequest.of(page-1,limit);
        return attendanceRepository.findAttendancesByCourseOrderByCreateTimeDesc(course,pageable);
    }

    @Override
    public Page<Attendance> findByStudentPageable(User student,int page,int limit) {
        Pageable pageable= PageRequest.of(page-1,limit);
        return attendanceRepository.findAttendancesByStudentOrderByCreateTimeDesc(student,pageable);
    }

    @Override
    public List<Attendance> findByStudent(User student) {
        return attendanceRepository.findAttendancesByStudentOrderByCreateTimeDesc(student);
    }

    @Override
    public Page<Attendance> findAllPageable(int page, int limit) {
        Pageable pageable= PageRequest.of(page-1,limit);
        return attendanceRepository.findAllOrderByCreateTimeDesc(pageable);
    }

    @Override
    public int countByCourseAndStateAndUniqueId(Long courseId, int state, String uniqueId) {
        return attendanceRepository.countLatest(courseId, state,uniqueId);
    }

    @Override
    public Attendance getLatestByCourseId(Long id) {
        return attendanceRepository.getLatestTimeById(id);
    }
}
