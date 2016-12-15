package com.walfud.sir;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.walfud.sir.engine.Action;
import com.walfud.sir.engine.Engine;
import com.walfud.sir.engine.filter.ClassFilter;
import com.walfud.sir.engine.filter.NodeFilter;
import com.walfud.sir.engine.filter.NodeTextFilter;
import com.walfud.sir.engine.filter.NotNullFilter;
import com.walfud.sir.engine.filter.PackageFilter;

import java.util.List;

/**
 * Created by walfud on 2016/12/3.
 */

public class MyAccessibilityService extends AccessibilityService {

    public static final String TAG = "MyAccessibilityService";

    private Engine mEngine;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.v(TAG, String.format("recv: %s", accessibilityEvent.toString()));

        if (mEngine == null) {
            mEngine = new Engine(new Action("Start Dev Setting Activity") {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
            }, new Action("Open Developer Option", /*1000, */new NotNullFilter(), new PackageFilter("com.android.settings"), new ClassFilter("com.android.settings.Settings$DevelopmentSettingsActivity"), new NodeFilter("com.android.settings:id/switch_bar"), new NodeFilter("com.android.settings:id/switch_widget")) {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, final AccessibilityNodeInfo lastNode0) {
                    if (lastNode0.isChecked()) {
                        return true;
                    }

                    compose(new Action("Agree", /*1000, */new NotNullFilter(), new PackageFilter("com.android.settings"), new ClassFilter("android.app.AlertDialog"), new NodeTextFilter("android:id/alertTitle", "Allow development settings?"), new NodeFilter("android:id/button1")) {
                        @Override
                        public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
                            return lastNode0.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    });
                    return lastNode0.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }, new Action("Scroll down", /*1000, */new NotNullFilter(), new PackageFilter("com.android.settings")) {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
                    AccessibilityNodeInfo rootNode = getRootInActiveWindow();
                    if (rootNode == null) {
                        return false;
                    }

                    List<AccessibilityNodeInfo> nodeInfoList = rootNode.findAccessibilityNodeInfosByViewId("android:id/list");
                    if (!nodeInfoList.isEmpty()) {
                        AccessibilityNodeInfo nodeInfo = nodeInfoList.get(0);
                        nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    }
                    return true;
                }
            }, new Action("Open USB Debugging", /*1000, */new NotNullFilter(), new PackageFilter("com.android.settings"), new NodeFilter("USB debugging")) {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, final AccessibilityNodeInfo lastNode0) {
                    if (lastNode0.isChecked()) {
                        return true;
                    }

                    compose(new Action("Agree", /*1000, */new NotNullFilter(), new PackageFilter("com.android.settings"), new ClassFilter("android.app.AlertDialog"), new NodeTextFilter("android:id/alertTitle", "Allow USB debugging?"), new NodeFilter("android:id/button1")) {
                        @Override
                        public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
                            return lastNode0.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    });
                    return AccessibilityUtils.getClickableParent(lastNode0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }, new Action("Tip") {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
                    Toast.makeText(getApplicationContext(), "Open 'Usb Debug' success", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        mEngine.dispatchEvent(accessibilityEvent);
    }

    @Override
    public void onInterrupt() {

    }
}