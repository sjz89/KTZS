package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Attendance;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
