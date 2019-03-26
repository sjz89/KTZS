package me.daylight.ktzs.model.dao;

import me.daylight.ktzs.model.entity.LeaveNote;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/16 21:58
 */
public interface LeaveNoteRepository extends JpaRepository<LeaveNote,Long> {
    @Modifying
    @Transactional
    @Query(value = "update leave_note set state=?2,remark=?3,update_time=current_time where id=?1 ",nativeQuery = true)
    void changeState(Long id,int state,String remark);

    List<LeaveNote> findLeaveNotesByStudentOrderByState(User student);

    @Query(value = "select count(*) from leave_note where student_id=?1 and state=1 " +
            "and start_date < ?2 and end_date > ?2",nativeQuery = true)
    int isStudentLeave( Long studentId, Date date);

    Page<LeaveNote> findLeaveNotesByStudentInOrderByCreateTimeDesc(Collection<User> students, Pageable pageable);

    @Query("select l from LeaveNote l order by l.createTime desc")
    Page<LeaveNote> findAllOrderByCreateTimeDesc(Pageable pageable);

    int countLeaveNotesByStartDateBeforeAndEndDateAfterAndState(Date now,Date now2,int state);

    int countAllByStartDateBeforeAndEndDateAfterAndStateAndStudentIn(Date now,Date now2,int state,Collection<User> students);

    int countAllByState(int state);

    int countAllByStateAndStudentIn(int state,Collection<User> students);
}
