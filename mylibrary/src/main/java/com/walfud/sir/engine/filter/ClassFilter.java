package com.walfud.sir.engine.filter;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by walfud on 2016/12/13.
 */

public class ClassFilter extends ActionFilter {
    private String className;

    public ClassFilter(String className) {
        this.className = className;
    }

    @Override
    public boolean filter(AccessibilityEvent accessibilityEvent) {
        return TextUtils.equals(className, accessibilityEvent.getClassName());
    }
}