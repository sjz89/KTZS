package me.daylight.ktzs.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static int getDayOfWeek(){
        Calendar now = Calendar.getInstance();
        boolean isFirstSunday = (now.getFirstDayOfWeek() == Calendar.SUNDAY);
        int weekDay = now.get(Calendar.DAY_OF_WEEK);
        if(isFirstSunday){
            weekDay = weekDay - 1;
            if(weekDay == 0){
                weekDay = 7;
            }
        }
        return weekDay;
    }
}
