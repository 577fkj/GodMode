package tiiehenry.viewcontroller.injection.hook;


import android.annotation.SuppressLint;

import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.injection.util.Property;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;

public final class SystemPropertiesHook extends XC_MethodHook implements Property.OnPropertyChangeListener<Boolean> {

    private boolean mDebugLayout;

    @Override
    protected void beforeHookedMethod(MethodHookParam param) {
        if (mDebugLayout && "debug.layout".equals(param.args[0])) {
            param.setResult(true);
        }
    }

    @Override
    public void onPropertyChange(Boolean debugLayout) {
        mDebugLayout = debugLayout;
        try {
            @SuppressLint("PrivateApi") Class<?> SystemPropertiesClass = Class.forName("android.os.SystemProperties");
            XposedHelpers.callStaticMethod(SystemPropertiesClass, "callChangeCallbacks");
        } catch (ClassNotFoundException e) {
            Logger.e(TAG, "invoke callChangeCallbacks fail", e);
        }
    }
}