package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;


/**
 * @author Daylight
 * @date 2019/01/31 14:31
 */
public interface MajorRepository extends JpaRepository<Major,Long> {

    boolean existsMajorByName(String name);

    @Query(value = "select count(*) from major_users where users_id=?1",nativeQuery = true)
    int isUserSetMajor(Long userId);

    @Modifying
    @Transactional
    @Query(value = "insert into major_users(users_id, major_id) values (?1,?2)",nativeQuery = true)
    void setMajor(Long userId,Long majorId);

    @Modifying
    @Transactional
    @Query(value = "update major_users set major_id=?2 where users_id=?1",nativeQuery = true)
    void changeUserMajor(Long userId,Long majorId);

    @Query(value = "select major_users.users_id from major_users",nativeQuery = true)
    List<Long> findUserIds();
}
