package com.walfud.sir.engine.filter;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by walfud on 2016/12/13.
 */

public class TypeFilter extends ActionFilter {
    public static final String TAG = "TypeFilter";

    private int type;

    public TypeFilter(int type) {
        this.type = type;
    }

    @Override
    public boolean filter(AccessibilityEvent accessibilityEvent) {
        return type == accessibilityEvent.getEventType();
    }

    @Override
    public String toString() {
        return String.format("%s:%d", TAG, type);
    }
}
