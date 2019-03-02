package me.daylight.ktzs.model.enums;

/**
 * @author Daylight
 * @date 2019/02/18 02:12
 */
public enum AttendanceState {

    SIGNED(1),

    UNSIGNED(2);

    private int state;

    AttendanceState(int state){
        this.state=state;
    }
    
    public int getState(){
        return this.state;
    }
}
