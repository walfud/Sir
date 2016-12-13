package com.walfud.sir;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.walfud.sir.engine.Action;
import com.walfud.sir.engine.Engine;
import com.walfud.sir.engine.filter.ClassFilter;
import com.walfud.sir.engine.filter.NodeFilter;
import com.walfud.sir.engine.filter.NodeIdFilter;
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
            mEngine = new Engine(new Action() {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
            }, new Action("Open Dev Setting Activity", new NotNullFilter(), new PackageFilter("com.android.settings"), new ClassFilter("com.android.settings.Settings$DevelopmentSettingsActivity"), new NodeIdFilter("com.android.settings:id/switch_bar"), new NodeIdFilter("com.android.settings:id/switch_widget")) {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
                    return lastNode0.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }, new Action("Open Dev Setting Activity", new NotNullFilter(), new PackageFilter("com.android.settings"), new ClassFilter("android.app.AlertDialog"), new NodeIdFilter("android:id/alertTitle", new NodeFilter.PropFilter() {
                @Override
                public boolean propFilter(List<AccessibilityNodeInfo> nodeList) {
                    return TextUtils.equals(nodeList.get(0).getText(), "Allow development settings?");
                }
            }), new NodeIdFilter("android:id/button1")) {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
                    return lastNode0.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }, new Action() {
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