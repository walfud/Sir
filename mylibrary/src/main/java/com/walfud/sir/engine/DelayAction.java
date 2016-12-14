package com.walfud.sir.engine;

import android.os.Handler;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.walfud.sir.engine.filter.ActionFilter;

/**
 * Created by walfud on 2016/12/14.
 */

public abstract class DelayAction extends Action {
    public static final String TAG = "DelayAction";

    public <T extends ActionFilter> DelayAction(String actionName, T... filters) {
        super(actionName, filters);
    }

    @Override
    public boolean handleProxy(final Engine engine, final AccessibilityEvent accessibilityEvent, final AccessibilityNodeInfo lastNode0) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (handle(accessibilityEvent, lastNode0)) {
                    engine.remove(accessibilityEvent);
                }
            }
        }, 1000);

        return false;
    }
}
