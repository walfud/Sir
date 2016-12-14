package com.walfud.sir.engine.filter;

import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

/**
 * Created by walfud on 2016/12/14.
 */

public class NodeTextFilter extends NodeFilter {
    public static final String TAG = "NodeTextFilter";

    public String value;
    public NodeTextFilter(String idOrText, final String value) {
        super(idOrText, new PropFilter() {
            @Override
            public boolean propFilter(List<AccessibilityNodeInfo> nodeList) {
                return TextUtils.equals(nodeList.get(0).getText(), value);
            }
        });

        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", TAG, value);
    }
}
