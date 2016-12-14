package com.walfud.sir.engine;

import android.os.Handler;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.walfud.sir.engine.filter.ActionFilter;
import com.walfud.sir.engine.filter.NodeFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by walfud on 2016/12/13.
 */

public abstract class Action {
    public static final String TAG = "Action";

    public String actionName;
    public List<ActionFilter> filterList;
    public AccessibilityNodeInfo lastNode0;     // `nodeInfoList.get(0)` at latest node filter
    public volatile boolean finish;
    public Action() {
        this("");
    }

    public <T extends ActionFilter> Action(String actionName, T... filters) {
        this.actionName = actionName;
        this.filterList = Arrays.<ActionFilter>asList(filters);
    }

    public boolean filter(AccessibilityEvent accessibilityEvent) {
        for (int i = 0; i < filterList.size(); i++) {
            ActionFilter filter = filterList.get(i);
            if (!filter.filter(accessibilityEvent)) {
                Log.v(TAG, String.format("drop(%s:%s): %s", actionName, filter.toString(), accessibilityEvent.toString()));
                return false;
            }

            if (i == filterList.size() - 1 && filter instanceof NodeFilter) {
                lastNode0 = ((NodeFilter) filter).nodeList.get(0);
            }
        }

        Log.v(TAG, String.format("accept(%s): %s", actionName, accessibilityEvent.toString()));
        return true;
    }

    public abstract boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0);

    public static abstract class Delay {
        Engine engine;
        AccessibilityEvent accessibilityEvent;
        AccessibilityNodeInfo lastNode0;
        public Delay(Engine engine, AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
            this.engine = engine;
            this.accessibilityEvent = accessibilityEvent;
            this.lastNode0 = lastNode0;
        }

        public abstract boolean delay(AccessibilityEvent accessibilityEvent, final AccessibilityNodeInfo lastNode0);
    }
    public boolean delay(final Delay delay) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (delay.delay(delay.accessibilityEvent, delay.lastNode0)) {
                    delay.engine.remove(delay.accessibilityEvent);
                }
            }
        }, 1000);
        return false;
    }
}