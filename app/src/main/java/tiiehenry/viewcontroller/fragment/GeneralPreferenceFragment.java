package tiiehenry.viewcontroller.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreferenceCompat;

import tiiehenry.viewcontroller.BuildConfig;
import tiiehenry.viewcontroller.CrashHandler;
import tiiehenry.viewcontroller.GodModeApplication;
import tiiehenry.viewcontroller.R;
import tiiehenry.viewcontroller.SettingsActivity;
import tiiehenry.viewcontroller.backup.RuleExporter;
import tiiehenry.viewcontroller.fragment.viewrules.ViewRuleListFragment;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.model.SharedViewModel;
import tiiehenry.viewcontroller.rule.ActRules;
import tiiehenry.viewcontroller.rule.AppRules;
import tiiehenry.viewcontroller.util.Clipboard;
import tiiehenry.viewcontroller.util.DonateHelper;
import tiiehenry.viewcontroller.util.PermissionHelper;
import tiiehenry.viewcontroller.util.Preconditions;
import tiiehenry.viewcontroller.util.ShareUtil;
import tiiehenry.viewcontroller.util.XposedEnvironment;
import tiiehenry.viewcontroller.widget.Snackbar;

import java.io.File;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by jrsen on 17-10-29.
 */

public final class GeneralPreferenceFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String SETTING_PREFS = "settings";
    private static final String KEY_VERSION_CODE = "version_code";

    //    private ProgressPreference mProgressPreference;
    private SwitchPreferenceCompat mEditorSwitchPreference;
    private Preference mBackupPreference;
    private Preference mRestorePreference;
    private Preference mJoinGroupPreference;
    private Preference mDonatePreference;

    private ActivityResultLauncher<String> mFileLauncher;
    private SharedViewModel mSharedViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        PreferenceManager.getDefaultSharedPreferences(requireContext()).registerOnSharedPreferenceChangeListener(this);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mFileLauncher = requireActivity().registerForActivityResult(new ActivityResultContracts.GetContent(), this::onActivityResult);
        mSharedViewModel.mAppRules.observe(this, this::onAppRuleChange);
        if (!checkCrash()) {
            mSharedViewModel.loadAppRules();
        }
    }

    private boolean checkCrash() {
        String crashInfo = CrashHandler.getLastCrashInfo(GodModeApplication.getApplication());
        if (crashInfo != null) {
            SpannableString text = new SpannableString(getString(R.string.crash_tip));
            SpannableString st = new SpannableString(crashInfo);
            st.setSpan(new RelativeSizeSpan(0.7f), 0, st.length(), 0);
            CharSequence message = TextUtils.concat(text, st);
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.hey_guy)
                    .setMessage(message)
                    .setPositiveButton(R.string.dialog_btn_copy, (dialog, which) -> Clipboard.putContent(requireContext(), crashInfo))
                    .show();
            return true;
        }
        return false;
    }

    private void onActivityResult(Uri uri) {
        if (uri == null) return;
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getText(R.string.dialog_message_importing));
        progressDialog.show();
//        mProgressPreference.setVisible(true);
        mSharedViewModel.importExternalRules(requireContext(), uri, new SharedViewModel.ImportCallback() {
            private void dismiss() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onSuccess() {
                dismiss();
                Snackbar.make(requireActivity(), R.string.import_success, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable t) {
                dismiss();
                Snackbar.make(requireActivity(), R.string.import_failed, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void onAppRuleChange(AppRules appRules) {
//        mProgressPreference.setVisible(false);
        appRules = appRules != null ? appRules : new AppRules();
        Set<Map.Entry<String, ActRules>> entries = appRules.entrySet();
        PreferenceCategory category = (PreferenceCategory) findPreference(getString(R.string.pref_key_app_rules));
        category.removeAll();
        PackageManager pm = requireContext().getPackageManager();
        for (Map.Entry<String, ActRules> entry : entries) {
            String packageName = entry.getKey();
            Drawable icon;
            CharSequence label;
            try {
                ApplicationInfo aInfo = pm.getApplicationInfo(packageName, 0);
                icon = aInfo.loadIcon(pm);
                label = aInfo.loadLabel(pm);
            } catch (PackageManager.NameNotFoundException ignore) {
                icon = ResourcesCompat.getDrawable(getResources(), R.mipmap.ic_god, requireContext().getTheme());
                label = packageName;
            }
            Preference preference = new Preference(category.getContext());
            preference.setIcon(icon);
            preference.setTitle(label);
            preference.setSummary(packageName);
            preference.setKey(packageName);
            preference.setOnPreferenceClickListener(this);
            category.addPreference(preference);
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.pref_general, rootKey);
//        mProgressPreference = (ProgressPreference) findPreference(getString(R.string.pref_key_progress_indicator));
        mEditorSwitchPreference = (SwitchPreferenceCompat) findPreference(getString(R.string.pref_key_editor));
        mEditorSwitchPreference.setChecked(GodModeManager.getDefault().isInEditMode());
        mEditorSwitchPreference.setOnPreferenceClickListener(this);
        mEditorSwitchPreference.setOnPreferenceChangeListener(this);
        mBackupPreference = findPreference(getString(R.string.pref_key_backup));
        mBackupPreference.setOnPreferenceClickListener(this);
        mRestorePreference = findPreference(getString(R.string.pref_key_restore));
        mRestorePreference.setOnPreferenceClickListener(this);
        mJoinGroupPreference = findPreference(getString(R.string.pref_key_join_group));
        mJoinGroupPreference.setOnPreferenceClickListener(this);
        mDonatePreference = findPreference(getString(R.string.pref_key_donate));
        mDonatePreference.setOnPreferenceClickListener(this);

        SharedPreferences sp = requireContext().getSharedPreferences(SETTING_PREFS, Context.MODE_PRIVATE);
        int previousVersionCode = sp.getInt(KEY_VERSION_CODE, 0);
        if (previousVersionCode != BuildConfig.VERSION_CODE) {
            sp.edit().putInt(KEY_VERSION_CODE, BuildConfig.VERSION_CODE).apply();
            showUpdatePolicyDialog();

        } else if (!XposedEnvironment.XposedType.UNKNOWN.isModuleActive(getContext())) {
            showEnableModuleDialog();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedViewModel.updateTitle(R.string.app_name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(
                mEditorSwitchPreference.getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return XposedEnvironment.XposedType.UNKNOWN.isModuleActive(requireContext());
    }

    private boolean checkPermission() {
        PermissionHelper permissionHelper = new PermissionHelper(requireActivity());

        if (!permissionHelper.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            permissionHelper.applyPermissions(Manifest.permission.READ_EXTERNAL_STORAGE);
            return false;
        }

        if (!permissionHelper.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            permissionHelper.applyPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (mEditorSwitchPreference == preference) {
            if (!XposedEnvironment.XposedType.UNKNOWN.isModuleActive(requireContext())) {
                Toast.makeText(requireContext(), R.string.not_active_module, Toast.LENGTH_SHORT).show();
                return true;
            }
            GodModeManager.getDefault().setEditMode(mEditorSwitchPreference.isChecked());
        } else if (mBackupPreference == preference) {
            if (!checkPermission()) {
                return true;
            }
            startBackup();
        } else if (mRestorePreference == preference) {
            if (!checkPermission()) {
                return true;
            }
            mFileLauncher.launch("application/zip");
        } else if (mJoinGroupPreference == preference) {
            showGroupInfoDialog();
        } else if (mDonatePreference == preference) {
            DonateHelper.showDonateDialog(requireContext());
        } else {
            String packageName = preference.getSummary().toString();
            mSharedViewModel.updateSelectedPackage(packageName);
            ViewRuleListFragment fragment = new ViewRuleListFragment();
            fragment.setIcon(preference.getIcon());
            fragment.setLabel(preference.getTitle());
            fragment.setPackageName(preference.getSummary());
            SettingsActivity activity = (SettingsActivity) requireActivity();
            activity.startPreferenceFragment(fragment);
        }
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
        if (TextUtils.equals(key, getString(R.string.pref_key_editor))) {
            mEditorSwitchPreference.setChecked(sp.getBoolean(key, false));
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_general, menu);
        MenuItem item = menu.findItem(R.id.menu_icon_switch);
        boolean hidden = mSharedViewModel.isIconHidden(requireContext());
        item.setTitle(!hidden ? R.string.menu_icon_switch_hide : R.string.menu_icon_switch_show);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_import_rules) {
            PermissionHelper permissionHelper = new PermissionHelper(requireActivity());
            if (!permissionHelper.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionHelper.applyPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return true;
            }
            mFileLauncher.launch("application/zip");
        } else if (item.getItemId() == R.id.menu_icon_switch) {
            boolean hidden = mSharedViewModel.isIconHidden(requireContext());
            mSharedViewModel.setIconHidden(requireContext(), hidden = !hidden);
            item.setTitle(hidden ? R.string.menu_icon_switch_show : R.string.menu_icon_switch_hide);
        }
        return true;
    }

    private void showEnableModuleDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.hey_guy)
                .setMessage(R.string.not_active_module)
                .setPositiveButton(R.string.active, (dialog, which) -> {
                    XposedEnvironment.XposedType xposedType = XposedEnvironment.checkXposedType(requireContext());
                    if (xposedType != XposedEnvironment.XposedType.UNKNOWN) {
                        Intent launchIntent = requireContext().getPackageManager().getLaunchIntentForPackage(xposedType.PACKAGE_NAME);
                        startActivity(launchIntent);
                    } else {
                        Snackbar.make(requireActivity(), R.string.not_found_xp_installer, Snackbar.LENGTH_LONG).show();
                    }
                })
                .show();
    }

    private void showUpdatePolicyDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.welcome_title)
                .setMessage(R.string.update_tips)
                .setPositiveButton(R.string.dialog_btn_alipay, (dialog1, which) -> DonateHelper.startAliPayDonate(requireContext()))
                .setNegativeButton(R.string.dialog_btn_wxpay, (dialog12, which) -> DonateHelper.startWxPayDonate(requireContext()))
                .show();
    }

    private void showGroupInfoDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getText(R.string.dialog_message_query_community));
        progressDialog.show();
        mSharedViewModel.getGroupInfo(new Callback<Map<String, String>[]>() {
            private void dismiss() {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onResponse(@NonNull Call<Map<String, String>[]> call, @NonNull Response<Map<String, String>[]> response) {
                dismiss();
                try {
                    if (!response.isSuccessful()) throw new Exception("not successful");
                    Map<String, String>[] body = Preconditions.checkNotNull(response.body());
                    final int N = body.length;
                    String[] names = new String[N];
                    String[] links = new String[N];
                    for (int i = 0; i < N; i++) {
                        Map<String, String> map = body[i];
                        names[i] = map.get("group_name");
                        links[i] = map.get("group_link");
                    }
                    new AlertDialog.Builder(requireContext())
                            .setItems(names, (dialog, which) -> {
                                try {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(links[which]));
                                    startActivity(intent);
                                } catch (Exception ignore) {
                                }
                            }).show();
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "获取群组信息失败:" + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Map<String, String>[]> call, @NonNull Throwable t) {
                dismiss();
                Toast.makeText(requireContext(), "获取群组信息失败" + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void startBackup() {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getText(R.string.pref_description_backup));
        progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                try {
                    String filepath = RuleExporter.getExportDir(requireContext()).getPath();
                    File zipFile = RuleExporter.exportAllRules(filepath, mSharedViewModel.mAppRules.getValue());
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(requireActivity(), getString(R.string.export_successful, filepath), Snackbar.LENGTH_LONG).show();
                            ShareUtil.share(requireContext(), zipFile);
                        }
                    });
                } catch (Exception e) {
                    Logger.e(getClass().getSimpleName(), "export single rule fail", e);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Snackbar.make(requireActivity(), R.string.export_failed, Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });


            }
        }.start();
    }

}