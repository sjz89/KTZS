package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.LeaveNote;
import me.daylight.ktzs.model.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/16 21:59
 */
public interface LeaveService {
    LeaveNote save(LeaveNote leaveNote);

    void changeState(Long id,int state,String remark);

    boolean isLeaveNoteExist(Long id);

    List<LeaveNote> getLeaveNotesByUser(User student);

    boolean isStudentLeave(Long studentId);

    Page<LeaveNote> getLeaveNoteByMajor(Long userId,int page,int limit);

    Page<LeaveNote> getAllLeaveNote(int page,int limit);

    int getLeaveCountOfToday();

    int getLeaveCountOfTodayByStudentIn(List<User> students);

    int getLeaveApplicationCount();

    int getLeaveApplicationCountByStudentIn(List<User> students);
}
