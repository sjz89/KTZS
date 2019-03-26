package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.SessionUtil;
import me.daylight.ktzs.configuration.redis.RedisConfig;
import me.daylight.ktzs.model.dto.AttendanceRecord;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.dto.SignInState;
import me.daylight.ktzs.model.dto.WsMsg;
import me.daylight.ktzs.model.entity.Attendance;
import me.daylight.ktzs.model.entity.Course;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.model.enums.AttendanceState;
import me.daylight.ktzs.model.enums.RoleList;
import me.daylight.ktzs.service.AttendanceService;
import me.daylight.ktzs.service.CourseService;
import me.daylight.ktzs.service.LeaveService;
import me.daylight.ktzs.service.UserService;
import me.daylight.ktzs.utils.DateUtil;
import me.daylight.ktzs.utils.RetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author daylight
 * @date 2019/02/28 22:55
 */
@RestController
@RequestMapping("/attendance")
@SuppressWarnings("ConstantConditions")
public class AttendanceController {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private LeaveService leaveService;

    @ApiDoc(description = "教师开启签到",role = RoleList.Teacher)
    @PostMapping("/start")
    public BaseResponse startSignIn(Course course, int limitMin, Double x, Double y){
        if (!courseService.isCourseExist(course.getId()))
            return RetResponse.error("课程不存在");
        if (redisTemplate.hasKey(SessionUtil.getInstance().getIdNumber()))
            return RetResponse.error("您已开启了一个签到");
        String uniqueId= DateUtil.dateToStr("MMddHHmm",new Date())+course.getId().toString();
        for (User student:courseService.findCourseById(course.getId()).getStudents()) {
            User user=new User();
            user.setId(student.getId());
            int state=AttendanceState.UNSIGNED.getCode();
            if (leaveService.isStudentLeave(student.getId()))
                state=AttendanceState.LEAVE.getCode();
            redisTemplate.opsForHash().put(uniqueId+"_list", student.getIdNumber(),
                    Attendance.generateValue(uniqueId, course, user,state));
        }
        redisTemplate.opsForHash().put(uniqueId+"_position","position-x",x);
        redisTemplate.opsForHash().put(uniqueId+"_position","position-y",y);
        redisTemplate.opsForValue().set("qd_"+uniqueId,0,limitMin,TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(SessionUtil.getInstance().getIdNumber(),uniqueId,limitMin,TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(uniqueId,SessionUtil.getInstance().getIdNumber(),limitMin,TimeUnit.MINUTES);
        return RetResponse.success(uniqueId);
    }

    @ApiDoc(description = "检查当前是否有正在进行的签到",role = RoleList.Teacher)
    @GetMapping("/checkHasSignInProgress")
    public BaseResponse checkProgress(){
        if (redisTemplate.hasKey(SessionUtil.getInstance().getIdNumber()))
            return RetResponse.success();
        return RetResponse.error("none");
    }

    @ApiDoc(description = "获取当前正在进行的签到",role = RoleList.Teacher)
    @GetMapping("/getNow")
    public BaseResponse getAttendanceNow(){
        if (!redisTemplate.hasKey(SessionUtil.getInstance().getIdNumber()))
            return RetResponse.error("您当前没有正在进行的签到");
        String uniqueId=(String)redisTemplate.opsForValue().get(SessionUtil.getInstance().getIdNumber());
        SignInState signInState=new SignInState();
        signInState.setUniqueId(uniqueId);
        signInState.setRemainTime(redisTemplate.getExpire(SessionUtil.getInstance().getIdNumber(),TimeUnit.MILLISECONDS));
        signInState.setCount((int)redisTemplate.opsForValue().get("qd_"+uniqueId));
        return RetResponse.success(signInState);
    }

    @SuppressWarnings("unchecked")
    @ApiDoc(description = "教师手动结束签到",role = RoleList.Teacher)
    @GetMapping("/stop")
    public BaseResponse stopSignIn(String uniqueId){
        if (!redisTemplate.hasKey(SessionUtil.getInstance().getIdNumber()))
            return RetResponse.error("您当前并无正在进行的签到");
        if (!uniqueId.equals(redisTemplate.opsForValue().get(SessionUtil.getInstance().getIdNumber())))
            return RetResponse.error("操作失败");
        //将redis中的签到记录存放到数据库中
        Map attendanceMap=redisTemplate.opsForHash().entries(uniqueId+"_list");
        attendanceService.saveAll(attendanceMap.values());

        redisTemplate.delete(uniqueId);
        redisTemplate.delete(SessionUtil.getInstance().getIdNumber());
        redisTemplate.delete("qd_"+uniqueId);
        redisTemplate.delete(uniqueId+"_list");
        redisTemplate.delete(uniqueId+"_position");
        return RetResponse.success();
    }

    @ApiDoc(description = "学生签到",role = RoleList.Student)
    @PostMapping("/signIn")
    public BaseResponse signIn(String uniqueId,Double x,Double y){
        //签到流程
        //1 检测签到是否结束
        if (!redisTemplate.hasKey("qd_"+uniqueId))
            return RetResponse.error("该签到已结束");
        //2 检测学生是否选修
        if (!redisTemplate.opsForHash().hasKey(uniqueId+"_list",SessionUtil.getInstance().getIdNumber()))
            return RetResponse.error("你未选修该课程，无法签到");
        //3 检测学生是否已经签到
        if (((Attendance)redisTemplate.opsForHash().get(uniqueId+"_list",SessionUtil.getInstance().getIdNumber())).getState()==AttendanceState.SIGNED.getCode())
            return RetResponse.error("你已签到，请不要重复签到");
        //4 检测学生位置是否在签到范围内
        if (((Double)redisTemplate.opsForHash().get(uniqueId+"_position","position-x")-x>0.002
        ||x-(Double) redisTemplate.opsForHash().get(uniqueId+"_position","position-x")>0.002)&&
                ((Double)redisTemplate.opsForHash().get(uniqueId+"_position","position-y")-y>0.002)||
                y-(Double) redisTemplate.opsForHash().get(uniqueId+"_position","position-y")>0.002)
            return RetResponse.error("不在签到范围内");

        Attendance attendance=(Attendance)redisTemplate.opsForHash().get(uniqueId+"_list",SessionUtil.getInstance().getIdNumber());
        attendance.setState(AttendanceState.SIGNED.getCode());
        attendance.setCreateTime(new Date());
        redisTemplate.opsForHash().put(uniqueId+"_list",SessionUtil.getInstance().getIdNumber(),attendance);

        AttendanceRecord record=new AttendanceRecord(null,courseService.findCourseById(attendance.getCourse().getId()).getName(),
                AttendanceState.SIGNED.getCode(),attendance.getCreateTime());

        int count=(int)redisTemplate.opsForValue().get("qd_"+uniqueId)+1;
        redisTemplate.opsForValue().set("qd_"+uniqueId,count,redisTemplate.getExpire("qd_"+uniqueId,TimeUnit.SECONDS),TimeUnit.SECONDS);

        User user=SessionUtil.getInstance().getUser();
        Map<String,Object> map=transferSignInDetail(user,attendance.getCreateTime());
        map.put("count",count);
        WsMsg msg=new WsMsg();
        msg.setReceiverIdList(Collections.singletonList((String) redisTemplate.opsForValue().get(uniqueId)));
        msg.setData(map);
        redisTemplate.convertAndSend(RedisConfig.Redis_Channel_SignInCount, msg);
        return RetResponse.success(record);
    }

    @ApiDoc(description = "获取全部签到记录",role = {RoleList.Admin,RoleList.Instructor})
    @GetMapping("/getAll")
    public BaseResponse getAllAttendanceRecord(int page,int limit){
        Map<String,Object> resultMap=new HashMap<>();
        Page<Attendance> attendances;
        if (SessionUtil.getInstance().getUser().getRole().getName().equals("admin"))
            attendances=attendanceService.findAllPageable(page,limit);
        else
            attendances=attendanceService.findAllByMajorPageable(SessionUtil.getInstance().getUser().getId(),page,limit);
        resultMap.put("list",attendances.getContent());
        resultMap.put("count",attendances.getTotalElements());
        return RetResponse.success(resultMap);
    }

    @ApiDoc(description = "根据课程查询签到记录",role = {RoleList.Admin,RoleList.Instructor})
    @GetMapping("/getByCourse")
    public BaseResponse getAttendanceByCourse(Course course,int page,int limit){
        if (!courseService.isCourseExist(course.getId()))
            return RetResponse.error("课程不存在");
        Map<String,Object> resultMap=new HashMap<>();
        Page<Attendance> attendances=attendanceService.findByCourse(course, page,limit);
        resultMap.put("list",attendances.getContent());
        resultMap.put("count",attendances.getTotalElements());
        return RetResponse.success(resultMap);
    }

    @ApiDoc(description = "根据学生查询签到记录",role = {RoleList.Admin,RoleList.Instructor})
    @GetMapping("/getByStudent")
    public BaseResponse getAttendanceByStudent(String idNumber,int page,int limit){
        if (!userService.isUserExist(idNumber))
            return RetResponse.error("账号不存在");
        Map<String,Object> resultMap=new HashMap<>();
        Page<Attendance> attendances=attendanceService.findByStudentPageable(userService.findUserByIdNumber(idNumber),page,limit);
        resultMap.put("list",attendances.getContent());
        resultMap.put("count",attendances.getTotalElements());
        return RetResponse.success(resultMap);
    }

    @ApiDoc(description = "根据课程获取最近一次签到情况",role = {RoleList.Teacher,RoleList.Student})
    @GetMapping("/getLatestRecord")
    public BaseResponse getLatestRecord(Long courseId){
        if (!courseService.isCourseExist(courseId))
            return RetResponse.error("课程不存在");
        Attendance attendance=attendanceService.getLatestByCourseId(courseId);
        if (attendance==null)
            return RetResponse.error("无最近签到");
        int count=attendanceService.countByCourseAndStateAndUniqueId(courseId,AttendanceState.SIGNED.getCode(),"%"+attendance.getUniqueId()+"%");
        Map<String,String> map=new HashMap<>();
        map.put("count",count+"/"+courseService.findCourseById(courseId).getStudents().size());
        map.put("time",DateUtil.dateToStr("yyyy-MM-dd HH:mm",attendance.getCreateTime()));
        return RetResponse.success(map);
    }

    @ApiDoc(description = "查看我的签到记录",role = RoleList.Student)
    @GetMapping("/getMyAttendance")
    public BaseResponse getMyAttendance(Integer limit){

        List<Attendance> attendances=attendanceService.findByStudent(SessionUtil.getInstance().getUser());
        List<AttendanceRecord> records=new ArrayList<>();
        if (limit!=null) {
            limit=limit>attendances.size()?attendances.size():limit;
            for (int i=0;i<limit;i++){
                records.add(new AttendanceRecord(attendances.get(i).getId(),attendances.get(i).getCourse().getName(),attendances.get(i).getState(),attendances.get(i).getCreateTime()));
            }
        }else {
            for (Attendance attendance : attendances) {
                records.add(new AttendanceRecord(attendance.getId(),attendance.getCourse().getName(), attendance.getState(), attendance.getCreateTime()));
            }
        }
        return RetResponse.success(records);
    }

    @ApiDoc(description = "获取当前签到详情",role = RoleList.Teacher)
    @GetMapping("/getSignInDetail")
    public BaseResponse getSignInDetail(){
        if (!redisTemplate.hasKey(SessionUtil.getInstance().getIdNumber()))
            return RetResponse.error("当前没有正在进行的签到");
        String uniqueId=(String)redisTemplate.opsForValue().get(SessionUtil.getInstance().getIdNumber());
        Map<Object,Object> attendanceMap=redisTemplate.opsForHash().entries(uniqueId+"_list");
        List<Map<String,Object>> attendanceList=new ArrayList<>();
        for (Map.Entry<Object,Object> entry:attendanceMap.entrySet()){
            Attendance attendance=(Attendance)entry.getValue();
            if (attendance.getState().equals(AttendanceState.SIGNED.getCode())) {
                User user=userService.findUserById(attendance.getStudent().getId());
                Map<String,Object> map=transferSignInDetail(user,attendance.getCreateTime());
                attendanceList.add(map);
            }
        }
        return RetResponse.success(attendanceList);
    }

    @ApiDoc(description = "获取教师发起的签到的统计情况",role = RoleList.Teacher)
    @GetMapping("/getRecordOfTeacher")
    public BaseResponse getRecordOfSignInCode(){
        return RetResponse.success(attendanceService.getRecordOfTeacher(SessionUtil.getInstance().getUser().getId()));
    }

    @ApiDoc(description = "根据签到码获取签到情况",role = RoleList.Teacher)
    @GetMapping("/getRecordByUniqueId")
    public BaseResponse getRecordByUniqueId(String uniqueId){
        List<Attendance> attendances=attendanceService.getRecordsByUniqueId(uniqueId);
        List<AttendanceRecord> records=new ArrayList<>();
        for (Attendance attendance:attendances)
            records.add(new AttendanceRecord(attendance.getId(),attendance.getState(),attendance.getStudent().getName(),attendance.getCreateTime()));
        return RetResponse.success(records);
    }

    @ApiDoc(description = "获取今日课堂签到情况",role = {RoleList.Admin,RoleList.Instructor})
    @GetMapping("/getAttendanceOfToday")
    public BaseResponse getAttendanceOfToday(){
        if (SessionUtil.getInstance().getUser().getRole().getName().equals("admin"))
            return RetResponse.success(attendanceService.getRecordOfToday());
        else {
            Long majorId=userService.findMajorByUserId(SessionUtil.getInstance().getUser().getId()).getId();
            return RetResponse.success(attendanceService.getRecordOfTodayByMajorId(majorId));
        }
    }

    private Map<String,Object> transferSignInDetail(User user,Date time){
        Map<String,Object> map=new HashMap<>();
        map.put("id",user.getId());
        map.put("name",user.getName());
        map.put("idNumber",user.getIdNumber());
        map.put("time",DateUtil.dateToStr("yyyy-MM-dd HH:mm:ss",time));
        return map;
    }

}
