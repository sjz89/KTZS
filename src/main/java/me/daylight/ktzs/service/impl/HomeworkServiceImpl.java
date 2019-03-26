package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.FileRepository;
import me.daylight.ktzs.model.dao.HomeworkRepository;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Homework;
import me.daylight.ktzs.model.entity.UploadFile;
import me.daylight.ktzs.service.HomeworkService;
import me.daylight.ktzs.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author daylight
 * @date 2019/03/23 20:14
 */
@Service
public class HomeworkServiceImpl implements HomeworkService {
    @Autowired
    private HomeworkRepository homeworkRepository;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public boolean isHomeworkExist(Long homeworkId) {
        return homeworkRepository.existsById(homeworkId);
    }

    @Override
    public Homework save(Homework homework) {
        return homeworkRepository.saveAndFlush(homework);
    }

    @Override
    public Map<String,Object> findById(Long id) {
        Object[] objects=homeworkRepository.findHomeworkById(id).get(0);
        Map<String,Object> map=new HashMap<>();
        map.put("name",objects[0]);
        map.put("content",objects[1]);
        map.put("courseName",objects[2]);
        map.put("endTime", DateUtil.dateToStr("yyyy-MM-dd",(Date)objects[3]));
        map.put("createTime",DateUtil.dateToStr("yyyy-MM-dd HH:mm:ss",(Date)objects[4]));
        map.put("count",objects[5]+"人");
        return map;
    }

    @Override
    public void addFileToHomework(Long homeworkId, Long fileId) {
        homeworkRepository.insertFileToHomework(homeworkId, fileId);
    }

    @Override
    public List<Homework> findHomeworkByCourse(Course course) {
        return homeworkRepository.findAllByCourseOrderByCreateTime(course);
    }

    @Override
    public Homework findLatestOneByCourse(Long courseId) {
        return homeworkRepository.findLatestOneByCourse(courseId);
    }

    @Override
    public List<Homework> findAllOfMe(Long userId) {
        return homeworkRepository.findAllOfMe(userId);
    }

    @Override
    public List<UploadFile> getFileListOfHomework(Long id) {
        return fileRepository.findUploadFilesOfHomework(id);
    }

    @Override
    public boolean isStudentUploaded(Long studentId, Long homeworkId) {
        return homeworkRepository.isStudentUploaded(studentId, homeworkId)!=0;
    }

    @Override
    public UploadFile findFileByUploaderAndHomework(Long uploaderId, Long homeworkId) {
        return fileRepository.findFileByUploaderAndHomework(uploaderId,homeworkId);
    }
}
