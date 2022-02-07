package tiiehenry.viewcontroller.injection.bridge;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import tiiehenry.viewcontroller.IObserver;
import tiiehenry.viewcontroller.injection.injector.InjectorImplApps;
import tiiehenry.viewcontroller.rule.ActRules;


/**
 * Created by jrsen on 17-10-18.
 */

public final class ManagerObserver extends IObserver.Stub implements Handler.Callback {

    private final Handler mHandler = new Handler(Looper.getMainLooper(), this);
    private static final int ACTION_EDIT_MODE_CHANGED = 0;
    private static final int ACTION_VIEW_RULES_CHANGED = 1;
    private static final int ACTION_APP_STATUS_CHANGED = 2;
    private final InjectorImplApps injectorImplApps;

    public ManagerObserver(InjectorImplApps injectorImplApps) {
        this.injectorImplApps = injectorImplApps;
    }

    @Override
    public void onEditModeChanged(boolean enable) {
        mHandler.obtainMessage(ACTION_EDIT_MODE_CHANGED, enable).sendToTarget();
    }

    @Override
    public void onViewRuleChanged(String packageName, ActRules actRules) {
        mHandler.obtainMessage(ACTION_VIEW_RULES_CHANGED,actRules).sendToTarget();
    }

    @Override
    public void onAppStatusChanged(boolean enable) {
        mHandler.obtainMessage(ACTION_APP_STATUS_CHANGED, enable).sendToTarget();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg.what == ACTION_EDIT_MODE_CHANGED) {
            injectorImplApps.notifyEditModeChanged((Boolean) msg.obj);
        } else if (msg.what == ACTION_VIEW_RULES_CHANGED) {
            injectorImplApps.notifyViewRulesChanged((ActRules) msg.obj);
        } else if (msg.what == ACTION_APP_STATUS_CHANGED) {
            injectorImplApps.notifyAppStatusChanged((Boolean) msg.obj);
        }
        return true;
    }

}

