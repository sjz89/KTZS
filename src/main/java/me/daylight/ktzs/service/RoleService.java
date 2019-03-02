package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.Permission;
import me.daylight.ktzs.model.entity.Role;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/27 15:12
 */
public interface RoleService {
    boolean isRoleExist(Role role);

    Role saveRole(Role role);

    void delRole(Role role);

    Role findRoleById(Long id);

    Role findRoleByName(String name);

    List<Role> findAllRole();

    Permission[] findPermissionsByRole(String roleName);

}
