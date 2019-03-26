package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.Device;
import me.daylight.ktzs.model.entity.DeviceReplace;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author daylight
 * @date 2019/03/25 23:49
 */
public interface DeviceService {
    Device save(Device device);

    boolean isDeviceExist(User user);

    String findImeiByStudent(Long userId);

    void deviceReplace(String imei, User student);

    boolean isDeviceReplacing(User student);

    void changeReplacedIMEI(String imei,Long userId);

    Page<DeviceReplace> getDeviceReplaceList(int page,int limit);

    Page<DeviceReplace> getDeviceReplaceListByMajor(int page,int limit,Long userId);

    void changeReplaceState(int state,Long id);

    void changeIMEI(String imei,Long userId);

    DeviceReplace getDeviceReplac(Long replaceId);

    int countDeviceReplace();

    int countDeviceReplaceByStudentIn(List<User> students);
}
