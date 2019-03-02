package me.daylight.ktzs.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author daylight
 * @date 2019/02/28 23:02
 */
public class DateUtil {
    public static String dateToStr(String format,Date date){
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
