package com.walfud.sir.engine.filter;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by walfud on 2016/12/13.
 */

public class NodeTextFilter extends NodeFilter {
    public static final String TAG = "NodeTextFilter";

    public NodeTextFilter(String obj) {
        super(obj, null);
    }

    public NodeTextFilter(String obj, PropFilter propFilter) {
        super(obj, propFilter);
    }

    @Override
    public List<AccessibilityNodeInfo> findMethod(AccessibilityNodeInfo nodeInfo, String obj) {
        return nodeInfo.findAccessibilityNodeInfosByText(obj);
    }

    @Override
    public String toString() {
        return String.format("%s:text(%s)", TAG, obj);
    }
}
