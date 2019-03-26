package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Role;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/25 14:55
 */
public interface UserRepository extends JpaRepository<User,Long> {
    boolean existsUserByIdNumberAndPassword(String idNumber,String password);

    boolean existsUserByIdNumber(String idNumber);

    User findUserByIdNumber(String idNumber);

    Page<User> findUsersByRoleOrderByIdNumberAsc(Pageable pageable,Role role);

    Page<User> findUsersByRoleIsNullOrderByIdNumberAsc(Pageable pageable);

    @Modifying
    @Transactional
    @Query("update User u set u.role=?2 where u.idNumber=?1")
    void setRole(String idNumber, Role role);

    @Query(value = "select * from user where id not in ?1 and role_id in ?2",nativeQuery = true)
    List<User> findUsersByIdNotInAndRoleIn(Collection ids,Collection<Role> roles);

    List<User> findUsersByRoleIn(Collection<Role> roles);

    @Query(value = "select * from user where role_id=?1",nativeQuery = true)
    List<User> findUsersByRoleId(Long roleId);

    @Query(value = "select * from user where role_id=(select role.id from role where role.name='student') and user.id not in " +
            "(select course_students.students_id from course_students where course_id=?1)",nativeQuery = true)
    List<User> findStudentUnChoose(Long courseId);

    @Query(value = "select * from user where role_id=(select role.id from role where role.name='student') and id in " +
            "(select users_id from major_users where major_id=(select major_id from major_users where users_id=?1)) order by id_number",nativeQuery = true)
    List<User> findStudents(Long userId);
}
