package com.walfud.sir.engine.filter;

import android.view.accessibility.AccessibilityEvent;

/**
 * Created by walfud on 2016/12/13.
 */

public abstract class ActionFilter {
    public abstract boolean filter(AccessibilityEvent accessibilityEvent);
}