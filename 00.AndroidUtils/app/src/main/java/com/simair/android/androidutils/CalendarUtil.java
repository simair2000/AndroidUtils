package com.simair.android.androidutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by simair on 2016-04-06.
 */
public class CalendarUtil {

    private static volatile CalendarUtil instance;
    private Calendar cal;

    public static CalendarUtil getInstance() {
        if(instance == null) {
            synchronized (CalendarUtil.class) {
                instance = new CalendarUtil();
                instance.cal = Calendar.getInstance();
            }
        }
        return instance;
    }

    private CalendarUtil() {}

    public int getThisYear() {
        return cal.get(Calendar.YEAR);
    }

    public int getThisMonth() {
        return cal.get(Calendar.MONTH) + 1;
	// test for git
    }

    /**
     * 특정 년/월의 날수를 반환한다.
     * @param year
     * @param month
     * @return
     */
    public int getDaysOfParticularMonthYear(int year, int month) {
        GregorianCalendar calendar = new GregorianCalendar(year, month - 1, 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }


    /**
     * 만료 날짜랑 오늘 날짜를 비교하는 메소드.
     * 특정일(만료날짜)와 오늘의 날짜를 비교할 때도 쓸 수 있다.
     * 특정 만료 날짜에서 오늘의 날짜를 뺀다. 음수 or 0 :만료된 날짜 ,   양수 : 만료 이전 날짜.
     * @param expiredDay yyyyMMdd 예: 20160827 같이 날짜를 받는다.
     * @return
     */
    public long getSubtractFromExpiredDay(String expiredDay) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        int yyyy     = cal.get(Calendar.YEAR);    //현재 년도
        int MM        = cal.get(Calendar.MONTH);   //현재 달
        int dd        = cal.get(Calendar.DATE);    //현재 날짜
        int hh        = cal.get(Calendar.HOUR);    //현재 시간
        cal.set(yyyy, MM, dd); //현재 날짜 세팅


        String today = df.format(cal.getTime());

        Date expiredDate = null;
        Date endDate = null;

        long diff = 0;
        long diffDays = 0;
        long diffTime = 0;

        expiredDate = df.parse(expiredDay);
        endDate = df.parse(today);

        diff = expiredDate.getTime() - endDate.getTime();
        diffDays = diff / (24 * 60 * 60 * 1000);

        return diffDays;
    }
}
