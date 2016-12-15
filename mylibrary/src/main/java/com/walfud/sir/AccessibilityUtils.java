package com.walfud.sir;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by walfud on 2016/12/15.
 */

public class AccessibilityUtils {

    public static AccessibilityNodeInfo getClickableParent(AccessibilityNodeInfo accessibilityNodeInfo) {
        while (accessibilityNodeInfo != null && !accessibilityNodeInfo.isClickable()) {
            accessibilityNodeInfo = accessibilityNodeInfo.getParent();
        }

        return accessibilityNodeInfo;
    }
}
