package me.daylight.ktzs.annotation;

import me.daylight.ktzs.model.enums.RoleList;

import java.lang.annotation.*;

/**
 * @author Daylight
 * @date 2019/01/28 01:58
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiDoc {
    String description();
    RoleList[] role() default RoleList.Admin;
}
