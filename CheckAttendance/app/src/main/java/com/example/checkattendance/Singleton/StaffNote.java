package com.example.checkattendance.Singleton;

public class StaffNote {
    private static StaffNote instance;
    private String ID;
    private int indexing;
    private String position;

    private StaffNote() {

    }

    public static StaffNote getInstance() {
        if (instance == null) {
            instance = new StaffNote();
        }
        return instance;
    }

    private void process_index_user() {
        if (ID.equals("NV01")) {
            indexing = 3;
        } else if (ID.equals("NV02")) {
            indexing = 4;
        } else if (ID.equals("NV04")) {
            indexing = 5;
        } else if (ID.equals("NV05")) {
            indexing = 6;
        }
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getID() {
        return ID;
    }

    public int getIndexing() {
        return indexing;
    }

    public void setID(String variable) {
        this.ID = variable;
        process_index_user();
    }
}