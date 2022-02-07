package tiiehenry.viewcontroller.rule;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by jrsen on 17-10-14.
 * activityclass:rules
 */
@Keep
public final class ActRules extends HashMap<String, List<ViewRule>> implements Parcelable {

    private int enabled = 1;

    public ActRules() {
    }

    public void enable() {
        enabled = 1;
    }

    public void disable() {
        enabled = 0;
    }

    public void setEnabled(boolean e) {
        if (e) {
            enabled = 1;
        } else {
            enabled = 0;
        }
    }

    public boolean isEnabled() {
        return enabled != 0;
    }

    public ActRules(ActRules actRules) {
        super(actRules);
        this.enabled = actRules.enabled;
    }

    public ActRules(int initialCapacity) {
        super(initialCapacity);
    }

    protected ActRules(Parcel in) {
        in.readMap(this, getClass().getClassLoader());
        enabled = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeMap(this);
        dest.writeInt(enabled);
    }

    public void removeRules(ActRules remove) {
        Set<Entry<String, List<ViewRule>>> entries = remove.entrySet();
        // sActRules contains old rules
        for (Map.Entry<String, List<ViewRule>> entry : entries) {
            String key = entry.getKey();
            List<ViewRule> oldRules = this.get(key);
            List<ViewRule> newRules = entry.getValue();
            if (newRules != null && oldRules != null) {
                oldRules.removeAll(newRules);
                if (oldRules.isEmpty()) this.remove(key);
            }
        }
    }

    public void removeRulesByAllEqual(ActRules remove) {
        Set<Entry<String, List<ViewRule>>> entries = remove.entrySet();
        // sActRules contains old rules
        for (Map.Entry<String, List<ViewRule>> entry : entries) {
            String key = entry.getKey();
            List<ViewRule> oldRules = this.get(key);
            List<ViewRule> newRules = entry.getValue();
            if (newRules != null && oldRules != null) {
//                for (ViewRule oldRule : oldRules) {
//
//                }
                oldRules.removeAll(newRules);
                if (oldRules.isEmpty()) this.remove(key);
            }
        }
    }

    /**
     * 添加不重复
     * @param add
     */
    public void addRules(ActRules add) {
        Set<Entry<String, List<ViewRule>>> entries = add.entrySet();
        // sActRules contains old rules
        for (Map.Entry<String, List<ViewRule>> entry : entries) {
            String key = entry.getKey();
            List<ViewRule> oldRules = this.get(key);
            ArrayList<ViewRule> rules;

            if (oldRules != null) {
                rules=new ArrayList<>(oldRules);
            }else {
                rules=new ArrayList<>();
            }
            List<ViewRule> newRules = entry.getValue();
            if (newRules != null) {
                rules.removeAll(newRules);
                rules.addAll(newRules);
            }
            this.put(key,rules);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ActRules> CREATOR = new Creator<ActRules>() {
        @Override
        public ActRules createFromParcel(Parcel in) {
            return new ActRules(in);
        }

        @Override
        public ActRules[] newArray(int size) {
            return new ActRules[size];
        }
    };

}
