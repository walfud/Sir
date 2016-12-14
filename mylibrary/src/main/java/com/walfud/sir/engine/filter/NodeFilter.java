package com.walfud.sir.engine.filter;

import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by walfud on 2016/12/13.
 */

public class NodeFilter extends ActionFilter {
    public static final String TAG = "NodeFilter";

    public interface PropFilter {
        boolean propFilter(List<AccessibilityNodeInfo> nodeList);
    }
    public String idOrText;       // Node id or text
    public PropFilter propFilter;
    public List<AccessibilityNodeInfo> nodeList;

    public NodeFilter(String idOrText) {
        this(idOrText, null);
    }

    public NodeFilter(String idOrText, PropFilter propFilter) {
        this.idOrText = idOrText;
        this.propFilter = propFilter;
        nodeList = new ArrayList<>();
    }

    @Override
    public boolean filter(AccessibilityEvent accessibilityEvent) {
        AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();         // TODO: recycle
        List<AccessibilityNodeInfo> nodesById = nodeInfo.findAccessibilityNodeInfosByViewId(idOrText);
        List<AccessibilityNodeInfo> nodesByText = nodeInfo.findAccessibilityNodeInfosByText(idOrText);

        nodeList.addAll(nodesById);
        nodeList.addAll(nodesByText);
        if (!nodeList.isEmpty()) {
            return propFilter == null ? true : propFilter.propFilter(nodeList);
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", TAG, nodeList);
    }
}