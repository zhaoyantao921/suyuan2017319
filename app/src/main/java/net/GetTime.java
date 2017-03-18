package net;

import java.text.SimpleDateFormat;
import java.util.Date;

/**获取当前时间的类
 */

public class GetTime {
    public static String getTime(){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String  time = formatter.format(curDate);
        return time;
    }

}
