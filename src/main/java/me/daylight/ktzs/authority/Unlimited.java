package me.daylight.ktzs.authority;

import java.lang.annotation.*;

/**
 * @author Daylight
 * @date 2019/01/29 21:09
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Unlimited {

}
