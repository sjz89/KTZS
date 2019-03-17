package me.daylight.ktzs.service.impl;

import me.daylight.ktzs.model.dao.LeaveNoteRepository;
import me.daylight.ktzs.model.entity.LeaveNote;
import me.daylight.ktzs.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Daylight
 * @date 2019/03/16 21:59
 */
@Service
public class LeaveServiceImpl implements LeaveService {
    @Autowired
    private LeaveNoteRepository leaveNoteRepository;


    @Override
    public LeaveNote save(LeaveNote leaveNote) {
        return leaveNoteRepository.saveAndFlush(leaveNote);
    }

    @Override
    public void changeState(Long id, int state) {
        leaveNoteRepository.changeState(id, state);
    }

    @Override
    public boolean isLeaveNoteExist(Long id) {
        return leaveNoteRepository.existsById(id);
    }


}
