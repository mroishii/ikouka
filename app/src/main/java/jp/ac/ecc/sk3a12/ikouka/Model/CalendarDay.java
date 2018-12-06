package jp.ac.ecc.sk3a12.ikouka.Model;

import java.util.ArrayList;

public class CalendarDay {
    private int year;
    private int month;
    private int day;
    private ArrayList<Event> events = new ArrayList();

    public CalendarDay(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public CalendarDay(int year, int month, int day, ArrayList<Event> events) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.events = events;
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

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        events.add(event);
    }
}
