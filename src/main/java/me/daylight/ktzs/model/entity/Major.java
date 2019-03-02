package me.daylight.ktzs.model.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/31 14:19
 */
@Entity
public class Major implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String name;

    @OneToMany
    private List<User> users;

    public Major(){}

    public Major(Long id,String name){
        this.id=id;
        this.name=name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
