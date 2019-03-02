package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Major;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.service.CourseService;
import me.daylight.ktzs.service.UserService;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Daylight
 * @date 2019/02/28 15:03
 */
@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @ApiDoc(description = "添加保存课程")
    @PostMapping("/save")
    public BaseResponse saveCourse(@RequestBody Course course){
        return RetResponse.success(courseService.saveCourse(course).getId());
    }

    @ApiDoc(description = "删除课程")
    @GetMapping("/del")
    public BaseResponse delCourse(Long id){
        if (!courseService.isCourseExist(id))
            return RetResponse.error("课程不存在");
        courseService.delCourse(id);
        return RetResponse.success();
    }

    @ApiDoc(description = "添加学生")
    @PostMapping("/addStudents")
    public BaseResponse addStudents(Long courseId, @RequestBody List<User> students){
        if (!courseService.isCourseExist(courseId))
            return RetResponse.error("课程不存在");
        Course course=courseService.findCourseById(courseId);
        if (course.getStudents()!=null)
            course.getStudents().addAll(students);
        else
            course.setStudents(students);
        courseService.saveCourse(course);
        return RetResponse.success();
    }

    @ApiDoc(description = "根据ID查找课程")
    @GetMapping("/getCourseById")
    public BaseResponse getCourseById(Long id){
        if (!courseService.isCourseExist(id))
            return RetResponse.error("课程不存在");
        return RetResponse.success(courseService.findCourseById(id));
    }

    @ApiDoc(description = "根据学期查找所有课程")
    @GetMapping("/getCoursesBySemester")
    public BaseResponse getAllCourse(String semester,int page,int limit){
        Page<Course> courses=courseService.getAllCourse(semester,page, limit);
        Map<String,Object> objectMap=new HashMap<>();
        objectMap.put("list",courses.getContent());
        objectMap.put("count",courses.getTotalElements());
        return RetResponse.success(objectMap);
    }

    @ApiDoc(description = "查看我的课程")
    @GetMapping("/getMyCourses")
    public BaseResponse getMyCourses(String semester){
        return RetResponse.success(courseService.findCoursesByUserIdAndSemester(semester,SessionUtil.getInstance().getUser().getId()));
    }

    @ApiDoc(description = "根据专业查找课程")
    @GetMapping("/getCoursesByMajorAndSemester")
    public BaseResponse findCoursesByMajorAndSemester(String semester, Major major){
        if (!userService.isMajorExist(major))
            return RetResponse.error("专业不存在");
        return RetResponse.success(courseService.findCoursesByMajorAndSemester(semester, major));
    }

    @ApiDoc(description = "查看选修课程的学生")
    @GetMapping("/getStudentsByCourse")
    public BaseResponse findStudentsByCourse(Long courseId){
        if (!courseService.isCourseExist(courseId))
            return RetResponse.error("课程不存在");
        List<User> students=courseService.findCourseById(courseId).getStudents();
        students.sort(Comparator.comparing(User::getIdNumber));
        return RetResponse.success(students);
    }
}
