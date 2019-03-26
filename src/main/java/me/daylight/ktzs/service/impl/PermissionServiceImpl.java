package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.PermissionRepository;
import me.daylight.ktzs.model.entity.Permission;
import me.daylight.ktzs.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/27 15:13
 */
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Override
    public Permission addPermission(Permission permission) {
        return permissionRepository.saveAndFlush(permission);
    }

    @Override
    public void delPermission(Permission permission) {
        permissionRepository.delete(permission);
    }

    @Override
    public boolean isPermissionExist(String path) {
        return permissionRepository.existsPermissionByPath(path);
    }

    @Override
    public List<Permission> findAllPermission() {
        return permissionRepository.findAll();
    }

    @Override
    public List<Permission> findPermissionsByPaths(List<String> paths) {
        return permissionRepository.findPermissionsByPathIn(paths);
    }

    @Override
    public List<Permission> findNotOwnedPermissionsByRole(List<Permission> ownedPermissions) {
        List<Long> ids=new ArrayList<>();
        for (Permission permission:ownedPermissions){
            ids.add(permission.getId());
        }
        return permissionRepository.findPermissionsByIdNotIn(ids);
    }

    @Override
    public void addAll(List<Permission> permissions) {
        permissionRepository.saveAll(permissions);
    }
}
