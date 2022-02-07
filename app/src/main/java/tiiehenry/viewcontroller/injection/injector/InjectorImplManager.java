package tiiehenry.viewcontroller.injection.injector;

import android.content.Context;

import tiiehenry.viewcontroller.util.XposedEnvironment;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

//            Run in God's management process
public class InjectorImplManager extends InjectorImpl {
    public InjectorImplManager(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super(loadPackageParam);
    }

    @Override
    public void handleLoadPackage() {
    }
}
