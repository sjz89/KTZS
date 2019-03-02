package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.RoleRepository;
import me.daylight.ktzs.model.entity.Permission;
import me.daylight.ktzs.model.entity.Role;
import me.daylight.ktzs.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

/**
 * @author Daylight
 * @date 2019/01/27 15:13
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean isRoleExist(Role role) {
        if (Objects.isNull(role.getId()))
            return roleRepository.existsRoleByName(role.getName());
        return roleRepository.existsById(role.getId());
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.saveAndFlush(role);
    }

    @Override
    public void delRole(Role role) {
        roleRepository.delete(role);
    }

    @Override
    public Role findRoleById(Long id) {
        return roleRepository.getOne(id);
    }

    @Override
    public Role findRoleByName(String name) {
        return roleRepository.findRoleByName(name);
    }

    @Override
    public List<Role> findAllRole() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public Permission[] findPermissionsByRole(String roleName) {
        return roleRepository.findRoleByName(roleName).getPermissions().toArray(new Permission[0]);
    }
}
