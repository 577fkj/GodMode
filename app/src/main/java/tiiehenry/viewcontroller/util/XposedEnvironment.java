package tiiehenry.viewcontroller.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Keep;

/**
 * Created by jrsen on 17-11-22.
 */
@Keep
public final class XposedEnvironment {

    public static final String PREF_DISABLED_PACKAGE_LIST = "disabled_packages_list";

    public enum XposedType {
        XPOSED("de.robv.android.xposed.installer"),
        EDXPOSED("org.meowcat.edxposed.manager"),
        TAICHI("me.weishu.exp"),
        LSPOSED("org.lsposed.manager"),
        UNKNOWN("unknown");

        public final String PACKAGE_NAME;

        XposedType(String packageName) {
            PACKAGE_NAME = packageName;
        }

        public boolean isModuleActive(Context context) {
            try {
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = Uri.parse("content://me.weishu.exposed.CP/");
                Bundle result = null;
                try {
                    result = contentResolver.call(uri, "active", null, null);
                } catch (RuntimeException e) {
                    // TaiChi is killed, try invoke
                    try {
                        Intent intent = new Intent("me.weishu.exp.ACTION_ACTIVE");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Throwable e1) {
                        return false;
                    }
                }
                if (result == null) {
                    result = contentResolver.call(uri, "active", null, null);
                }

                if (result == null) {
                    return false;
                }
                return result.getBoolean("active", false);
            } catch (Throwable ignored) {
            }
            return false;
        }
    }

    /*
    public static void launchXposedManager(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.fuelgauge.PowerUsageSummary");
        intent.setComponent(cn);
        try
        {
            startActivity(intent)
        }catch(ActivityNotFoundException e){
            Toast.makeText(context,"Activity Not Found",Toast.LENGTH_SHORT).show()
        }
        Log.e("XposedEnvironment", "checkXposedType: " + pm.getLaunchIntentForPackage(XposedType.LSPOSED.PACKAGE_NAME) );
        if (pm.getLaunchIntentForPackage(XposedType.LSPOSED.PACKAGE_NAME) != null) {
            return XposedType.LSPOSED;
        } else if(pm.getLaunchIntentForPackage(XposedType.XPOSED.PACKAGE_NAME) != null) {
            return XposedType.XPOSED;
        } else if (pm.getLaunchIntentForPackage(XposedType.EDXPOSED.PACKAGE_NAME) != null) {
            return XposedType.EDXPOSED;
        } else if (pm.getLaunchIntentForPackage(XposedType.TAICHI.PACKAGE_NAME) != null) {
            return XposedType.TAICHI;
        } else {
            return XposedType.UNKNOWN;
        }
    }*/

    public static XposedType checkXposedType(Context context) {
        PackageManager pm = context.getPackageManager();
        if (pm.getLaunchIntentForPackage(XposedType.LSPOSED.PACKAGE_NAME) != null) {
            return XposedType.LSPOSED;
        } else if (pm.getLaunchIntentForPackage(XposedType.XPOSED.PACKAGE_NAME) != null) {
            return XposedType.XPOSED;
        } else if (pm.getLaunchIntentForPackage(XposedType.EDXPOSED.PACKAGE_NAME) != null) {
            return XposedType.EDXPOSED;
        } else if (pm.getLaunchIntentForPackage(XposedType.TAICHI.PACKAGE_NAME) != null) {
            return XposedType.TAICHI;
        } else {
            return XposedType.UNKNOWN;
        }
    }


}
