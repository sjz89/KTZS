package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.Unlimited;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.service.RoleService;
import me.daylight.ktzs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Daylight
 * @date 2019/01/25 14:49
 */
@Controller
public class PageController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @ApiDoc(description = "主页")
    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("user",SessionUtil.getInstance().getUser());
        return "common/index";
    }

    @ApiDoc(description = "首页")
    @GetMapping("/home")
    public String home(){
        return "common/home";
    }

    @ApiDoc(description = "登陆页面")
    @Unlimited
    @GetMapping("/login")
    public String login(){
        return "user/login";
    }

    @ApiDoc(description = "注册页面")
    @Unlimited
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("roleList",roleService.findAllRole());
        model.addAttribute("majorList",userService.getMajorList());
        return "user/register";
    }

    @ApiDoc(description = "跳转页面")
    @GetMapping("/redirect")
    public String redirect(){
        return "common/redirect";
    }

    @ApiDoc(description = "未授权页面")
    @Unlimited
    @GetMapping("/401")
    public String unauthorized(){
        return "common/401";
    }

    @ApiDoc(description = "404页面")
    @Unlimited
    @GetMapping("/404")
    public String pageNotFound(){
        return "common/404";
    }

    @ApiDoc(description = "错误页面")
    @Unlimited
    @GetMapping("/error")
    public String error(){
        return "common/error";
    }

    @ApiDoc(description = "用户信息页面")
    @GetMapping("/user/page/info")
    public String info(Model model) {
        model.addAttribute("user",SessionUtil.getInstance().getUser());
        return "user/info";
    }

    @ApiDoc(description = "更改密码页面")
    @GetMapping("/user/page/changePwd")
    public String changePwd(){
        return "user/changePwd";
    }

    @ApiDoc(description = "权限管理页面")
    @GetMapping("/authority/page/authManage")
    public String authManage(){
        return "manager/rbac/authManage";
    }

    @ApiDoc(description = "用户管理页面")
    @GetMapping("/authority/page/userManage")
    public String userManage(Model model){
        model.addAttribute("roleList",roleService.findAllRole());
        return "manager/rbac/userManage";
    }

    @ApiDoc(description = "角色管理页面")
    @GetMapping("/authority/page/roleManage")
    public String roleManage(){
        return "manager/rbac/roleManage";
    }

    @ApiDoc(description = "接口文档页面")
    @GetMapping("/authority/page/apiList")
    public String apiList(){
        return "manager/apiList";
    }

    @ApiDoc(description = "专业管理页面")
    @GetMapping("/major/page/majorManage")
    public String majorManage(){
        return "manager/major/majorManage";
    }

    @ApiDoc(description = "用户专业管理页面")
    @GetMapping("/major/page/userMajorManage")
    public String userMajorManage(Model model){
        model.addAttribute("majorList",userService.getMajorList());
        return "manager/major/userMajorManage";
    }

    @ApiDoc(description = "课程管理页面")
    @GetMapping("/course/page/courseManage")
    public String courseManage(){
        return "manager/course/courseManage";
    }

    @ApiDoc(description = "课程选课学生管理页面")
    @GetMapping("/course/page/courseStudentManage")
    public String courseStudentManage(){
        return "manager/course/courseStudentManage";
    }
}
