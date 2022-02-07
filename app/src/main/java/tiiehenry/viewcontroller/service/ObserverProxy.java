package tiiehenry.viewcontroller.service;

import android.os.IBinder;
import android.os.RemoteException;

import tiiehenry.viewcontroller.IObserver;
import tiiehenry.viewcontroller.rule.ActRules;

public class ObserverProxy implements IObserver {

    private final String packageName;
    private final IObserver observer;

    public ObserverProxy(String packageName, IObserver observer) {
        this.packageName = packageName;
        this.observer = observer;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    public void onEditModeChanged(boolean enable) throws RemoteException {
        observer.onEditModeChanged(enable);
    }

    @Override
    public void onAppStatusChanged(boolean enable) throws RemoteException {
        observer.onAppStatusChanged(enable);
    }

    @Override
    public void onViewRuleChanged(String packageName, ActRules actRules) throws RemoteException {
        observer.onViewRuleChanged(packageName, actRules);
    }

    @Override
    public IBinder asBinder() {
        return observer.asBinder();
    }
}
