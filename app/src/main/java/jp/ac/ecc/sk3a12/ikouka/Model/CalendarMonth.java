package jp.ac.ecc.sk3a12.ikouka.Model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import jp.ac.ecc.sk3a12.ikouka.Model.CalendarDay;

public class CalendarMonth {
    private int year;
    private int month;
    private ArrayList<CalendarDay> days;
    private int offset;

    public CalendarMonth(int year, int month) {
        this.year = year;
        this.month = month - 1;
        build();
    }

    public int getOffset() {
        return offset;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void build() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        cal.set(year, month, 1);

        this.offset = cal.get(Calendar.DAY_OF_WEEK);
    }
}
