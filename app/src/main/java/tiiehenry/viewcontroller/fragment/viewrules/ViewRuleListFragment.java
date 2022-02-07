package tiiehenry.viewcontroller.fragment.viewrules;

import android.Manifest;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import tiiehenry.viewcontroller.R;
import tiiehenry.viewcontroller.backup.RuleExporter;
import tiiehenry.viewcontroller.injection.bridge.GodModeManager;
import tiiehenry.viewcontroller.injection.util.Logger;
import tiiehenry.viewcontroller.model.SharedViewModel;
import tiiehenry.viewcontroller.rule.ViewRule;
import tiiehenry.viewcontroller.util.CommandUtil;
import tiiehenry.viewcontroller.util.PermissionHelper;
import tiiehenry.viewcontroller.util.Preconditions;
import tiiehenry.viewcontroller.util.ShareUtil;
import tiiehenry.viewcontroller.widget.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import tiiehenry.android.fragment.fragments.StateFragment;

/**
 * Created by jrsen on 17-10-29.
 */
public final class ViewRuleListFragment extends StateFragment {

    private View rootView;
    private Drawable mIcon;
    private CharSequence mLabel;
    private CharSequence mPackageName;

    private RecyclerView mRecyclerView;

    private SharedViewModel mSharedViewModel;
    private SwitchCompat mSwitch;
    private RuleDetailAdapter adapter;

    public void setIcon(Drawable icon) {
        mIcon = icon;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setLabel(CharSequence label) {
        mLabel = label;
    }

    public CharSequence getLabel() {
        return mLabel;
    }

    public void setPackageName(CharSequence packageName) {
        mPackageName = packageName;
    }

    public CharSequence getPackageName() {
        return mPackageName;
    }

    public SharedViewModel getSharedViewModel() {
        return mSharedViewModel;
    }

    public ViewRuleListFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        mSharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        mSharedViewModel.mSelectedPackage.observe(this, packageName -> mSharedViewModel.updateViewRuleList(packageName));
        mSharedViewModel.mActRules.observe(this, newData -> {
            if (newData.isEmpty()) {
                requireActivity().onBackPressed();
            } else {
                if (adapter != null) {
                    List<ViewRule> oldData = adapter.getDataList();
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new Callback(oldData, newData));
                    adapter.refresh(newData);
                    diffResult.dispatchUpdatesTo(adapter);
                }
            }
        });
    }

//    @NonNull
//    @Override
//    protected View getView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
//        return inflater.inflate(R.layout.list_fragment_layout, container, false);
//    }
//
//    @Override
//    public void initView(@NonNull View rootView) {
//        mRecyclerView = Preconditions.checkNotNull(rootView).findViewById(R.id.recycler_view);
//        mSwitch = Preconditions.checkNotNull(rootView).findViewById(R.id.app_rules_switch);
//        mSwitch.setChecked(!GodModeManager.getDefault().isAppDisabled(mPackageName.toString()));
//        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                GodModeManager.getDefault().setAppEnable(mPackageName.toString(), isChecked);
//                mRecyclerView.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
//            }
//        });
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//        adapter = new RuleDetailAdapter(this);
//        mRecyclerView.setAdapter(adapter);
//    }

    @Override
    public void onResume() {
        super.onResume();
        mSharedViewModel.updateTitle(R.string.title_app_rule);
        if (adapter != null) {
//            Toast.makeText(getContext(), "+" + adapter.getData().size(), Toast.LENGTH_LONG).show();
//            Log.e("ViewRuleListFragment", "onResume: " + adapter);
//            Log.e("ViewRuleListFragment", "onResume: " + adapter.getData().size());
            adapter.notifyDataSetChanged();
        }
//        SettingsActivity activity = (SettingsActivity) requireActivity();
//        activity.startPreferenceFragment(this);
    }

    private static final class Callback extends DiffUtil.Callback {

        final List<ViewRule> mOldData, mNewData;

        private Callback(List<ViewRule> oldData, List<ViewRule> newData) {
            mOldData = oldData;
            mNewData = newData;
        }

        @Override
        public int getOldListSize() {
            return mOldData.size();
        }

        @Override
        public int getNewListSize() {
            return mNewData.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return mOldData.get(oldItemPosition).hashCode() == mNewData.get(newItemPosition).hashCode();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_app_rules, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_stop_and_run_app) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Process process = CommandUtil.su();
                        if (process == null) {
                            return;
                        }
                        CommandUtil.run(process, "am force-stop " + mPackageName + "\n");
                        process.waitFor();
                        process.destroy();
                        rootView.post(new Runnable() {
                            @Override
                            public void run() {
                                Intent launchIntent = requireContext().getPackageManager().getLaunchIntentForPackage(mPackageName.toString());
                                startActivity(launchIntent);
                            }
                        });
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else if (item.getItemId() == R.id.menu_run_app) {
            Intent launchIntent = requireContext().getPackageManager().getLaunchIntentForPackage(mPackageName.toString());
            startActivity(launchIntent);
        } else if (item.getItemId() == R.id.menu_revoke_rules) {
            if (!mSharedViewModel.deleteAppRules(mPackageName.toString())) {
                Snackbar.make(requireActivity(), R.string.snack_bar_msg_revert_rule_fail, Snackbar.LENGTH_SHORT).show();
            }
        } else if (item.getItemId() == R.id.menu_export_rules) {
            PermissionHelper permissionHelper = new PermissionHelper(requireActivity());
            if (!permissionHelper.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionHelper.applyPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return true;
            }
            new Thread() {
                @Override
                public void run() {
                    String result = "";
                    try {
                        String filepath = RuleExporter.getExportDir(requireContext()).getPath();
                        File zipFile = RuleExporter.exportRules(filepath, Objects.requireNonNull(mSharedViewModel.mActRules.getValue()));
                        mRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(requireActivity(), getString(R.string.export_successful, filepath), Snackbar.LENGTH_LONG).show();
                                ShareUtil.share(requireContext(), zipFile);
                            }
                        });
                    } catch (Exception e) {
                        Logger.e(getClass().getName(), "export single rule fail", e);
                        mRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                Snackbar.make(requireActivity(), R.string.export_failed, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }.start();

        }
        return super.onOptionsItemSelected(item);
    }

}
