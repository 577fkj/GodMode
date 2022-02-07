package tiiehenry.viewcontroller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.util.XposedEnvironment;

public final class NotificationService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "GMPNotiService";
    private static final int ID = 10121;

    @Override
    public void onCreate() {
        super.onCreate();
        createControlChannel();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean enable = isEditMode();
        if (intent!=null) {
            if (TextUtils.equals(intent.getAction(), Intent.ACTION_EDIT)) {
                if (!XposedEnvironment.XposedType.UNKNOWN.isModuleActive(this)) {
                    Toast.makeText(this, R.string.not_active_module, Toast.LENGTH_SHORT).show();
                    return super.onStartCommand(intent, flags, startId);
                }
                enable = !enable;
                setEditModeEnable(enable);
            }
        }
        postNotification(enable);
        return super.onStartCommand(intent, flags, startId);
    }

    private void createControlChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(TAG, "Control panel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Let there be light");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    private void postNotification(boolean editMode) {
        if (editMode) {
            startForeground(ID, buildNotification(true));
        } else {
            stopForeground(false);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(ID, buildNotification(false));
        }
    }

    private Notification buildNotification(boolean editMode) {
        Intent managerIntent = new Intent(this, SettingsActivity.class);

        int flag = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flag = flag | PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent managerPendingIntent = PendingIntent.getActivity(this, 0, managerIntent, flag);

        Intent intent = new Intent(this, NotificationService.class);
        intent.setAction(Intent.ACTION_EDIT);

        int flags = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags = flags | PendingIntent.FLAG_MUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, flags);

        return new NotificationCompat.Builder(this, TAG)
                .setSmallIcon(R.drawable.ic_angel_small)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_angel_normal))
                .setContentTitle(getText(R.string.app_name))
                .setContentText(editMode ? getString(R.string.enter_edit) : getString(R.string.exit_edit))
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_menu_manage, getString(R.string.manage), managerPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setOngoing(editMode)
                .build();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setEditModeEnable(boolean enable) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        sp.edit().putBoolean("editor_switch", enable).apply();
        GodModeManager.getDefault().setEditMode(enable);
    }

    public boolean isEditMode() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        return sp.getBoolean("editor_switch", false);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (TextUtils.equals(key, getString(R.string.pref_key_editor))) {
            postNotification(sharedPreferences.getBoolean(key, false));
        }
    }
}
