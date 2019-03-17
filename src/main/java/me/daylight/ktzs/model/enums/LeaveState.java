package me.daylight.ktzs.model.enums;

/**
 * @author Daylight
 * @date 2019/03/16 22:04
 */
public enum LeaveState {
    WAITING(0),
    AGREE(1),
    DISAGREE(2);

    private int code;

    LeaveState(int code){
        this.code=code;
    }

    public int getCode(){
        return code;
    }
}
