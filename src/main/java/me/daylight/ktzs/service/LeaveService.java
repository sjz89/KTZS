package me.daylight.ktzs.service;

import me.daylight.ktzs.model.entity.LeaveNote;

/**
 * @author Daylight
 * @date 2019/03/16 21:59
 */
public interface LeaveService {
    LeaveNote save(LeaveNote leaveNote);

    void changeState(Long id,int state);

    boolean isLeaveNoteExist(Long id);
}
