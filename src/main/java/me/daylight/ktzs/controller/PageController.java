package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.Unlimited;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.model.enums.RoleList;
import me.daylight.ktzs.service.*;
import me.daylight.ktzs.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Autowired
    private CourseService courseService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @ApiDoc(description = "主页", role = RoleList.All)
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("user", SessionUtil.getInstance().getUser());
        return "common/index";
    }

    @ApiDoc(description = "首页", role = {RoleList.Admin, RoleList.Instructor})
    @GetMapping("/home")
    public String home(Model model) {
        User loginUser = SessionUtil.getInstance().getUser();
        if (loginUser.getRole().getName().equals("admin")) {
            model.addAttribute("absentCount", attendanceService.getAbsentCountOfToday());
            model.addAttribute("leaveCount", leaveService.getLeaveCountOfToday());
            model.addAttribute("leaveApplication", leaveService.getLeaveApplicationCount());
            model.addAttribute("deviceReplace", deviceService.countDeviceReplace());
        } else {
            List<User> students = userService.findStudents(SessionUtil.getInstance().getUser().getId());
            model.addAttribute("absentCount", attendanceService
                    .getAbsentCountOfTodayByCourseIn(courseService.getCoursesByMajor(userService
                            .findMajorByUserId(SessionUtil.getInstance().getUser().getId()).getId())));
            model.addAttribute("leaveCount", leaveService.getLeaveCountOfTodayByStudentIn(students));
            model.addAttribute("leaveApplication", leaveService.getLeaveApplicationCountByStudentIn(students));
            model.addAttribute("deviceReplace", deviceService.countDeviceReplaceByStudentIn(students));
        }
        return "common/home";
    }

    @ApiDoc(description = "app下载页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/app")
    public String appPage() {
        return "common/apk";
    }

    @ApiDoc(description = "登陆页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/login")
    public String login() {
        return "user/login";
    }

    @ApiDoc(description = "注册页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("roleList", roleService.findAllRole());
        model.addAttribute("majorList", userService.getMajorList());
        return "user/register";
    }

    @ApiDoc(description = "跳转页面", role = RoleList.All)
    @GetMapping("/redirect")
    public String redirect() {
        return "common/redirect";
    }

    @ApiDoc(description = "未授权页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/401")
    public String unauthorized() {
        return "common/401";
    }

    @ApiDoc(description = "404页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/404")
    public String pageNotFound() {
        return "common/404";
    }

    @ApiDoc(description = "错误页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/error")
    public String error() {
        return "common/error";
    }

    @ApiDoc(description = "用户信息页面", role = RoleList.All)
    @GetMapping("/user/info.html")
    public String info(Model model) {
        model.addAttribute("user", SessionUtil.getInstance().getUser());
        return "user/info";
    }

    @ApiDoc(description = "更改密码页面", role = RoleList.All)
    @GetMapping("/user/changePwd.html")
    public String changePwd() {
        return "user/changePwd";
    }

    @ApiDoc(description = "权限管理页面")
    @GetMapping("/authority/authManage.html")
    public String authManage() {
        return "rbac/authManage";
    }

    @ApiDoc(description = "用户管理页面")
    @GetMapping("/authority/userManage.html")
    public String userManage(Model model) {
        model.addAttribute("roleList", roleService.findAllRole());
        return "rbac/userManage";
    }

    @ApiDoc(description = "角色管理页面")
    @GetMapping("/authority/roleManage.html")
    public String roleManage() {
        return "rbac/roleManage";
    }

    @ApiDoc(description = "接口文档页面")
    @GetMapping("/authority/apiList.html")
    public String apiList() {
        return "manage/apiList";
    }

    @ApiDoc(description = "专业管理页面")
    @GetMapping("/major/majorManage.html")
    public String majorManage() {
        return "major/majorManage";
    }

    @ApiDoc(description = "用户专业管理页面")
    @GetMapping("/major/userMajorManage.html")
    public String userMajorManage(Model model) {
        model.addAttribute("majorList", userService.getMajorList());
        return "major/userMajorManage";
    }

    @ApiDoc(description = "课程管理页面")
    @GetMapping("/course/courseManage.html")
    public String courseManage(Model model) {
        model.addAttribute("majorList", userService.getMajorList());
        model.addAttribute("teacherList", userService.findUsersByRole(roleService.findRoleByName("teacher").getId()));
        return "course/courseManage";
    }

    @ApiDoc(description = "课程选课学生管理页面")
    @GetMapping("/course/courseStudentManage.html")
    public String courseStudentManage(Model model) {
        model.addAttribute("courseList", courseService.getAllCourse());
        return "course/courseStudentManage";
    }

    @ApiDoc(description = "课堂签到二维码展示页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/signIn/{uniqueId}")
    public String showUniqueCodePage(@PathVariable String uniqueId, Model model) {
        model.addAttribute("hasUniqueId", redisTemplate.hasKey(uniqueId));
        model.addAttribute("remainTime", redisTemplate.getExpire(uniqueId, TimeUnit.SECONDS));
        model.addAttribute("uniqueId", uniqueId);
        model.addAttribute("isUniqueIdCorrect", redisTemplate.hasKey("qd_" + uniqueId));
        return "common/signIn";
    }

    @ApiDoc(description = "根据课程查询签到记录页面", role = {RoleList.Admin, RoleList.Instructor})
    @GetMapping("/attendance/queryByCourse.html")
    public String queryByCourse(Model model) {
        if (SessionUtil.getInstance().getUser().getRole().getName().equals("admin"))
            model.addAttribute("courseList", courseService.getAllCourse());
        else
            model.addAttribute("courseList", courseService.getCoursesByMajor(userService
                    .findMajorByUserId(SessionUtil.getInstance().getUser().getId()).getId()));
        return "attendance/queryByCourse";
    }

    @ApiDoc(description = "根据学生查询签到记录页面", role = {RoleList.Admin, RoleList.Instructor})
    @GetMapping("/attendance/queryByStudent.html")
    public String queryByStudent() {
        return "attendance/queryByStudent";
    }

    @ApiDoc(description = "辅导员管理请假申请页面", role = {RoleList.Admin, RoleList.Instructor})
    @GetMapping("/manage/leaveManage.html")
    public String leaveManage() {
        return "manage/leaveManage";
    }

    @ApiDoc(description = "本专业学生管理页面", role = RoleList.Instructor)
    @GetMapping("/major/studentManage.html")
    public String studentManage() {
        return "major/studentManage";
    }

    @ApiDoc(description = "文件上传页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/upload")
    public String upload() {
        return "common/upload";
    }

    @ApiDoc(description = "文件下载页面", role = RoleList.Unlimited)
    @Unlimited
    @GetMapping("/download")
    public String download() {
        return "common/download";
    }

    @ApiDoc(description = "设备更换管理页面", role = {RoleList.Admin, RoleList.Instructor})
    @GetMapping("/manage/deviceReplace.html")
    public String deviceReplace() {
        return "manage/deviceManage";
    }

    @ApiDoc(description = "下载APP", role = RoleList.Unlimited)
    @Unlimited
    @RequestMapping("/app/download")
    public ResponseEntity<FileSystemResource> downloadAPP() {
        String path = "app/";
        String name = "KTZS.apk";
        FileUtil.createDirIfNotExists("app/");
        File file = new File(FileUtil.absolutePath, FileUtil.staticDir + path + name);
        if (file.exists()) {
            return ResponseEntity
                    .ok()
                    .header("Content-Disposition", "attachment;fileName=" + name)
                    .contentLength(file.length())
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .body(new FileSystemResource(file));
        }
        return ResponseEntity.notFound().build();
    }
}
