package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Device;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * @author daylight
 * @date 2019/03/25 23:49
 */
@SuppressWarnings("ALL")
public interface DeviceRepository extends JpaRepository<Device,Long> {
    boolean existsDeviceByStudent(User student);

    @Query(value = "select imei from device where student_id=?1",nativeQuery = true)
    String findImeiByStudent(Long userId);

    @Modifying
    @Transactional
    @Query(value = "update device set imei=?1 where student_id=?2",nativeQuery = true)
    void changeIMEI(String imei,Long userId);
}
