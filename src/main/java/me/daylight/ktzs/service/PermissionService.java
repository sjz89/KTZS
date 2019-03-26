package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.Permission;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/27 15:13
 */
public interface PermissionService {
    Permission addPermission(Permission permission);

    void delPermission(Permission permission);

    boolean isPermissionExist(String path);

    List<Permission> findAllPermission();

    List<Permission> findPermissionsByPaths(List<String> paths);

    List<Permission> findNotOwnedPermissionsByRole(List<Permission> ownedPermissions);

    void addAll(List<Permission> permissions);
}
