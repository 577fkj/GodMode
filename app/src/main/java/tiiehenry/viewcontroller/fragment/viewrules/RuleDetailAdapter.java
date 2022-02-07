package tiiehenry.viewcontroller.fragment.viewrules;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import tiiehenry.viewcontroller.R;
import tiiehenry.viewcontroller.SettingsActivity;
import tiiehenry.viewcontroller.fragment.ruledetails.ViewRuleDetailsFragment;
import tiiehenry.viewcontroller.rule.ViewRule;

import tiiehenry.android.view.recyclerview.adapter.SimpleIdRecyclerAdapter;
import tiiehenry.android.view.recyclerview.holder.RecyclerViewHolder;

public class RuleDetailAdapter extends SimpleIdRecyclerAdapter<ViewRule> {
    private final ViewRuleListFragment viewRuleListFragment;

    public RuleDetailAdapter(ViewRuleListFragment viewRuleListFragment) {
        super(androidx.preference.R.layout.preference);
        this.viewRuleListFragment = viewRuleListFragment;
    }


    @Override
    protected void bindData(@NonNull RecyclerViewHolder holder, int position, @NonNull ViewRule item) {
        Context context = holder.getContext();
        TextView mSummaryTextView = holder.getTextView(android.R.id.summary);
        ViewRule viewRule = mData.get(position);
        Glide.with(context).load(viewRule).placeholder(viewRuleListFragment.getIcon()).diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.getImageView(android.R.id.icon));
        if (viewRule.activityClass != null && viewRule.activityClass.lastIndexOf('.') > -1) {
            String activityName = viewRule.activityClass.substring(viewRule.activityClass.lastIndexOf('.') + 1);
            SpannableStringBuilder titleBuilder = new SpannableStringBuilder();
            if (!viewRule.enable) {
                SpannableString senable = new SpannableString("*");
                senable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.prefsAliasColor)), 0, senable.length(), 0);
                titleBuilder.append(senable);
            }
            titleBuilder.append(context.getString(R.string.field_activity, activityName));
            mSummaryTextView.setText(titleBuilder);
        }

        SpannableStringBuilder summaryBuilder = new SpannableStringBuilder();

        if (!TextUtils.isEmpty(viewRule.alias)) {
            SpannableString ss = new SpannableString(context.getString(R.string.field_rule_alias, viewRule.alias));
            ss.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.prefsAliasColor)), 0, ss.length(), 0);
            summaryBuilder.append(ss);
        }
        summaryBuilder.append(context.getString(R.string.field_view, viewRule.viewClass));
        mSummaryTextView.setText(summaryBuilder);
        holder.itemView.setFocusable(true);
        holder.itemView.setClickable(true);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewRuleDetailsFragment fragment = new ViewRuleDetailsFragment();
                fragment.setIcon(viewRuleListFragment.getIcon());
                fragment.setLabel(viewRuleListFragment.getLabel());
                fragment.setPackageName(viewRuleListFragment.getPackageName());
                fragment.setViewRule(viewRule);

                SettingsActivity activity = (SettingsActivity) fragment.requireActivity();
                activity.startPreferenceFragment(fragment);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String disableText = viewRule.enable ? "disable" : "enable";
                DialogInterface.OnClickListener disableListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        viewRule.enable = !viewRule.enable;
                        viewRuleListFragment.getSharedViewModel().updateRule(viewRule);
                        notifyItemChanged(holder.getAdapterPosition());
                    }
                };
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Operate")
                        .setMessage("Select one action")
                        .setNeutralButton(disableText, disableListener)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton("delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                viewRuleListFragment.getSharedViewModel().deleteRule(viewRule);
                            }
                        }).show();
                return true;
            }
        });
    }
}