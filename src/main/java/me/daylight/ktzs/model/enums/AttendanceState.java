package me.daylight.ktzs.model.enums;

/**
 * @author Daylight
 * @date 2019/02/18 02:12
 */
public enum AttendanceState {
    LEAVE(2),

    SIGNED(1),

    UNSIGNED(0);

    private int state;

    AttendanceState(int state){
        this.state=state;
    }
    
    public int getCode(){
        return this.state;
    }
}
