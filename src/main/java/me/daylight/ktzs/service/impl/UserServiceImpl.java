package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.MajorRepository;
import me.daylight.ktzs.model.dao.RoleRepository;
import me.daylight.ktzs.model.dao.UserRepository;
import me.daylight.ktzs.model.entity.Major;
import me.daylight.ktzs.model.entity.Role;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/26 00:52
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MajorRepository majorRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean checkPassword(String idNumber, String password) {
        return userRepository.existsUserByIdNumberAndPassword(idNumber, password);
    }

    @Override
    public boolean isUserExist(String idNumber) {
        return userRepository.existsUserByIdNumber(idNumber);
    }

    @Override
    @Transactional
    public User findUserByIdNumber(String idNumber) {
        return userRepository.findUserByIdNumber(idNumber);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public Page<User> findUsersByRole(int page,int limit,Role role) {
        Pageable pageable= PageRequest.of(page-1,limit);
        return userRepository.findUsersByRoleOrderByIdNumberAsc(pageable,role);
    }

    @Override
    public Page<User> findUsersByRoleIsNull(int page, int limit) {
        Pageable pageable= PageRequest.of(page-1,limit);
        return userRepository.findUsersByRoleIsNullOrderByIdNumberAsc(pageable);
    }

    @Override
    public User saveUser(User user) {
        return userRepository.saveAndFlush(user);
    }

    @Override
    public void saveUsers(List<User> users) {
        userRepository.saveAll(users);
    }

    @Override
    public void delUser(User user) {
        userRepository.delete(user);
    }

    @Override
    public void setRole(String idNumber,Role role) {
        userRepository.setRole(idNumber, role);
    }

    @Override
    public Major findMajorById(Long id) {
        return majorRepository.getOne(id);
    }

    @Override
    public boolean isMajorExist(Major major) {
        if (major.getId()!=null)
            return majorRepository.existsById(major.getId());
        return majorRepository.existsMajorByName(major.getName());
    }

    @Override
    public Major saveMajor(Major major) {
        return majorRepository.saveAndFlush(major);
    }

    @Override
    public void delMajor(Major major) {
        majorRepository.delete(major);
    }

    @Override
    public List<Major> getMajorList() {
        return majorRepository.findAll();
    }

    @Override
    public List<User> findUsersByMajorNull() {
        List<Long> userIds=majorRepository.findUserIds();
        List<Role> roles=new ArrayList<>();
        roles.add(roleRepository.findRoleByName("student"));
        roles.add(roleRepository.findRoleByName("instructor"));
        if (userIds.size()==0)
            return userRepository.findUsersByRoleIn(roles);
        return userRepository.findUsersByIdNotInAndRoleIn(userIds,roles);
    }

    @Override
    public void changeUserMajor(Long userId, Long majorId) {
        if (majorRepository.isUserSetMajor(userId)==1)
            majorRepository.changeUserMajor(userId, majorId);
        else
            majorRepository.setMajor(userId, majorId);
    }

    @Override
    public List<User> findUsersByRole(Long roleId) {
        return userRepository.findUsersByRoleId(roleId);
    }

    @Override
    public List<User> findStudents(Long userId) {
        return userRepository.findStudents(userId);
    }

    @Override
    public Major findMajorByUserId(Long userId) {
        return majorRepository.findMajorByUserId(userId);
    }
}
