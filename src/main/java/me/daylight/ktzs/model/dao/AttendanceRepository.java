package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Attendance;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * @author daylight
 * @date 2019/03/01 00:07
 */
public interface AttendanceRepository extends JpaRepository<Attendance,Long> {
    Page<Attendance> findAttendancesByCourseOrderByCreateTimeDesc(Course course, Pageable pageable);

    Page<Attendance> findAttendancesByStudentOrderByCreateTimeDesc(User student,Pageable pageable);

    List<Attendance> findAttendancesByStudentOrderByCreateTimeDesc(User user);

    @Query("select a from Attendance a order by a.createTime desc ")
    Page<Attendance> findAllOrderByCreateTimeDesc(Pageable pageable);

    @Query(value = "select count(*) from attendance where course_id=?1 and state=?2 and unique_id like ?3",nativeQuery = true)
    int countLatest(Long courseId,int state,String uniqueId);

    @Query(value = "select * from attendance where course_id=?1 ORDER BY create_time DESC limit 1 ",nativeQuery =true)
    Attendance getLatestTimeById(Long courseId);

    @Modifying
    @Transactional
    void deleteAttendancesByCourse(Course course);

    @Query(value = "SELECT IFNULL(" +
            "(SELECT COUNT(unique_id) FROM attendance WHERE state = 1 and unique_id =p2.unique_id ),0) as count ," +
            " count(unique_id) as number , unique_id ,(select name from course where id=course_id) as name " +
            "FROM attendance p2 where course_id in (select course.id from course where teacher_id=?1)" +
            "GROUP BY unique_id,course_id order by unique_id desc",nativeQuery = true)
    List<Object[]> getRecordOfTeacher(Long userId);

    @Query(value = "SELECT IFNULL((SELECT COUNT(unique_id) FROM attendance WHERE state = 1 and unique_id =p2.unique_id ),0) as count ," +
            "count(unique_id) as number , unique_id ,(select name from course where id=course_id) as name," +
            "(select name from user where id = (select teacher_id from course where id=course_id) ) as teacherName" +
            " FROM attendance p2 where unique_id like ?1" +
            " GROUP BY unique_id,course_id order by unique_id desc",nativeQuery = true)
    List<Object[]> getRecordOfToday(String date);

    @Query(value = "SELECT IFNULL((SELECT COUNT(unique_id) FROM attendance WHERE state = 1 and unique_id =p2.unique_id ),0) as count ," +
            "count(unique_id) as number , unique_id ,(select name from course where id=course_id) as name," +
            "(select name from user where id = (select teacher_id from course where id=course_id) ) as teacherName" +
            " FROM attendance p2 where unique_id like ?1 and course_id in (select id from course where major_id=?2)" +
            " GROUP BY unique_id,course_id order by unique_id desc",nativeQuery = true)
    List<Object[]> getRecordOfTodayOfMajor(String date,Long majorId);

    List<Attendance> getAttendancesByUniqueId(String uniqueId);

    Page<Attendance> findAttendancesByStudentInOrderByCreateTimeDesc(Collection<User> students,Pageable pageable);

    int countAttendancesByUniqueIdLikeAndState(String uniqueId,int state);

    int countAttendancesByUniqueIdLikeAndStateAndCourseIn(String uniqueId,int state,Collection<Course> courses);
}
