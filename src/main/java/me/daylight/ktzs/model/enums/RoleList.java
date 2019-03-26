package me.daylight.ktzs.model.enums;

/**
 * @author daylight
 * @date 2019/03/20 23:13
 */
public enum RoleList {
    Unlimited("unlimited"),
    Admin("admin"),
    Instructor("instructor"),
    Teacher("teacher"),
    Student("student"),
    All("all");

    String name;

    RoleList(String name){
        this.name=name;
    }
}
