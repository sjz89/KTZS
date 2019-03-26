package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.AttendanceRepository;
import me.daylight.ktzs.model.dao.UserRepository;
import me.daylight.ktzs.model.entity.Attendance;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.model.enums.AttendanceState;
import me.daylight.ktzs.service.AttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author daylight
 * @date 2019/03/01 00:08
 */
@Service
public class AttendanceServiceImpl implements AttendanceService {
    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

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
    public Page<Attendance> findAllByMajorPageable(Long userId, int page, int limit) {
        Pageable pageable=PageRequest.of(page-1,limit);
        List<User> students=userRepository.findStudents(userId);
        return attendanceRepository.findAttendancesByStudentInOrderByCreateTimeDesc(students,pageable);
    }

    @Override
    public int countByCourseAndStateAndUniqueId(Long courseId, int state, String uniqueId) {
        return attendanceRepository.countLatest(courseId, state,uniqueId);
    }

    @Override
    public Attendance getLatestByCourseId(Long id) {
        return attendanceRepository.getLatestTimeById(id);
    }

    @Override
    public List<Map<String, Object>> getRecordOfTeacher(Long userId) {
        List<Object[]> objects=attendanceRepository.getRecordOfTeacher(userId);
        List<Map<String, Object>> maps=new ArrayList<>();
        for (Object[] object:objects){
            Map<String,Object> map=new HashMap<>();
            map.put("count","出勤人数： "+object[0]+"/"+object[1]+"人");
            map.put("uniqueId",object[2]);
            map.put("courseName",object[3]);
            maps.add(map);
        }
        return maps;
    }

    @Override
    public List<Attendance> getRecordsByUniqueId(String uniqueId) {
        return attendanceRepository.getAttendancesByUniqueId(uniqueId);
    }

    @Override
    public int getAbsentCountOfToday() {
        return attendanceRepository.countAttendancesByUniqueIdLikeAndState(getStrOfToday(), AttendanceState.UNSIGNED.getCode());
    }

    @Override
    public int getAbsentCountOfTodayByCourseIn(List<Course> courses) {
        return attendanceRepository.countAttendancesByUniqueIdLikeAndStateAndCourseIn(getStrOfToday(),AttendanceState.UNSIGNED.getCode(),courses);
    }

    @Override
    public List<Map<String, Object>> getRecordOfToday() {
        List<Object[]> objects=attendanceRepository.getRecordOfToday(getStrOfToday());
        return transformMap(objects);
    }

    @Override
    public List<Map<String, Object>> getRecordOfTodayByMajorId(Long majorId) {
        return transformMap(attendanceRepository.getRecordOfTodayOfMajor(getStrOfToday(),majorId));
    }

    private List<Map<String,Object>> transformMap(List<Object[]> objects){
        List<Map<String, Object>> maps=new ArrayList<>();
        for (Object[] object:objects){
            Map<String,Object> map=new HashMap<>();
            map.put("count",object[0]);
            map.put("allCount",object[1]);
            map.put("uniqueId",object[2]);
            map.put("courseName",object[3]);
            map.put("teacherName",object[4]);
            maps.add(map);
        }
        return maps;
    }

    private String getStrOfToday(){
        Calendar calendar=Calendar.getInstance();
        int month=calendar.get(Calendar.MONTH)+1;
        int day=calendar.get(Calendar.DAY_OF_MONTH);
        return "%"+String.format("%02d",month)+String.format("%02d",day)+"%";
    }
}
