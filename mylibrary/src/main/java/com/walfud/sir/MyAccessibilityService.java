package com.walfud.sir;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityRecord;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by walfud on 2016/12/3.
 */

public class MyAccessibilityService extends AccessibilityService {

    public static final String TAG = "MyAccessibilityService";

    private Op mOp;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        Log.v(TAG, String.format("recv: %s", accessibilityEvent.toString()));

        if (mOp == null) {
            mOp = new Op(new Action() {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    return true;
                }
            }, new EasyAction() {
                @Override
                public boolean handle(int eventType, long eventTime, int contentChangeTypes, CharSequence packageName, CharSequence className, int windowId, CharSequence contentDescription, int scrollX, int scrollY, AccessibilityNodeInfo source, int recordCount, AccessibilityRecord[] records) {
                    if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && TextUtils.equals(packageName, "com.android.settings") && TextUtils.equals(className, "com.android.settings.Settings$DevelopmentSettingsActivity")) {
                        List<AccessibilityNodeInfo> switchBarNodes = source.findAccessibilityNodeInfosByViewId("com.android.settings:id/switch_bar");
                        if (!switchBarNodes.isEmpty()) {
                            List<AccessibilityNodeInfo> switchBoxNodes = switchBarNodes.get(0).findAccessibilityNodeInfosByViewId("com.android.settings:id/switch_widget");
                            if (!switchBoxNodes.isEmpty()) {
                                AccessibilityNodeInfo switchBoxNode = switchBoxNodes.get(0);
                                if (switchBoxNode.isChecked()) {
                                    // Open -> Closed
                                } else {
                                    // Closed -> Open
                                }
                                return switchBoxNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            }
                        }
                    }
                    return false;
                }
            }, new Action() {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent) {
                    AccessibilityNodeInfo source = accessibilityEvent.getSource();
                    if (source != null) {
                        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && TextUtils.equals(accessibilityEvent.getPackageName(), "com.android.settings") && TextUtils.equals(accessibilityEvent.getClassName(), "android.app.AlertDialog")) {
                            List<AccessibilityNodeInfo> titleNodes = source.findAccessibilityNodeInfosByViewId("android:id/alertTitle");
                            if (!titleNodes.isEmpty() && TextUtils.equals(titleNodes.get(0).getText(), "Allow development settings?")) {
                                List<AccessibilityNodeInfo> okNodes = source.findAccessibilityNodeInfosByViewId("android:id/button1");
                                if (!okNodes.isEmpty()) {
                                    AccessibilityNodeInfo okNode = okNodes.get(0);
                                    return okNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                }
                            }
                        }
                        source.recycle();
                    }
                    return false;
                }
            }, new Action() {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent) {
                    Toast.makeText(getApplicationContext(), "Open 'Usb Debug' success", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        mOp.dispatchEvent(accessibilityEvent);
    }

    @Override
    public void onInterrupt() {

    }

    // internal
    public static class Action {
        public static final String TAG = "Action";

        public String actionName;
        public Action() {
            this("");
        }

        public Action(String actionName) {
            this.actionName = actionName;
        }

        public boolean handle(AccessibilityEvent accessibilityEvent) {
            Log.v(TAG, String.format("accept: %s", accessibilityEvent.toString()));
            return true;
        }
    }

    public static class EasyAction extends Action {
        public static final String TAG = "EasyAction";

        @Override
        public final boolean handle(AccessibilityEvent accessibilityEvent) {
            int recordCount = accessibilityEvent.getRecordCount();
            AccessibilityRecord[] records = new AccessibilityRecord[recordCount];
            for (int i = 0; i < recordCount; i++) {
                records[i] = accessibilityEvent.getRecord(i);
            }
            int eventType = accessibilityEvent.getEventType();
            int contentChangeTypes = accessibilityEvent.getContentChangeTypes();
            long eventTime = accessibilityEvent.getEventTime();
            CharSequence packageName = accessibilityEvent.getPackageName();
            int movementGranularity = accessibilityEvent.getMovementGranularity();
            int action = accessibilityEvent.getAction();
            AccessibilityNodeInfo source = accessibilityEvent.getSource();
            int windowId = accessibilityEvent.getWindowId();
            int itemCount = accessibilityEvent.getItemCount();
            int currentItemIndex = accessibilityEvent.getCurrentItemIndex();
            int fromIndex = accessibilityEvent.getFromIndex();
            int toIndex = accessibilityEvent.getToIndex();
            int scrollX = accessibilityEvent.getScrollX();
            int scrollY = accessibilityEvent.getScrollY();
            int maxScrollX = accessibilityEvent.getMaxScrollX();
            int maxScrollY = accessibilityEvent.getMaxScrollY();
            int addedCount = accessibilityEvent.getAddedCount();
            int removedCount = accessibilityEvent.getRemovedCount();
            CharSequence className = accessibilityEvent.getClassName();
            CharSequence beforeText = accessibilityEvent.getBeforeText();
            CharSequence contentDescription = accessibilityEvent.getContentDescription();
            Parcelable parcelableData = accessibilityEvent.getParcelableData();
//            long sourceNodeId = accessibilityEvent.getSourceNodeId();

            boolean ret = false;
            if (!TextUtils.isEmpty(packageName) && source != null) {
                Log.v(TAG, String.format("accept: %s", accessibilityEvent.toString()));
                ret = handle(eventType, eventTime, contentChangeTypes, packageName, className, windowId, contentDescription, scrollX, scrollY, source, recordCount, records);
            } else {
                Log.v(TAG, String.format("drop: %s", accessibilityEvent.toString()));
            }

            if (source != null) {
                source.recycle();
            }
            return ret;
        }

        public boolean handle(int eventType, long eventTime, int contentChangeTypes, CharSequence packageName, CharSequence className, int windowId, CharSequence contentDescription, int scrollX, int scrollY, AccessibilityNodeInfo source, int recordCount, AccessibilityRecord[] records) {
            return true;
        }
    }

    public static class Record {
        public String actionName;
        public long timestamp;

        public Record(String actionName, long timestamp) {
            this.actionName = actionName;
            this.timestamp = timestamp;
        }
    }

    public static class Op {
        public static final String TAG = "Op";

        protected Queue<Action> mHandlerQueue;
        protected List<Record> mRecordList;

        public Op(Action... actions) {
            mHandlerQueue = new LinkedList<>();
            for (Action action : actions) {
                mHandlerQueue.add(action);
            }
            mRecordList = new ArrayList<>();
        }

        public void dispatchEvent(AccessibilityEvent accessibilityEvent) {
            Action action = mHandlerQueue.peek();
            if (action == null) {
                return;
            }

            if (action.handle(accessibilityEvent)) {
                Log.d(TAG, String.format("emit: %s", accessibilityEvent.toString()));

                mHandlerQueue.remove();
                mRecordList.add(new Record(action.actionName, System.currentTimeMillis()));
            }
        }
    }
}