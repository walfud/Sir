package com.walfud.sir.engine.filter;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by walfud on 2016/12/13.
 */
public class NotNullFilter extends ActionFilter {
    @Override
    public boolean filter(AccessibilityEvent accessibilityEvent) {
        return accessibilityEvent != null
                && !TextUtils.isEmpty(accessibilityEvent.getPackageName())
                && !TextUtils.isEmpty(accessibilityEvent.getClassName())
                && accessibilityEvent.getSource() != null;      // TODO: recycle
    }
}