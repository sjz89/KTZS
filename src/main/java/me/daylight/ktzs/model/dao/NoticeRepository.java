package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/10 23:58
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    @Query(value = "select notice.id as id,notice.content as content,notice.create_time as time,course.name as courseName,user.name as teacherName " +
            "from notice,course,user where course.id=notice.course_id and user.id=course.teacher_id and notice.course_id in " +
            "(select course_id from course_students where students_id=?1) order by notice.create_time desc",nativeQuery = true)
    List<Object[]> findNoticesByStudent(Long studentId);

    @Query(value = "select * from notice where course_id=?1 order by create_time desc limit 1",nativeQuery = true)
    Notice getLatestByCourseId(Long id);

    @Query(value = "select notice.id as id,notice.content as content,notice.create_time as time,course.name as courseName,user.name as teacherName " +
            "from notice,course,user where course.id=notice.course_id and user.id=course.teacher_id and notice.id=?1 ",nativeQuery = true)
    List<Object[]> findNoticesById(Long id);

    @Query(value = "select notice.id as id,notice.content as content,notice.create_time as time,course.name as courseName,user.name as teacherName " +
            "from notice,course,user where course.id =?1 and user.id=course.teacher_id and notice.course_id=?1 order by notice.create_time desc",nativeQuery = true)
    List<Object[]> getNoticeDtoByCourse(Long courseId);
}
