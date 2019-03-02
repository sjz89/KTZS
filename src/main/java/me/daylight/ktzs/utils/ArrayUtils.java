package me.daylight.ktzs.utils;

import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * @author Daylight
 * @date 2019/02/21 22:27
 */
public class ArrayUtils {
    public static boolean isNullOrEmpty(@Nullable List list){
        if (Objects.isNull(list))
            return true;
        return list.size() == 0;
    }
}
