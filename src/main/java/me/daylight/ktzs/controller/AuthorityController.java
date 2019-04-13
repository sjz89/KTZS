package me.daylight.ktzs.controller;

import me.daylight.ktzs.annotation.ApiDoc;
import me.daylight.ktzs.authority.Unlimited;
import me.daylight.ktzs.model.dto.Api;
import me.daylight.ktzs.model.dto.BaseResponse;
import me.daylight.ktzs.model.entity.*;
import me.daylight.ktzs.model.enums.RoleList;
import me.daylight.ktzs.service.DeviceService;
import me.daylight.ktzs.service.PermissionService;
import me.daylight.ktzs.service.RoleService;
import me.daylight.ktzs.service.UserService;
import me.daylight.ktzs.utils.RetResponse;
import me.daylight.ktzs.authority.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Daylight
 * @date 2019/01/30 00:00
 */
@RestController
@RequestMapping("/authority")
public class AuthorityController {
    @Autowired
    private List<Api> apiDoc;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private PermissionService permissionService;

    @ApiDoc(description = "获取全部接口信息")
    @GetMapping(value = "/getAllUrl")
    public BaseResponse getAllUrl() {
        return RetResponse.success(apiDoc);
    }

    @Unlimited
    @ApiDoc(description = "用户登录",role = RoleList.Unlimited)
    @PostMapping("/login")
    public BaseResponse login(String idNumber, String password){
        if (!userService.isUserExist(idNumber))
            return RetResponse.error("用户不存在");
        if (!userService.checkPassword(idNumber, password))
            return RetResponse.error("账号密码不匹配");
        User user=userService.findUserByIdNumber(idNumber);
        //禁止客户端登陆管理员、辅导员账号
        if (SessionUtil.getInstance().isMobile()&&(user.getRole().getName().equals("admin")||user.getRole().getName().equals("instructor")))
            return RetResponse.error("无法在客户端登陆管理员、辅导员账号");
        //禁止学生、教师在网页端登陆
        if ((user.getRole().getName().equals("teacher")||user.getRole().getName().equals("student"))&&!SessionUtil.getInstance().isMobile())
            return RetResponse.error("请在客户端登陆教师、学生账号");
        if (user.getRole().getName().equals("student")){
            if (!deviceService.isDeviceExist(user)) {
                Device device=new Device();
                device.setImei(SessionUtil.getInstance().getIMEI());
                device.setStudent(user);
                deviceService.save(device);
            }
            else if (!SessionUtil.getInstance().getIMEI().equals(deviceService.findImeiByStudent(user.getId())))
                return RetResponse.error("Illegal Device");
        }

        Permission[] permissions=roleService.findPermissionsByRole(user.getRole().getName());
        SessionUtil.getInstance().setSessionMap(user,permissions);
        //设置Session过期时间
        SessionUtil.getInstance().setSessionTimeout();
        return RetResponse.success(user.getRole().getName());
    }

    @Unlimited
    @ApiDoc(description = "用户注册",role = RoleList.Unlimited)
    @PostMapping("/register")
    public BaseResponse register(@RequestBody User user,Long majorId){
        //防止无权限用户注册管理员账号
        if (!SessionUtil.getInstance().checkAuth("/authority/**")&&user.getRole().getName().equals("admin"))
            return RetResponse.unauthorized();
        if (userService.isUserExist(user.getIdNumber()))
            return RetResponse.error("用户已存在");
        user=userService.saveUser(user);
        if (majorId!=null){
            if (!userService.isMajorExist(new Major(majorId,null)))
                return RetResponse.error("专业不存在");
            Major major=userService.findMajorById(majorId);
            if (major.getUsers()==null)
                major.setUsers(new ArrayList<>());
            major.getUsers().add(user);
            userService.saveMajor(major);
        }
        return RetResponse.success(user.getId());
    }

    @Unlimited
    @ApiDoc(description = "用户登出",role = RoleList.Unlimited)
    @GetMapping("/logout")
    public BaseResponse logout(){
        SessionUtil.getInstance().logout();
        return RetResponse.success();
    }

    @ApiDoc(description = "删除用户")
    @PostMapping("/delUser")
    public BaseResponse delUser(User user){
        if (!userService.isUserExist(user.getIdNumber()))
            return RetResponse.error("用户不存在");
        userService.delUser(user);
        return RetResponse.success();
    }

    @ApiDoc(description = "根据角色获取用户信息")
    @GetMapping("/getUsersByRole")
    public BaseResponse getUsersByRole(int page,int limit,Role role){
        if (role.getId()!=0&&!roleService.isRoleExist(role))
            return RetResponse.error("角色不存在");
        Map<String,Object> objectMap=new HashMap<>();
        Page<User> users;
        if (role.getId()==0){
            users=userService.findUsersByRoleIsNull(page, limit);
        }else
            users=userService.findUsersByRole(page, limit, role);
        for (User user:users)
            user.setRole(null);
        objectMap.put("list",users.getContent());
        objectMap.put("count",users.getTotalElements());
        return RetResponse.success(objectMap);
    }

    @ApiDoc(description = "添加角色")
    @PostMapping("/addRole")
    public BaseResponse addRole(@RequestBody Role role){
        if (roleService.isRoleExist(role))
            return RetResponse.error("角色已存在");
        return RetResponse.success(roleService.saveRole(role).getId());
    }

    @ApiDoc(description = "删除角色")
    @PostMapping("/delRole")
    public BaseResponse delRole(Role role){
        if (!roleService.isRoleExist(role))
            return RetResponse.error("角色不存在");
        roleService.delRole(role);
        return RetResponse.success();
    }

    @ApiDoc(description = "获取全部角色")
    @GetMapping("/getAllRole")
    public BaseResponse getAllRole(){
        return RetResponse.success(roleService.findAllRole());
    }

    @ApiDoc(description = "设置用户角色")
    @PostMapping("/setRole")
    public BaseResponse setRole(String idNumber,Long roleId){
        if (!userService.isUserExist(idNumber))
            return RetResponse.error("用户不存在");
        if (!roleService.isRoleExist(new Role(roleId,null)))
            return RetResponse.error("角色不存在");
        userService.setRole(idNumber,roleService.findRoleById(roleId));
        return RetResponse.success();
    }

    @ApiDoc(description = "添加权限")
    @PostMapping("/addPermission")
    public BaseResponse addPermission(@RequestBody Permission permission){
        if (permissionService.isPermissionExist(permission.getPath()))
            return RetResponse.error("权限已存在");
        return RetResponse.success(permissionService.addPermission(permission).getId());
    }

    @ApiDoc(description = "删除权限")
    @PostMapping("/delPermission")
    public BaseResponse delPermission(Permission permission){
        if (!permissionService.isPermissionExist(permission.getPath()))
            return RetResponse.error("权限不存在");
        permissionService.delPermission(permission);
        return RetResponse.success();
    }

    @ApiDoc(description = "获取全部权限")
    @GetMapping("/getAllPermission")
    public BaseResponse getAllPermission(){
        return RetResponse.success(permissionService.findAllPermission());
    }

    @ApiDoc(description = "设置角色权限")
    @PostMapping("/setPermissions")
    public BaseResponse setPermissions(Long roleId,@RequestBody List<String> paths){
        if (!roleService.isRoleExist(new Role(roleId,null)))
            return RetResponse.error("角色不存在");
        Role role=roleService.findRoleById(roleId);
        role.setPermissions(permissionService.findPermissionsByPaths(paths));
        roleService.saveRole(role);
        return RetResponse.success();
    }

    @ApiDoc(description = "根据角色获取未拥有的权限")
    @GetMapping("/getNotOwnedPermissionsByRole")
    public BaseResponse getNotOwnedPermissions(Long roleId){
        if (!roleService.isRoleExist(new Role(roleId,null)))
            return RetResponse.error("角色不存在");
        Role role=roleService.findRoleById(roleId);
        return RetResponse.success(permissionService.findNotOwnedPermissionsByRole(role.getPermissions()));
    }

    @ApiDoc(description = "批量生成学生账号")
    @PostMapping("/generateStudents")
    public BaseResponse generateStudents(int year,Long majorId,int idStart,int idEnd){
        List<User> users=new ArrayList<>();
        Major major=userService.findMajorById(majorId);
        Role role=roleService.findRoleByName("student");
        for (int i=idStart;i<=idEnd;i++){
            User user=new User();
            user.setIdNumber(year +String.format("%02d",majorId)+String.format("%03d",i));
            user.setName("generate_"+String.format("%03d",i));
            user.setPassword("123456");
            user.setRole(role);
            users.add(user);
        }
        userService.saveUsers(users);
        major.getUsers().addAll(users);
        userService.saveMajor(major);
        return RetResponse.success();
    }

    @ApiDoc(description = "从ApiDoc导入权限")
    @GetMapping("/importFromApiDoc")
    public BaseResponse importFromApiDoc(){
        List<Permission> permissions=new ArrayList<>();
        for (Api api:apiDoc){
            if (api.getRole().get(0).equals(RoleList.Unlimited.name()))
                continue;
            Permission permission=new Permission();
            permission.setPath(api.getUrl());
            permission.setDescription(api.getDescription());
            permissions.add(permission);
        }
        permissionService.addAll(permissions);
        return RetResponse.success();
    }

    @ApiDoc(description = "学生申请更换绑定设备",role = RoleList.Unlimited)
    @PostMapping("/deviceReplace")
    @Unlimited
    public BaseResponse deviceReplace(String imei,String idNumber){
        User user=userService.findUserByIdNumber(idNumber);
        if (deviceService.isDeviceReplacing(user))
            deviceService.changeReplacedIMEI(imei,user.getId());
        else
            deviceService.deviceReplace(imei,user);
        return RetResponse.success();
    }

    @ApiDoc(description = "获取更换设备请求列表",role = {RoleList.Admin,RoleList.Instructor})
    @GetMapping("/getDeviceReplaceList")
    public BaseResponse getDeviceReplaceList(int page,int limit){
        Map<String,Object> objectMap=new HashMap<>();
        Page<DeviceReplace> replaces;
        if (SessionUtil.getInstance().getUser().getRole().getName().equals("admin"))
            replaces=deviceService.getDeviceReplaceList(page, limit);
        else
            replaces=deviceService.getDeviceReplaceListByMajor(page,limit,SessionUtil.getInstance().getUser().getId());
        for (DeviceReplace deviceReplace:replaces.getContent()){
            deviceReplace.getStudent().setRole(null);
        }
        objectMap.put("list",replaces.getContent());
        objectMap.put("count",replaces.getTotalElements());
        return RetResponse.success(objectMap);
    }

    @ApiDoc(description = "更改设备更换申请状态",role = {RoleList.Admin,RoleList.Instructor})
    @PostMapping("/changeDeviceReplaceState")
    public BaseResponse changeDeviceReplaceState(int state,Long id){
        deviceService.changeReplaceState(state, id);
        if (state==1) {
            DeviceReplace deviceReplace=deviceService.getDeviceReplac(id);
            deviceService.changeIMEI(deviceReplace.getImei(),deviceReplace.getStudent().getId());
        }
        return RetResponse.success();
    }
}
