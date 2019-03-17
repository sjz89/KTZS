package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.LeaveNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

/**
 * @author Daylight
 * @date 2019/03/16 21:58
 */
public interface LeaveNoteRepository extends JpaRepository<LeaveNote,Long> {
    @Modifying
    @Transactional
    @Query(value = "update leave_note set state=?2 where id=?1 ",nativeQuery = true)
    void changeState(Long id,int state);
}
