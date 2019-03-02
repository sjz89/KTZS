package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.Major;
import me.daylight.ktzs.model.entity.Role;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/26 00:51
 */
public interface UserService {
    boolean checkPassword(String idNumber,String password);

    boolean isUserExist(String idNumber);

    User findUserByIdNumber(String idNumber);

    Page<User> findUsersByRole(int page,int limit,Role role);

    Page<User> findUsersByRoleIsNull(int page,int limit);

    User saveUser(User user);

    void saveUsers(List<User> users);

    void delUser(User user);

    void setRole(String idNumber,Role role);

    Major findMajorById(Long id);

    boolean isMajorExist(Major major);

    Major saveMajor(Major major);

    void delMajor(Major major);

    List<Major> getMajorList();

    List<User> findUsersByMajorNull();

    void changeUserMajor(Long userId,Long majorId);
}
