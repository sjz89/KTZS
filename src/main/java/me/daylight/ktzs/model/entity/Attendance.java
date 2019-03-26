package me.daylight.ktzs.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Daylight
 * @date 2019/02/18 02:03
 */
@Entity
public class Attendance implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uniqueId;

    @ManyToOne
    private Course course;

    @ManyToOne
    private User student;

    private Integer state;

    @JsonFormat(timezone = "GMT+8",pattern ="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonIgnore
    public static Attendance generateValue(String uniqueId, Course course, User student,int state){
        Attendance attendance=new Attendance();
        attendance.uniqueId=uniqueId;
        attendance.course=course;
        attendance.student=student;
        attendance.state=state;
        attendance.createTime=new Date();
        return attendance;
    }
}
