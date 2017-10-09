package com.gmail.fomichov.m.youtubeanalytics.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyDateUtils {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS"); // задаем формат даты
    private static Calendar calendar = Calendar.getInstance(); // подключаем календарь

    private MyDateUtils() {
    }

    public static String getCurrentDate() {
        return dateFormat.format(calendar.getTime());
    }

    // сравниваем две даты
    public static Boolean datesComparing(Date dateStart, Date dateEnd) {
        return (dateStart.getTime() < dateEnd.getTime()) ;
    }

    // конвентируем дату с текстового формата в обьект класса Date
    public static Date convertStringToDate(String date) throws ParseException {
        calendar.setTime(dateFormat.parse(date));
        return calendar.getTime();
    }

    public static Date getYearMonthDay2(Date date) throws ParseException {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.HOUR_OF_DAY, 0);
        return c.getTime();
    }

}
