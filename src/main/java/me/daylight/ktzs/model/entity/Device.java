package me.daylight.ktzs.model.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author daylight
 * @date 2019/03/25 23:46
 */
@Entity
public class Device implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imei;

    @OneToOne
    private User student;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }
}
