package com.walfud.sir.engine;

/**
 * Created by walfud on 2016/12/13.
 */

public class Record {
    public String actionName;
    public long timestamp;

    public Record(String actionName, long timestamp) {
        this.actionName = actionName;
        this.timestamp = timestamp;
    }
}