package com.lxj.androidktx;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;

public class AndroidConfig {
    public static Context context;
    public static boolean isDebug = true;
    public static String defaultLogTag = "android";
    public static String sharedPrefName = "android";

    private void init(Context context,
                      Boolean isDebug,
                      String defaultLogTag,
                      String sharedPrefName
    ) {
        AndroidConfig.context = context;
        AndroidConfig.isDebug = isDebug;
        AndroidConfig.defaultLogTag = defaultLogTag;
        AndroidConfig.sharedPrefName = sharedPrefName;
    }
}
