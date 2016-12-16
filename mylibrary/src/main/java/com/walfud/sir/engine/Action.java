package com.walfud.sir.engine;

import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.walfud.sir.engine.filter.ActionFilter;
import com.walfud.sir.engine.filter.NodeFilter;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by walfud on 2016/12/13.
 */

public abstract class Action {
    public static final String TAG = "Action";

    public String actionName;
    public long delayMs;
    public List<ActionFilter> filterList;
    public AccessibilityNodeInfo lastNode0;     // `nodeInfoList.get(0)` at latest node filter
    public Queue<Action> actionQueue;
    public Action(String name) {
        this(name, -1);
    }
    public <T extends ActionFilter> Action(String name, T... filters) {
        this(name, -1, filters);
    }
    public <T extends ActionFilter> Action(String actionName, long delayMs, T... filters) {
        this.actionName = actionName;
        this.delayMs = delayMs;
        this.filterList = Arrays.<ActionFilter>asList(filters);
        this.actionQueue = new LinkedList<>();
        actionQueue.add(this);
    }

    public boolean filter(AccessibilityEvent accessibilityEvent) {
        Action thiz = actionQueue.peek();

        for (int i = 0; i < thiz.filterList.size(); i++) {
            ActionFilter filter = thiz.filterList.get(i);
            if (!filter.filter(accessibilityEvent)) {
                Log.v(TAG, String.format("drop(%s:%s): %s", thiz.actionName, filter.toString(), accessibilityEvent.toString()));
                return false;
            }

            if (i == thiz.filterList.size() - 1 && filter instanceof NodeFilter) {
                thiz.lastNode0 = ((NodeFilter) filter).nodeList.get(0);
            }
        }

        Log.v(TAG, String.format("accept(%s): %s", thiz.actionName, accessibilityEvent.toString()));
        return true;
    }

    public boolean handleProxy(final Engine engine, final AccessibilityEvent accessibilityEvent, final AccessibilityNodeInfo lastNode0) {
        final Action thiz = actionQueue.peek();

        if (thiz.delayMs > 0) {
            try {
                Thread.sleep(thiz.delayMs);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }

        if (thiz.handle(accessibilityEvent, lastNode0)) {
            actionQueue.remove();
            Log.d(TAG, String.format("emit: %s", thiz.actionName));
        }

        return actionQueue.isEmpty();
    }

    public void compose(Action... actions) {
        for (Action action : actions) {
            actionQueue.add(action);
        }
    }

    public Action thiz() {
        return actionQueue.peek();
    }

    public abstract boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0);
}