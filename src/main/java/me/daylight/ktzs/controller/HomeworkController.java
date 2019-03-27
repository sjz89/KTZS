package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.authority.Unlimited;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Homework;
import me.daylight.ktzs.model.entity.UploadFile;
import me.daylight.ktzs.model.enums.RoleList;
import me.daylight.ktzs.service.CourseService;
import me.daylight.ktzs.service.HomeworkService;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Daylight
 * @date 2019/03/13 00:02
 */
@RestController
@RequestMapping("/homework")
public class HomeworkController {
    @Autowired
    private HomeworkService homeworkService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @ApiDoc(description = "教师布置作业",role = RoleList.Teacher)
    @PostMapping("/publish")
    public BaseResponse publishHomework(String name,String content,Long courseId,Long endTime){
        Homework homework=new Homework();
        homework.setName(name);
        homework.setContent(content);
        homework.setEndTime(new Date(endTime));
        Course course=new Course();
        course.setId(courseId);
        homework.setCourse(course);
        return RetResponse.success(homeworkService.save(homework).getId());
    }

    @ApiDoc(description = "查询课程所有作业",role = {RoleList.Teacher,RoleList.Student})
    @GetMapping("/findAllByCourse")
    public BaseResponse findAllByCourse(Course course){
        if (!courseService.isCourseExist(course.getId()))
            return RetResponse.error("课程不存在");
        return RetResponse.success(homeworkService.findHomeworkByCourse(course));
    }

    @ApiDoc(description = "查询课程最新作业",role = {RoleList.Teacher,RoleList.Student})
    @GetMapping("/findLatestOneByCourse")
    public BaseResponse findLatestOneByCourse(Long courseId){
        if (!courseService.isCourseExist(courseId))
            return RetResponse.error("课程不存在");
        return RetResponse.success(homeworkService.findLatestOneByCourse(courseId));
    }

    @ApiDoc(description = "查询我的所有作业",role = {RoleList.Student,RoleList.Teacher})
    @GetMapping("/findAllOfMe")
    public BaseResponse findHomeworkOfStudent(){
        List<Homework> homeworkList=homeworkService.findAllOfMe(SessionUtil.getInstance().getUser().getId());
        for (Homework homework:homeworkList){
            homework.getCourse().setStudents(null);
            homework.getCourse().setTeacher(null);
            homework.getCourse().setMajor(null);
            homework.setFiles(null);
            homework.setContent(null);
        }
        return RetResponse.success(homeworkList);
    }

    @ApiDoc(description = "获取作业详情",role = {RoleList.Teacher,RoleList.Student})
    @GetMapping("/getDetail")
    public BaseResponse getDetail(Long id){
        if (!homeworkService.isHomeworkExist(id))
            return RetResponse.error("作业不存在");
        return RetResponse.success(homeworkService.findById(id));
    }

    @ApiDoc(description = "获取作业文件列表",role = {RoleList.Teacher,RoleList.Student})
    @GetMapping("/getFileList")
    public BaseResponse getFileListOfHomework(Long id){
        if (!homeworkService.isHomeworkExist(id))
            return RetResponse.error("作业不存在");
        List<UploadFile> files=homeworkService.getFileListOfHomework(id);
        for (UploadFile file:files)
            file.getUploader().setRole(null);
        return RetResponse.success(files);
    }

    @ApiDoc(description = "开启电脑端上传下载文件",role = {RoleList.Teacher,RoleList.Student})
    @GetMapping("/setRandomCode")
    public BaseResponse setRandomCode(String randomCode,Long homeworkId){
        Map<String,Long> map=new HashMap<>();
        map.put("homeworkId",homeworkId);
        map.put("userId",SessionUtil.getInstance().getUser().getId());
        redisTemplate.opsForHash().putAll(randomCode,map);
        redisTemplate.opsForHash().getOperations().expire(randomCode,10,TimeUnit.MINUTES);
        return RetResponse.success();
    }

    @SuppressWarnings("ConstantConditions")
    @ApiDoc(description = "从提取码中获取信息",role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/checkRandomCode")
    public BaseResponse getInfoFromRandomCode(String randomCode){
        if (!redisTemplate.hasKey(randomCode))
            return RetResponse.error("文件码不存在");
        Map map=redisTemplate.opsForHash().entries(randomCode);
        return RetResponse.success(map);
    }
}
