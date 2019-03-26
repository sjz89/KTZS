package me.daylight.ktzs.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @author Daylight
 * @date 2019/03/07 23:24
 */
public class AttendanceRecord {
    private Long id;

    private String studentName;

    private String courseName;

    private int state;

    @JsonFormat(timezone = "GMT+8",pattern ="yyyy-MM-dd HH:mm:ss")
    private Date time;

    public AttendanceRecord(Long id,String courseName, int state, Date time) {
        this.id=id;
        this.courseName = courseName;
        this.state = state;
        this.time = time;
    }

    public AttendanceRecord(Long id,int state,String studentName,Date time){
        this.id=id;
        this.studentName = studentName;
        this.state = state;
        this.time = time;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
