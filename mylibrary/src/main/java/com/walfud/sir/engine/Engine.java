package com.walfud.sir.engine;

import android.view.accessibility.AccessibilityEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by walfud on 2016/12/13.
 */

public class Engine {
    public static final String TAG = "Engine";

    protected Queue<Action> mHandlerQueue;
    protected List<Record> mRecordList;

    public Engine(Action... actions) {
        mHandlerQueue = new LinkedList<>();
        for (Action action : actions) {
            mHandlerQueue.add(action);
        }
        mRecordList = new ArrayList<>();
    }

    public void dispatchEvent(AccessibilityEvent accessibilityEvent) {
        Action action = mHandlerQueue.peek();
        if (action == null) {
            return;
        }

        if (action.filter(accessibilityEvent)) {
            if (action.handleProxy(this, accessibilityEvent, action.thiz().lastNode0)) {
                mHandlerQueue.remove();
            }
        }
    }
}