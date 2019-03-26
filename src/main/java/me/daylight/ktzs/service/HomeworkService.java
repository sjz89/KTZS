package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Homework;
import me.daylight.ktzs.model.entity.UploadFile;

import java.util.List;
import java.util.Map;

/**
 * @author daylight
 * @date 2019/03/23 20:14
 */
public interface HomeworkService {
    boolean isHomeworkExist(Long homeworkId);

    Homework save(Homework homework);

    Map<String,Object> findById(Long id);

    void addFileToHomework(Long homeworkId,Long fileId);

    List<Homework> findHomeworkByCourse(Course course);

    Homework findLatestOneByCourse(Long courseId);

    List<Homework> findAllOfMe(Long userId);

    List<UploadFile> getFileListOfHomework(Long id);

    boolean isStudentUploaded(Long studentId,Long homeworkId);

    UploadFile findFileByUploaderAndHomework(Long uploaderId,Long homeworkId);
}
