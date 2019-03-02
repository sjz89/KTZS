package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.User;
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

    @ApiDoc(description = "获取用户信息")
    @GetMapping("/getSelfInfo")
    public BaseResponse getSelfInfo(){
        if (!SessionUtil.getInstance().isUserLogin())
            return RetResponse.error("请先登录");
        return RetResponse.success(SessionUtil.getInstance().getUser());
    }

    @ApiDoc(description = "更改密码")
    @PostMapping("/changePwd")
    public BaseResponse changePwd(String oldPwd,String password){
        if (!userService.checkPassword(SessionUtil.getInstance().getIdNumber(),oldPwd))
            return RetResponse.error("原密码错误");
        User user=SessionUtil.getInstance().getUser();
        user.setPassword(password);
        userService.saveUser(user);
        return RetResponse.success();
    }

    @ApiDoc(description = "更改用户信息")
    @PostMapping("/changeInfo")
    public BaseResponse changeUserInfo(@RequestBody User user){
        User loginUser=SessionUtil.getInstance().getUser();
        if (user.getName()!=null&&!user.getName().equals(""))
            loginUser.setName(user.getName());
        if (user.getPhone()!=null)
            loginUser.setPhone(user.getPhone());
        userService.saveUser(loginUser);
        return RetResponse.success();
    }
}
