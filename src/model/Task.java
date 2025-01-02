package model;

import java.sql.Timestamp;

public class Task {
    private int id;
    private String title;
    private Timestamp dueTime;
    private String note;
    public Task( int id, String title, Timestamp dueTime, String note) {
        this.id = id;
        this.title = title;
        this.dueTime = dueTime;
        this.note = note;
    }
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Timestamp getDueTime() {
        return dueTime;
    }

    public String getNote(){
        return note;
        }

    @Override
    public String toString() {

        return id + " - " + title + "   |   " + dueTime + "   |   " + note;
    }
}

