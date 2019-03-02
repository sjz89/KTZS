package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Daylight
 * @date 2019/01/25 14:55
 */
public interface RoleRepository extends JpaRepository<Role,Long> {
    boolean existsRoleByName(String name);

    Role findRoleByName(String name);
}
