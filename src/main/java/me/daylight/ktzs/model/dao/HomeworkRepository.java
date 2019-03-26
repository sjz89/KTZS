package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author daylight
 * @date 2019/03/23 20:15
 */
public interface HomeworkRepository extends JpaRepository<Homework,Long> {
    @Modifying
    @Transactional
    @Query(value = "insert into homework_files (homework_id, files_id) values (?1,?2);",nativeQuery = true)
    void insertFileToHomework(Long homeworkId,Long fileId);

    List<Homework> findAllByCourseOrderByCreateTime(Course course);

    @Query(value = "select * from homework where course_id=?1 order by create_time limit 1",nativeQuery = true)
    Homework findLatestOneByCourse(Long courseId);

    @Query(value = "select * from homework where course_id in " +
            "(select course_id from course_students where students_id=?1) " +
            "or course_id in (select course.id from course where course.teacher_id=?1) order by create_time",nativeQuery = true)
    List<Homework> findAllOfMe(Long userId);

    @Query(value = "select name,content,(select course.name from course where course.id=homework.course_id),end_time,create_time," +
            "(select count(homework_files.files_id) from homework_files where homework_files.homework_id=?1) from homework " +
            "where homework.id=?1",nativeQuery = true)
    List<Object[]> findHomeworkById(Long id);

    @Query(value = "select count(files_id) from homework_files where files_id in " +
            "(select id from upload_file where uploader_id=?1) and homework_id=?2",nativeQuery = true)
    int isStudentUploaded(Long userId,Long homeworkId);
}
