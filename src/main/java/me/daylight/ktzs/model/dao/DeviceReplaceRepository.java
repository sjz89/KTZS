package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.DeviceReplace;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;

/**
 * @author daylight
 * @date 2019/03/26 10:30
 */
public interface DeviceReplaceRepository extends JpaRepository<DeviceReplace,Long> {
    boolean existsByStudentAndState(User student,int state);

    @Query(value = "update device_replace set imei=?1 where student_id=?2",nativeQuery = true)
    @Modifying
    @Transactional
    void changeReplacedIMEI(String imei,Long userId);

    @Query("select d from DeviceReplace d order by d.createTime desc ")
    Page<DeviceReplace> findAllOrderByCreateTimeDesc(Pageable pageable);

    Page<DeviceReplace> findAllByStudentInOrderByCreateTimeDesc(Collection<User> students,Pageable pageable);

    @Modifying
    @Transactional
    @Query("update DeviceReplace d set d.state=?1 where d.id=?2")
    void changeReplaceState(int state,Long id);

    int countAllByState(int state);

    int countAllByStateAndStudentIn(int state,Collection<User> students);
}
