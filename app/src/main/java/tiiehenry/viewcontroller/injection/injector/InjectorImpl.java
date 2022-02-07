package tiiehenry.viewcontroller.injection.injector;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public abstract class InjectorImpl {
    public final XC_LoadPackage.LoadPackageParam loadPackageParam;

    public InjectorImpl(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        this.loadPackageParam = loadPackageParam;
    }

    public abstract void handleLoadPackage() throws Throwable;
}
