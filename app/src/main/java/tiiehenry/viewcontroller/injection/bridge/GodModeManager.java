package tiiehenry.viewcontroller.injection.bridge;

import android.graphics.Bitmap;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;

import tiiehenry.viewcontroller.IGodModeManager;
import tiiehenry.viewcontroller.IObserver;
import tiiehenry.viewcontroller.rule.ActRules;
import tiiehenry.viewcontroller.rule.AppRules;
import tiiehenry.viewcontroller.rule.ViewRule;
import com.kaisar.xservicemanager.XServiceManager;

public final class GodModeManager {

    private static GodModeManager instance;
    private final IGodModeManager mGMM;

    private GodModeManager(IGodModeManager gmm) {
        this.mGMM = gmm;
    }

    public static GodModeManager getDefault() {
        synchronized (GodModeManager.class) {
            if (instance == null) {
                IBinder service = XServiceManager.getService("godmode");
                if (service != null) {
                    instance = new GodModeManager(IGodModeManager.Stub.asInterface(service));
                } else {
                    instance = new GodModeManager(new IGodModeManager.Default());
                }
            }
            return instance;
        }
    }

    public void setEditMode(boolean enable) {
        try {
            mGMM.setEditMode(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isInEditMode() {
        try {
            return mGMM.isInEditMode();
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addObserver(String packageName, IObserver observer) {
        try {
            mGMM.addObserver(packageName, observer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void removeObserver(String packageName, IObserver observer) {
        try {
            mGMM.removeObserver(packageName, observer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public AppRules getAllRules() {
        try {
            return mGMM.getAllRules();
        } catch (RemoteException e) {
            e.printStackTrace();
            return new AppRules();
        }
    }

    public ActRules getRules(String packageName) {
        try {
            return mGMM.getRules(packageName);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ActRules();
        }
    }

    public boolean  writeAllRule(String packageName, ActRules actRules) {
        try {
            return mGMM.writeAllRule(packageName,actRules);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean writeRule(String packageName, ViewRule viewRule, Bitmap bitmap) {
        try {
            return mGMM.writeRule(packageName, viewRule, bitmap);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateRule(String packageName, ViewRule viewRule) {
        try {
            return mGMM.updateRule(packageName, viewRule);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRule(String packageName, ViewRule viewRule) {
        try {
            return mGMM.deleteRule(packageName, viewRule);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteRules(String packageName) {
        try {
            return mGMM.deleteRules(packageName);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setAppEnable(String packageName, boolean enable) {
        try {
            mGMM.setAppEnable(packageName, enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public String[] getDisabledApps() {
        try {
            return mGMM.getDisabledApps();
        } catch (RemoteException e) {
            e.printStackTrace();
            return new String[]{};
        }
    }

    public boolean isAppDisabled(String packageName) {
        try {
            return mGMM.isAppDisabled(packageName);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ParcelFileDescriptor openImageFileDescriptor(String filePath) {
        try {
            return mGMM.openImageFileDescriptor(filePath);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void writeBitmap(String packageName, String imagePath, Bitmap bitmap) {
        try {
            mGMM.writeBitmap(packageName,imagePath,bitmap);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
