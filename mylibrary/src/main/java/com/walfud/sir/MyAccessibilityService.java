package com.walfud.sir;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
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
            }, new Action() {
                @Override
                public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
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
                public boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0) {
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
    public static abstract class Action {
        public static final String TAG = "Action";

        public String actionName;
        public List<ActionFilter> filterList;
        public AccessibilityNodeInfo lastNode0;     // `nodeInfoList.get(0)` at latest node filter
        public Action() {
            this("");
        }

        public <T extends ActionFilter> Action(String actionName, T... filters) {
            this.actionName = actionName;
            this.filterList = Arrays.<ActionFilter>asList(filters);
        }

        public boolean filter(AccessibilityEvent accessibilityEvent) {
            for (int i = 0; i < filterList.size(); i++) {
                ActionFilter filter = filterList.get(i);
                if (!filter.filter(accessibilityEvent)) {
                    Log.v(TAG, String.format("drop: %s", accessibilityEvent.toString()));
                    return false;
                }

                if (i == filterList.size() - 1 && filter instanceof NodeFilter) {
                    lastNode0 = ((NodeFilter) filter).nodeList.get(0);
                }
            }

            Log.v(TAG, String.format("accept: %s", accessibilityEvent.toString()));
            return true;
        }

        public abstract boolean handle(AccessibilityEvent accessibilityEvent, AccessibilityNodeInfo lastNode0);
    }

    public static abstract class ActionFilter {
        public abstract boolean filter(AccessibilityEvent accessibilityEvent);
    }

    public static class NotNullFilter extends ActionFilter {
        @Override
        public boolean filter(AccessibilityEvent accessibilityEvent) {
            return accessibilityEvent != null
                    && !TextUtils.isEmpty(accessibilityEvent.getPackageName())
                    && !TextUtils.isEmpty(accessibilityEvent.getClassName())
                    && accessibilityEvent.getSource() != null;
        }
    }

    public static class PackageFilter extends ActionFilter {
        private String packageName;

        public PackageFilter(String packageName) {
            this.packageName = packageName;
        }

        @Override
        public boolean filter(AccessibilityEvent accessibilityEvent) {
            return TextUtils.equals(packageName, accessibilityEvent.getPackageName());
        }
    }
    public static class ClassFilter extends ActionFilter {
        private String className;

        public ClassFilter(String className) {
            this.className = className;
        }

        @Override
        public boolean filter(AccessibilityEvent accessibilityEvent) {
            return TextUtils.equals(className, accessibilityEvent.getClassName());
        }
    }

    public static abstract class NodeFilter extends ActionFilter {
        public String[] objs;       // Node id or text
        public List<AccessibilityNodeInfo> nodeList;
        public NodeFilter(String... objs) {
            this.objs = objs;
        }

        @Override
        public boolean filter(AccessibilityEvent accessibilityEvent) {
            for (String obj : objs) {
                List<AccessibilityNodeInfo> nodes = findMethod(accessibilityEvent.getSource(), obj);
                if (!nodes.isEmpty()) {
                    nodeList = nodes;
                    return true;
                }
            }

            nodeList = new ArrayList<>();
            return false;
        }

        public abstract List<AccessibilityNodeInfo> findMethod(AccessibilityNodeInfo nodeInfo, String obj);
    }
    public static class NodeTextFilter extends NodeFilter {
        public NodeTextFilter(String... objs) {
            super(objs);
        }

        @Override
        public List<AccessibilityNodeInfo> findMethod(AccessibilityNodeInfo nodeInfo, String obj) {
            return nodeInfo.findAccessibilityNodeInfosByText(obj);
        }
    }
    public static class NodeIdFilter extends NodeFilter {
        public NodeIdFilter(String... objs) {
            super(objs);
        }

        @Override
        public List<AccessibilityNodeInfo> findMethod(AccessibilityNodeInfo nodeInfo, String obj) {
            return nodeInfo.findAccessibilityNodeInfosByViewId(obj);
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

            if (action.filter(accessibilityEvent)) {
                if (action.handle(accessibilityEvent, action.lastNode0)) {
                    Log.d(TAG, String.format("emit: %s", accessibilityEvent.toString()));

                    mHandlerQueue.remove();
                    mRecordList.add(new Record(action.actionName, System.currentTimeMillis()));
                }
            }
        }
    }
}