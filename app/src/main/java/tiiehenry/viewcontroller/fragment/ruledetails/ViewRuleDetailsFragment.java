package tiiehenry.viewcontroller.fragment.ruledetails;

import static tiiehenry.viewcontroller.GodModeApplication.TAG;
import static tiiehenry.viewcontroller.injection.util.ViewBitmapUtils.recycleNullableBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.preference.DropDownPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import tiiehenry.viewcontroller.R;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.model.SharedViewModel;
import tiiehenry.viewcontroller.widget.preference.ImageViewPreference;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.Preconditions;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by jrsen on 17-10-29.
 */

public final class ViewRuleDetailsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener, LoaderManager.LoaderCallbacks<Bitmap> {

    private Drawable mIcon;
    private CharSequence mLabel;
    private CharSequence mPackageName;
    private ViewRule mViewRule;
    private Bitmap mRuleImageBitmap;

    private SharedViewModel mSharedViewModel;
    private EditTextPreference mAliasPreference;
    private EditTextPreference mAlphaPreference;
    private DropDownPreference mVisiblePreference;
    private DropDownPreference mTargetTypePreference;
    private EditTextPreference mTargetWPreference;
    private EditTextPreference mTargetHPreference;
    private SwitchPreference mSwitchPreference;
    private SwitchPreference mClickPreference;
    private boolean deleted = false;

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public void setLabel(CharSequence label) {
        mLabel = label;
    }

    public void setPackageName(CharSequence packageName) {
        mPackageName = packageName;
    }

    public void setViewRule(ViewRule viewRule) {
        mViewRule = viewRule;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_rule_details);

        Preference preference = findPreference(getString(R.string.pref_key_detail_rule_created_time));
        preference.setTitle(R.string.rule_details_field_create_time);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        preference.setSummary(dateFormat.format(new Date(mViewRule.timestamp)));

        preference = findPreference(getString(R.string.pref_key_detail_rule_match_version));
        preference.setTitle(R.string.rule_details_field_generate_version);
        preference.setSummary(String.format(Locale.getDefault(), "%s %s", mLabel, mViewRule.matchVersionName));

        preference = findPreference(getString(R.string.pref_key_detail_rule_applied_activity));
        preference.setTitle(R.string.rule_details_field_activity);
        preference.setSummary(Preconditions.optionDefault(mViewRule.activityClass, "None"));

        mSwitchPreference = (SwitchPreference) findPreference(getString(R.string.pref_key_detail_rule_enable));
        mSwitchPreference.setChecked(mViewRule.enable);
        mSwitchPreference.setOnPreferenceChangeListener(this);
        mSwitchPreference.setOnPreferenceClickListener(preference1 -> {
            return false;
        });
        mSwitchPreference.setIcon(mIcon);
        mSwitchPreference.setTitle(mLabel);
        mSwitchPreference.setSummary(mPackageName);

        mClickPreference = (SwitchPreference) findPreference(getString(R.string.pref_key_detail_rule_auto_click));
        mClickPreference.setChecked(mViewRule.autoClick);
        mClickPreference.setOnPreferenceChangeListener(this);
        mClickPreference.setOnPreferenceClickListener(preference1 -> {
            return false;
        });

        mAliasPreference = (EditTextPreference) findPreference(getString(R.string.pref_key_detail_rule_alias));
//        mAliasPreference.setTitle(R.string.rule_details_field_alias);
        mAliasPreference.setDialogTitle(R.string.rule_details_set_alias);
        mAliasPreference.setSummary(Preconditions.optionDefault(mViewRule.alias, getString(R.string.rule_details_set_alias)));
        mAliasPreference.setPersistent(false);
        mAliasPreference.setOnPreferenceChangeListener(this);
        mAliasPreference.setOnPreferenceClickListener(preference1 -> {
            ((EditTextPreference) preference1).setText(mViewRule.alias);
            return false;
        });

        mAlphaPreference = (EditTextPreference) findPreference(getString(R.string.pref_key_detail_rule_alpha));
//        mAlphaPreference.setTitle(R.string.rule_details_field_alpha);
        mAlphaPreference.setDialogTitle(R.string.rule_details_set_alpha);
        mAlphaPreference.setSummary(Preconditions.optionDefault("" + mViewRule.alpha, getString(R.string.rule_details_set_alpha)));
        mAlphaPreference.setPersistent(false);
        mAlphaPreference.setOnPreferenceChangeListener(this);
        mAlphaPreference.setOnPreferenceClickListener(preference1 -> {
            ((EditTextPreference) preference1).setText("" + mViewRule.alpha);
            return false;
        });

        mTargetWPreference = (EditTextPreference) findPreference(getString(R.string.pref_key_detail_rule_target_width));
//        mTargetWPreference.setTitle(R.string.rule_details_field_target_width);
        mTargetWPreference.setDialogTitle(R.string.rule_details_set_target_width);
        mTargetWPreference.setSummary(Preconditions.optionDefault("" + mViewRule.targetWidth, getString(R.string.rule_details_set_target_width)));
        mTargetWPreference.setPersistent(false);
        mTargetWPreference.setOnPreferenceChangeListener(this);
        mTargetWPreference.setOnPreferenceClickListener(preference1 -> {
            ((EditTextPreference) preference1).setText("" + mViewRule.targetWidth);
            return false;
        });


        mTargetHPreference = (EditTextPreference) findPreference(getString(R.string.pref_key_detail_rule_target_height));
//        mTargetHPreference.setTitle(R.string.rule_details_field_target_height);
        mTargetHPreference.setDialogTitle(R.string.rule_details_set_target_height);
        mTargetHPreference.setSummary(Preconditions.optionDefault("" + mViewRule.targetHeight, getString(R.string.rule_details_set_target_height)));
        mTargetHPreference.setPersistent(false);
        mTargetHPreference.setOnPreferenceChangeListener(this);
        mTargetHPreference.setOnPreferenceClickListener(preference1 -> {
            ((EditTextPreference) preference1).setText("" + mViewRule.targetHeight);
            return false;
        });

        preference = findPreference(getString(R.string.pref_key_detail_view_bounds));
        Rect bounds = new Rect(mViewRule.x, mViewRule.y, mViewRule.x + mViewRule.width, mViewRule.y + mViewRule.height);
        preference.setTitle(R.string.rule_details_field_view_bounds);
        preference.setSummary(bounds.toShortString());

        preference = findPreference(getString(R.string.pref_key_detail_view_type));
        preference.setTitle(R.string.rule_details_field_view_type);
        preference.setSummary(mViewRule.viewClass);

        preference = findPreference(getString(R.string.pref_key_detail_view_depth));
        preference.setTitle(R.string.rule_details_field_view_depth);
        preference.setSummary(Arrays.toString(mViewRule.depth));

        preference = findPreference(getString(R.string.pref_key_detail_view_res_name));
        preference.setTitle(R.string.rule_details_field_res_name);
        preference.setSummary(mViewRule.resourceName);

        if (!TextUtils.isEmpty(mViewRule.text)) {
            preference = findPreference(getString(R.string.pref_key_detail_view_text));
            preference.setTitle(R.string.rule_details_field_text);
            preference.setSummary(mViewRule.text);
            preference.setVisible(true);
        }
        if (!TextUtils.isEmpty(mViewRule.description)) {
            preference = findPreference(getString(R.string.pref_key_detail_view_description));
            preference.setTitle(R.string.rule_details_field_description);
            preference.setSummary(mViewRule.description);
            preference.setVisible(true);
        }

        mVisiblePreference = (DropDownPreference) findPreference(getString(R.string.pref_key_detail_view_visible));
        mVisiblePreference.setOnPreferenceChangeListener(this);
//        mVisiblePreference.setTitle(R.string.rule_details_field_visible);
//        String[] entries = getResources().getStringArray(R.array.visible_entries);
//        String[] values = getResources().getStringArray(R.array.visible_values);
        mVisiblePreference.setSummary("%s");
//        mVisiblePreference.setEntries(entries);
//        mVisiblePreference.setEntryValues(values);
        mVisiblePreference.setValue(String.valueOf(mViewRule.visibility));
        mTargetTypePreference = (DropDownPreference) findPreference(getString(R.string.pref_key_detail_view_target_wh_type));
        mTargetTypePreference.setOnPreferenceChangeListener(this);
//        mTargetWHPreference.setTitle(R.string.rule_details_field_ta);
//        String[] entries = getResources().getStringArray(R.array.ta);
//        String[] values = getResources().getStringArray(R.array.visible_values);
        mTargetTypePreference.setSummary("%s");
//        mTargetWHPreference.setEntries(entries);
//        mTargetWHPreference.setEntryValues(values);
        mTargetTypePreference.setValue(String.valueOf(mViewRule.targetParamType));
        if (!TextUtils.isEmpty(mViewRule.imagePath)) {
            LoaderManager.getInstance(this).initLoader(0, null, this).forceLoad();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_app_rule, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            mSharedViewModel.deleteRule(mViewRule);
            deleted = true;
            requireActivity().onBackPressed();
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleNullableBitmap(mRuleImageBitmap);
//        if (!deleted){
//            mSharedViewModel.updateRule(mViewRule);
//        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mSwitchPreference) {
            mViewRule.enable = !mViewRule.enable;
        } else  if (preference == mClickPreference) {
            mViewRule.autoClick = !mViewRule.autoClick;
        } else if (preference == mAliasPreference) {
            mViewRule.alias = (String) newValue;
            preference.setSummary(mViewRule.alias);
        } else if (preference == mAlphaPreference) {
            try {
                int alpha = Integer.parseInt((String) newValue);
                if (alpha < 0 || alpha > 255) {
                    throw new NumberFormatException("alpha should be between 0 and 255");
                }
                mViewRule.alpha = alpha;
                preference.setSummary("" + mViewRule.alpha);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "input error: please input integer between 0 and 255", Toast.LENGTH_SHORT).show();
            }
        } else if (preference == mTargetTypePreference) {
            mViewRule.targetParamType = Integer.parseInt((String) newValue);
        } else if (preference == mTargetWPreference || preference == mTargetHPreference) {
            try {
                int num = Integer.parseInt((String) newValue);
                if (preference == mTargetWPreference) {
                    mViewRule.targetWidth = num;
                } else {
                    mViewRule.targetHeight = num;
                }
                preference.setSummary("" + num);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "input error: please input integer", Toast.LENGTH_SHORT).show();
            }
//            mSharedViewModel.updateRule(mViewRule);
        } else if (preference == mVisiblePreference) {
            mViewRule.visibility = Integer.parseInt((String) newValue);
        }
        mSharedViewModel.updateRule(mViewRule);
        return true;
    }

    @NonNull
    @Override
    public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
        return new RuleImageLoader(requireContext(), mViewRule);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap bitmap) {
        if (bitmap != null) {
            mRuleImageBitmap = bitmap;
            ImageViewPreference imageViewPreference = new ImageViewPreference(getContext());
            imageViewPreference.setImageBitmap(bitmap);
            getPreferenceScreen().addPreference(imageViewPreference);
        }
        LoaderManager.getInstance(this).destroyLoader(0);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Bitmap> loader) {
    }

    static final class RuleImageLoader extends AsyncTaskLoader<Bitmap> {

        private final ViewRule mViewRule;

        public RuleImageLoader(@NonNull Context context, ViewRule viewRule) {
            super(context);
            this.mViewRule = viewRule;
        }

        @Nullable
        @Override
        public Bitmap loadInBackground() {
            try {
                try (ParcelFileDescriptor parcelFileDescriptor = GodModeManager.getDefault().openImageFileDescriptor(mViewRule.imagePath)) {
                    Objects.requireNonNull(parcelFileDescriptor, String.format("Can not open %s", mViewRule.imagePath));
                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.getFileDescriptor()).copy(Bitmap.Config.ARGB_8888, true);
                    Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
                    Paint markPaint = new Paint();
                    markPaint.setColor(Color.RED);
                    markPaint.setAlpha(100);
                    Canvas canvas = new Canvas(newBitmap);
                    canvas.drawRect(mViewRule.x, mViewRule.y, mViewRule.x + mViewRule.width, mViewRule.y + mViewRule.height, markPaint);
                    return newBitmap;
                }
            } catch (Exception e) {
                Logger.w(TAG, e.getMessage());
                return null;
            }
        }
    }
}
