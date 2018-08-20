package dwayne.shim.gonggochatni.crawler.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {

    public static String getTimestampDaysBefore(int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, days * -1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(cal.getTimeInMillis());
    }

    public static String getTimestampMinutesBefore(int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, minutes * -1);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return formatter.format(cal.getTimeInMillis());
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getTimestampMinutesBefore(20));
    }
}
