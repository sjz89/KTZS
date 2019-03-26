package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.LeaveNoteRepository;
import me.daylight.ktzs.model.dao.UserRepository;
import me.daylight.ktzs.model.entity.LeaveNote;
import me.daylight.ktzs.model.entity.User;
import me.daylight.ktzs.model.enums.LeaveState;
import me.daylight.ktzs.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/16 21:59
 */
@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveNoteRepository leaveNoteRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public LeaveNote save(LeaveNote leaveNote) {
        return leaveNoteRepository.saveAndFlush(leaveNote);
    }

    @Override
    public void changeState(Long id, int state,String remark) {
        leaveNoteRepository.changeState(id, state,remark);
    }

    @Override
    public boolean isLeaveNoteExist(Long id) {
        return leaveNoteRepository.existsById(id);
    }

    @Override
    public List<LeaveNote> getLeaveNotesByUser(User student) {
        return leaveNoteRepository.findLeaveNotesByStudentOrderByState(student);
    }

    @Override
    public boolean isStudentLeave(Long studentId) {
        return leaveNoteRepository.isStudentLeave(studentId,new Date())!=0;
    }

    @Override
    public Page<LeaveNote> getLeaveNoteByMajor(Long userId,int page,int limit) {
        Pageable pageable= PageRequest.of(page-1,limit);
        List<User> students=userRepository.findStudents(userId);
        return leaveNoteRepository.findLeaveNotesByStudentInOrderByCreateTimeDesc(students,pageable);
    }

    @Override
    public Page<LeaveNote> getAllLeaveNote(int page, int limit) {
        Pageable pageable=PageRequest.of(page-1,limit);
        return leaveNoteRepository.findAllOrderByCreateTimeDesc(pageable);
    }

    @Override
    public int getLeaveCountOfToday() {
        return leaveNoteRepository.countLeaveNotesByStartDateBeforeAndEndDateAfterAndState(new Date(),new Date(), LeaveState.AGREE.getCode());
    }

    @Override
    public int getLeaveCountOfTodayByStudentIn(List<User> students) {
        return leaveNoteRepository.countAllByStartDateBeforeAndEndDateAfterAndStateAndStudentIn(new Date(),new Date(),LeaveState.AGREE.getCode(),students);
    }

    @Override
    public int getLeaveApplicationCount() {
        return leaveNoteRepository.countAllByState(LeaveState.WAITING.getCode());
    }

    @Override
    public int getLeaveApplicationCountByStudentIn(List<User> students) {
        return leaveNoteRepository.countAllByStateAndStudentIn(LeaveState.WAITING.getCode(),students);
    }


}
