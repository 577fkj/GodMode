package tiiehenry.viewcontroller.injection;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import tiiehenry.viewcontroller.BuildConfig;
import tiiehenry.viewcontroller.injection.injector.InjectorImpl;
import tiiehenry.viewcontroller.injection.injector.InjectorImplAndroid;
import tiiehenry.viewcontroller.injection.injector.InjectorImplApps;
import tiiehenry.viewcontroller.injection.injector.InjectorImplManager;

public final class GodModeInjector implements IXposedHookLoadPackage {

    public static InjectorImpl injectorImpl;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (!loadPackageParam.isFirstApplication) {
            return;
        }

        switch (loadPackageParam.packageName) {
            case "android"://Run in system process
                GodModeInjector.injectorImpl = new InjectorImplAndroid(loadPackageParam);
                break;
            case BuildConfig.APPLICATION_ID://Run in God's management process
                GodModeInjector.injectorImpl = new InjectorImplManager(loadPackageParam);
                break;
            default://Run in other application processes
                GodModeInjector.injectorImpl = new InjectorImplApps(loadPackageParam);
        }
        GodModeInjector.injectorImpl.handleLoadPackage();
    }

}
