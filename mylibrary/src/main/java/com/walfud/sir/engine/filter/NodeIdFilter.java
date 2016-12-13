package com.walfud.sir.engine.filter;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by walfud on 2016/12/13.
 */

public class NodeIdFilter extends NodeFilter {
    public NodeIdFilter(String obj) {
        super(obj, null);
    }

    public NodeIdFilter(String obj, PropFilter propFilter) {
        super(obj, propFilter);
    }

    @Override
    public List<AccessibilityNodeInfo> findMethod(AccessibilityNodeInfo nodeInfo, String obj) {
        return nodeInfo.findAccessibilityNodeInfosByViewId(obj);
    }
}
