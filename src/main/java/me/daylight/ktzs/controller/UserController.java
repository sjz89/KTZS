package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.model.enums.RoleList;
import me.daylight.ktzs.service.CourseService;
import me.daylight.ktzs.service.UserService;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Daylight
 * @date 2019/01/31 14:31
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @ApiDoc(description = "获取用户信息",role = RoleList.All)
    @GetMapping("/getSelfInfo")
    public BaseResponse getSelfInfo(){
        if (!SessionUtil.getInstance().isUserLogin())
            return RetResponse.error("请先登录");
        User user=SessionUtil.getInstance().getUser();
        if (!SessionUtil.getInstance().isMobile())
            return RetResponse.success(user);

        return RetResponse.success(RetResponse.transformUser(user));
    }

    @ApiDoc(description = "更改密码",role = RoleList.All)
    @PostMapping("/changePwd")
    public BaseResponse changePwd(String oldPwd,String password){
        if (!userService.checkPassword(SessionUtil.getInstance().getIdNumber(),oldPwd))
            return RetResponse.error("原密码错误");
        User user=SessionUtil.getInstance().getUser();
        user.setPassword(password);
        userService.saveUser(user);
        return RetResponse.success();
    }

    @ApiDoc(description = "更改用户信息",role = RoleList.All)
    @PostMapping("/changeInfo")
    public BaseResponse changeUserInfo(@RequestBody User user){
        User loginUser=userService.findUserByIdNumber(SessionUtil.getInstance().getIdNumber());
        if (user.getName()!=null&&!user.getName().equals(""))
            loginUser.setName(user.getName());
        if (user.getPhone()!=null)
            loginUser.setPhone(user.getPhone());
        userService.saveUser(loginUser);
        return RetResponse.success();
    }

    @ApiDoc(description = "查看用户信息",role = RoleList.All)
    @GetMapping("/getUserInfo")
    public BaseResponse getUserInfo(String idNumber){
        if (!userService.isUserExist(idNumber))
            return RetResponse.error("用户不存在");
        User user=userService.findUserByIdNumber(idNumber);
        user.setPassword(null);
        user.getRole().setPermissions(null);
        if (!SessionUtil.getInstance().isMobile())
            return RetResponse.success(user);
        return RetResponse.success(RetResponse.transformUser(user));
    }

    @ApiDoc(description = "根据课程查看教师信息",role = RoleList.All)
    @GetMapping("/getTeacherInfo")
    public BaseResponse getTeacherInfo(Long courseId){
        if (!courseService.isCourseExist(courseId))
            return RetResponse.error("课程不存在");
        User teacher=courseService.findCourseById(courseId).getTeacher();
        if (!SessionUtil.getInstance().isMobile())
            return RetResponse.success(teacher);
        return RetResponse.success(RetResponse.transformUser(teacher));
    }
}
