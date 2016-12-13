package com.walfud.sir.engine.filter;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by walfud on 2016/12/13.
 */
public class PackageFilter extends ActionFilter {
    private String packageName;

    public PackageFilter(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean filter(AccessibilityEvent accessibilityEvent) {
        return TextUtils.equals(packageName, accessibilityEvent.getPackageName());
    }
}