package tiiehenry.viewcontroller;

import tiiehenry.viewcontroller.IObserver;
import tiiehenry.viewcontroller.rule.AppRules;
import tiiehenry.viewcontroller.rule.ActRules;
import tiiehenry.viewcontroller.rule.ViewRule;
import android.graphics.Bitmap;
import android.os.ParcelFileDescriptor;

interface IGodModeManager {

    void setEditMode(boolean enable);

    boolean isInEditMode();

    void addObserver(String packageName, in IObserver observer);

    void removeObserver(String packageName, in IObserver observer);

    AppRules getAllRules();

    ActRules getRules(String packageName);

    boolean writeBitmap(String packageName,String fileName,in Bitmap snapshot);

    boolean writeAllRule(String packageName,in ActRules actRules);

    boolean writeRule(String packageName, in ViewRule viewRule, in Bitmap bitmap);

    boolean updateRule(String packageName, in ViewRule viewRule);

    boolean deleteRule(String packageName, in ViewRule viewRule);

    boolean deleteRules(String packageName);

    void setAppEnable(String packageName,boolean enable);

    String[] getDisabledApps();

   boolean isAppDisabled(String packageName);

    ParcelFileDescriptor openImageFileDescriptor(String filePath);

}
