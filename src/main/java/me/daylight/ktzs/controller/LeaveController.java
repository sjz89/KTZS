package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.dto.LeaveDto;
import me.daylight.ktzs.model.entity.LeaveNote;
import me.daylight.ktzs.model.enums.LeaveState;
import me.daylight.ktzs.model.enums.RoleList;
import me.daylight.ktzs.service.LeaveService;
import me.daylight.ktzs.utils.DateUtil;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author Daylight
 * @date 2019/03/16 21:58
 */
@RestController
@RequestMapping("/leave")
public class LeaveController {
    @Autowired
    private LeaveService leaveService;

    @ApiDoc(description = "提交请假申请",role = RoleList.Student)
    @PostMapping("/save")
    public BaseResponse save(Long startDate,Long endDate,String reason){
        LeaveNote leaveNote=new LeaveNote();
        leaveNote.setStartDate(new Date(startDate));
        leaveNote.setEndDate(new Date(endDate));
        leaveNote.setReason(reason);
        leaveNote.setStudent(SessionUtil.getInstance().getUser());
        leaveNote.setState(LeaveState.WAITING.getCode());
        return RetResponse.success(leaveService.save(leaveNote).getId());
    }

    @ApiDoc(description = "更改请假申请状态",role = {RoleList.Admin,RoleList.Instructor})
    @PostMapping("/changeState")
    public BaseResponse changeState(Long leaveId,int state,String remark){
        if (!leaveService.isLeaveNoteExist(leaveId))
            return RetResponse.error("不存在该请假申请");
        leaveService.changeState(leaveId, state,remark);
        return RetResponse.success();
    }

    @ApiDoc(description = "查询个人请假记录",role = RoleList.Student)
    @GetMapping("/getMyLeaveRecord")
    public BaseResponse getMyLeaveRecord(){
        List<LeaveDto> leaveDtos=new ArrayList<>();
        for (LeaveNote leaveNote:leaveService.getLeaveNotesByUser(SessionUtil.getInstance().getUser())){
            LeaveDto leaveDto=new LeaveDto();
            leaveDto.setId(leaveNote.getId());
            leaveDto.setStudentName(leaveNote.getStudent().getName());
            leaveDto.setStartDate(DateUtil.dateToStr("yyyy-MM-dd",leaveNote.getStartDate()));
            leaveDto.setEndDate(DateUtil.dateToStr("yyyy-MM-dd",leaveNote.getEndDate()));
            leaveDto.setState(leaveNote.getState());
            leaveDto.setReason(leaveNote.getReason());
            leaveDto.setRemark(leaveNote.getRemark());
            leaveDto.setTime(DateUtil.dateToStr("yyyy-MM-dd HH:mm:ss",leaveNote.getCreateTime()));
            leaveDtos.add(leaveDto);
        }
        return RetResponse.success(leaveDtos);
    }

    @ApiDoc(description = "查询所有请假申请",role = {RoleList.Admin,RoleList.Instructor})
    @GetMapping("/getLeaveNote")
    public BaseResponse getLeaveNote(int page,int limit){
        Page<LeaveNote> leaveNotePage;
        if (SessionUtil.getInstance().getUser().getRole().getName().equals("admin"))
            leaveNotePage=leaveService.getAllLeaveNote(page, limit);
        else
            leaveNotePage=leaveService.getLeaveNoteByMajor(SessionUtil.getInstance().getUser().getId(),page,limit);
        Map<String,Object> map=new HashMap<>();
        map.put("count",leaveNotePage.getTotalElements());
        map.put("list",leaveNotePage.getContent());
        return RetResponse.success(map);
    }
}
