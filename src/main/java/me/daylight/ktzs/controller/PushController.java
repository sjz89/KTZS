package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.configuration.redis.RedisConfig;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.dto.NoticeDto;
import me.daylight.ktzs.model.dto.WsMsg;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.Notice;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.service.CourseService;
import me.daylight.ktzs.service.NoticeService;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/10 22:29
 */
@RestController
@RequestMapping("/notice")
public class PushController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private CourseService courseService;

    @ApiDoc(description = "教师发送通知")
    @PostMapping("/push")
    public BaseResponse push(@RequestBody Notice notice){

        notice=noticeService.save(notice);
        Course course=courseService.findCourseById(notice.getCourse().getId());
        notice.getCourse().setName(course.getName());
        notice.getCourse().setTeacher(course.getTeacher());

        List<String> receiveIds=new ArrayList<>();
        for (User student:course.getStudents())
            receiveIds.add(student.getIdNumber());

        notice.getCourse().setStudents(null);
        notice.getCourse().getTeacher().setRole(null);

        WsMsg msg=new WsMsg();
        msg.setData(notice);
        msg.setReceiverIdList(receiveIds);
        redisTemplate.convertAndSend(RedisConfig.Redis_Channel_Notice,msg);

        return RetResponse.success();
    }

    @ApiDoc(description = "获取课程最新通知")
    @GetMapping("/getLatestNotice")
    public BaseResponse getLatestNotice(Long courseId){
        if (!courseService.isCourseExist(courseId))
            return RetResponse.error("课程不存在");
        Notice notice=noticeService.getLatestByCourseId(courseId);
        if (notice==null)
            return RetResponse.error("无最近通知");
        NoticeDto noticeDto=new NoticeDto();
        noticeDto.setId(notice.getId());
        noticeDto.setContent(notice.getContent());
        noticeDto.setTime(notice.getCreateTime().getTime());
        return RetResponse.success(noticeDto);
    }

    @ApiDoc(description = "获取学生收到的通知")
    @GetMapping("/getNoticesForMe")
    public BaseResponse getNoticesForMe(){
        return RetResponse.success(noticeService.findNoticesByStudent(SessionUtil.getInstance().getUser().getId()));
    }
}
