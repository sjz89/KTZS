package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.LeaveNote;
import me.daylight.ktzs.model.enums.LeaveState;
import me.daylight.ktzs.service.LeaveService;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daylight
 * @date 2019/03/16 21:58
 */
@RestController
@RequestMapping("/leave")
public class LeaveController {
    @Autowired
    private LeaveService leaveService;

    @ApiDoc(description = "提交请假申请")
    @PostMapping("/save")
    public BaseResponse save(Long courseId,String reason){
        LeaveNote leaveNote=new LeaveNote();
        Course course=new Course();
        course.setId(courseId);
        leaveNote.setCourse(course);
        leaveNote.setReason(reason);
        leaveNote.setStudent(SessionUtil.getInstance().getUser());
        leaveNote.setState(LeaveState.WAITING.getCode());
        return RetResponse.success(leaveService.save(leaveNote).getId());
    }

    @ApiDoc(description = "更改请假申请状态")
    @PostMapping("/changeState")
    public BaseResponse changeState(Long courseId,int state){
        if (!leaveService.isLeaveNoteExist(courseId))
            return RetResponse.error("不存在该请假申请");
        leaveService.changeState(courseId, state);
        return RetResponse.success();
    }



}
