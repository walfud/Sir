package com.walfud.sir.engine.filter;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by walfud on 2016/12/13.
 */

public abstract class NodeFilter extends ActionFilter {
    public interface PropFilter {
        boolean propFilter(List<AccessibilityNodeInfo> nodeList);
    }
    public String obj;       // Node id or text
    public PropFilter propFilter;
    public List<AccessibilityNodeInfo> nodeList;
    public NodeFilter(String obj, PropFilter propFilter) {
        this.obj = obj;
        this.propFilter = propFilter;
    }

    @Override
    public boolean filter(AccessibilityEvent accessibilityEvent) {
        List<AccessibilityNodeInfo> nodes = findMethod(accessibilityEvent.getSource(), obj);
        if (!nodes.isEmpty()) {
            nodeList = nodes;
            return propFilter == null ? true : propFilter.propFilter(nodes);
        }

        nodeList = new ArrayList<>();
        return false;
    }

    public abstract List<AccessibilityNodeInfo> findMethod(AccessibilityNodeInfo nodeInfo, String obj);
}