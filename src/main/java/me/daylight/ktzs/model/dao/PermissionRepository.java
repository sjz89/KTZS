package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/25 14:56
 */
public interface PermissionRepository extends JpaRepository<Permission,Long> {
    boolean existsPermissionByPath(String path);

    List<Permission> findPermissionsByPathIn(Collection<String> paths);

    List<Permission> findPermissionsByIdNotIn(Collection<Long> ids);
}
