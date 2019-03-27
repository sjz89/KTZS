package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.Major;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.model.enums.RoleList;
import me.daylight.ktzs.service.UserService;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author Daylight
 * @date 2019/02/01 00:00
 */
@RestController
@RequestMapping("/major")
public class MajorController {

    @Autowired
    private UserService userService;

    @ApiDoc(description = "添加专业")
    @GetMapping("/addMajor")
    public BaseResponse addMajor(String name){
        if (userService.isMajorExist(new Major(null,name)))
            return RetResponse.error("专业已存在");
        Major major=new Major();
        major.setName(name);
        return RetResponse.success(userService.saveMajor(major).getId());
    }

    @ApiDoc(description = "删除专业")
    @GetMapping("/delMajor")
    public BaseResponse delMajor(Major major){
        if (!userService.isMajorExist(major))
            return RetResponse.error("专业不存在");
        userService.delMajor(major);
        return RetResponse.success();
    }

    @ApiDoc(description = "更改用户专业")
    @GetMapping("/setMajor")
    public BaseResponse serMajor(Long userId, Long majorId){
        if (!userService.isMajorExist(new Major(majorId,null)))
            return RetResponse.error("专业不存在");
        userService.changeUserMajor(userId, majorId);
        return RetResponse.success();
    }

    @ApiDoc(description = "获取专业信息")
    @GetMapping("/getMajors")
    public BaseResponse getMajors(){
        List<Major> majors=userService.getMajorList();
        List<Map<String,Object>> list=new ArrayList<>();
        for (Major major:majors){
            Map<String,Object> objectMap=new HashMap<>();
            objectMap.put("id",String.format("%02d",major.getId()));
            objectMap.put("name",major.getName());
            objectMap.put("count",major.getUsers()==null?0:major.getUsers().size());
            list.add(objectMap);
        }
        return RetResponse.success(list);
    }

    @ApiDoc(description = "根据专业获取学生与辅导员")
    @GetMapping("/getUsersByMajor")
    public BaseResponse getUsersByMajor(Long majorId){
        if (majorId==null)
            return RetResponse.success(userService.findUsersByMajorNull());
        if (!userService.isMajorExist(new Major(majorId,null)))
            return RetResponse.error("专业不存在");
        Major major=userService.findMajorById(majorId);
        List<User> users=major.getUsers();
        for (User user:users){
            user.getRole().setPermissions(null);
        }
        users.sort(Comparator.comparing(User::getIdNumber));
        /*java list sort lambda表达式
        原式如下
        new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getIdNumber().compareTo(o2.getIdNumber());
            }
        };*/
        return RetResponse.success(users);
    }

    @ApiDoc(description = "获取本专业学生",role = RoleList.Instructor)
    @GetMapping("/getStudentsOfMyMajor")
    public BaseResponse getStudentsOfMyMajor(){
        return RetResponse.success(userService.findStudents(SessionUtil.getInstance().getUser().getId()));
    }
}
