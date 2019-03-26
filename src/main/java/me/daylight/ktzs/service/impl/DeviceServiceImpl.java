package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.DeviceReplaceRepository;
import me.daylight.ktzs.model.dao.DeviceRepository;
import me.daylight.ktzs.model.dao.UserRepository;
import me.daylight.ktzs.model.entity.Device;
import me.daylight.ktzs.model.entity.DeviceReplace;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author daylight
 * @date 2019/03/25 23:49
 */
@Service
public class DeviceServiceImpl implements DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceReplaceRepository deviceReplaceRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Device save(Device device) {
        return deviceRepository.saveAndFlush(device);
    }

    @Override
    public boolean isDeviceExist(User user) {
        return deviceRepository.existsDeviceByStudent(user);
    }

    @Override
    public String findImeiByStudent(Long userId) {
        return deviceRepository.findImeiByStudent(userId);
    }

    @Override
    public void deviceReplace(String imei, User student) {
        DeviceReplace deviceReplace=new DeviceReplace();
        deviceReplace.setStudent(student);
        deviceReplace.setImei(imei);
        deviceReplaceRepository.saveAndFlush(deviceReplace);
    }

    @Override
    public boolean isDeviceReplacing(User student) {
        return deviceReplaceRepository.existsByStudentAndState(student,0);
    }

    @Override
    public void changeReplacedIMEI(String imei, Long userId) {
        deviceReplaceRepository.changeReplacedIMEI(imei, userId);
    }

    @Override
    public Page<DeviceReplace> getDeviceReplaceList(int page,int limit) {
        Pageable pageable= PageRequest.of(page-1,limit);
        return deviceReplaceRepository.findAllOrderByCreateTimeDesc(pageable);
    }

    @Override
    public Page<DeviceReplace> getDeviceReplaceListByMajor(int page, int limit, Long userId) {
        Pageable pageable= PageRequest.of(page-1,limit);
        List<User> students=userRepository.findStudents(userId);
        return deviceReplaceRepository.findAllByStudentInOrderByCreateTimeDesc(students,pageable);
    }

    @Override
    public void changeReplaceState(int state, Long id) {
        deviceReplaceRepository.changeReplaceState(state, id);
    }

    @Override
    public void changeIMEI(String imei,Long userId) {
        deviceRepository.changeIMEI(imei, userId);
    }

    @Override
    public DeviceReplace getDeviceReplac(Long replaceId) {
        return deviceReplaceRepository.getOne(replaceId);
    }

    @Override
    public int countDeviceReplace() {
        return deviceReplaceRepository.countAllByState(0);
    }

    @Override
    public int countDeviceReplaceByStudentIn(List<User> students) {
        return deviceReplaceRepository.countAllByStateAndStudentIn(0,students);
    }


}
