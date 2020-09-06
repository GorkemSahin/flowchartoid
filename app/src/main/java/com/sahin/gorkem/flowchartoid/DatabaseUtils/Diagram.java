package com.sahin.gorkem.flowchartoid.DatabaseUtils;

/**
 * Created by Gorkem on 5/18/2018.
 */

public class Diagram {

    public static final String TABLE_NAME = "diagrams";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_DEST = "destination";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    private int id;
    private String name;
    private String data;
    private String destination;
    private String timestamp;


    // Create table SQL query
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_NAME + " TEXT,"
                    + COLUMN_DATA + " TEXT,"
                    + COLUMN_DEST + " TEXT,"
                    + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    public Diagram() {
    }

    public Diagram(int id, String name, String data, String destination, String timestamp) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.destination = destination;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}