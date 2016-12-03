package com.walfud.sir;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by walfud on 2016/12/3.
 */

public class MyAccessibilityService extends AccessibilityService {

    public static final String TAG = "MyAccessibilityService";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.e(TAG, accessibilityEvent.toString());
    }

    @Override
    public void onInterrupt() {

    }
}
