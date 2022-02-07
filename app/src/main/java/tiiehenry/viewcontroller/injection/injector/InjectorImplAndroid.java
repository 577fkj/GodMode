package tiiehenry.viewcontroller.injection.injector;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;

import android.content.Context;
import android.os.Binder;

import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.service.GodModeManagerService;
import com.kaisar.xservicemanager.XServiceManager;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

//Run in system process
public class InjectorImplAndroid extends InjectorImpl {
    public InjectorImplAndroid(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super(loadPackageParam);
    }

    @Override
    public void handleLoadPackage() {
        Logger.d(TAG, "inject GodModeManagerService as system service.");
        XServiceManager.initForSystemServer();
        XServiceManager.registerService("godmode", new XServiceManager.ServiceFetcher<Binder>() {
            @Override
            public Binder createService(Context ctx) {
                return new GodModeManagerService(ctx);
            }
        });
    }
}
