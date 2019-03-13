package me.daylight.ktzs.model.dto;

/**
 * @author Daylight
 * @date 2019/03/08 15:30
 */
public class CourseDto {
    private Long id;

    private String name;

    private String teacherName;

    private String time;

    public CourseDto(Long id,String name, String teacherName, String time) {
        this.id=id;
        this.name = name;
        this.teacherName = teacherName;
        this.time = time;
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
