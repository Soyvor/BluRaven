package com.exuberant.ah2019;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Utils {

    public static List<String> getCurrentTime(){
        List<String> timeList = new ArrayList<>();
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat(" hh:mm a");
        timeList.add(simpleDateFormat.format(date));
        timeList.add(simpleTimeFormat.format(date));
        return timeList;
    }

}
