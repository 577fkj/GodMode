package tiiehenry.viewcontroller.rule;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by jrsen on 17-10-14.
 */
@Keep
public final class ViewRule implements Parcelable, Cloneable {

    //是否启用
    @SerializedName("enable")
    public boolean enable = true;
    //生成规则的应用名称
    @SerializedName("label")
    public final String label;
    //生成规则的应用包名
    @SerializedName("package_name")
    public final String packageName;
    @SerializedName("match_version_name")
    public final String matchVersionName;
    //生成规则的应用版本号
    @SerializedName("match_version_code")
    public final int matchVersionCode;
    //规则版本
    @SerializedName("version_code")
    public final int versionCode;
    //规则图片
    @SerializedName("img_path")
    public String imagePath;
    //规则别名
    @SerializedName("alias")
    public String alias;
    //控件透明度 0-255 View.setAlpha(0-1f)
    @SerializedName("alpha")
    public int alpha = 255;
    //相对于window的x坐标
    @SerializedName("x")
    public final int x;
    //相对于window的y坐标
    @SerializedName("y")
    public final int y;
    //控件宽度
    @SerializedName("width")
    public final int width;
    //控件高度
    @SerializedName("height")
    public final int height;
    //布局深度
    @SerializedName("depth")
    public final int[] depth;
    //控件所属activity
    @SerializedName("act_class")
    public final String activityClass;
    //控件类型
    @SerializedName("view_class")
    public final String viewClass;
    //资源id
    @SerializedName("res_name")
    public final String resourceName;
    //控件文字
    @SerializedName("text")
    public final String text;
    //控件描述
    @SerializedName("description")
    public final String description;
    //自动点击
    @SerializedName("auto_click")
    public boolean autoClick = false;
    //View可见性
    @SerializedName("visibility")
    public int visibility;
    //View的调整宽度的类型，0表示不用，1表示宽度，2表示高度，3表示宽高
    @SerializedName("param_target_type")
    public int targetParamType=0;
    //View的调整宽度
    @SerializedName("param_target_width")
    public int targetWidth=0;
    //View的调整高度
    @SerializedName("param_target_height")
    public int targetHeight=0;
    //规则记录时间
    @SerializedName("timestamp")
    public final long timestamp;

    public float getAlphaNormalized() {
        return (0f+alpha)/255;
    }

    public ViewRule(Boolean enable, String label, String packageName, String matchVersionName, int matchVersionCode, int versionCode, String imagePath, String alias, int alpha, int x, int y, int width, int height, int[] depth, String activityClass, String viewClass, String resourceName, String text, String description,boolean autoClick, int visibility, int targetParamType, int targetWidth, int targetHeight, long timestamp) {
        this.enable = enable;
        this.label = label;
        this.packageName = packageName;
        this.matchVersionName = matchVersionName;
        this.matchVersionCode = matchVersionCode;
        this.versionCode = versionCode;
        this.imagePath = imagePath;
        this.alias = alias;
        this.alpha = alpha;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.activityClass = activityClass;
        this.viewClass = viewClass;
        this.resourceName = resourceName;
        this.text = text;
        this.description = description;
        this.autoClick = autoClick;
        this.visibility = visibility;
        this.targetParamType = targetParamType;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
        this.timestamp = timestamp;
    }

    public ViewRule copy(ViewRule rule) {
        this.enable = rule.enable;
//        this.label = label;
//        this.packageName = packageName;
//        this.matchVersionName = matchVersionName;
//        this.matchVersionCode = matchVersionCode;
//        this.versionCode = versionCode;
        this.imagePath = rule.imagePath;
        this.alias = rule.alias;
        this.alpha = rule.alpha;
//        this.x = x;
//        this.y = y;
//        this.width = width;
//        this.height = height;
//        this.depth = depth;
//        this.activityClass = activityClass;
//        this.viewClass = viewClass;
//        this.resourceName = resourceName;
//        this.text = text;
//        this.description = description;
        this.autoClick = rule.autoClick;
        this.visibility = rule.visibility;
        this.targetParamType = rule.targetParamType;
        this.targetWidth = rule.targetWidth;
        this.targetHeight = rule.targetHeight;
//        this.timestamp = timestamp;
        return this;
    }

    protected ViewRule(Parcel in) {
        enable = in.readInt() != 0;
        label = in.readString();
        packageName = in.readString();
        matchVersionName = in.readString();
        matchVersionCode = in.readInt();
        versionCode = in.readInt();
        imagePath = in.readString();
        alias = in.readString();
        alpha = in.readInt();
        x = in.readInt();
        y = in.readInt();
        width = in.readInt();
        height = in.readInt();
        depth = in.createIntArray();
        activityClass = in.readString();
        viewClass = in.readString();
        resourceName = in.readString();
        text = in.readString();
        description = in.readString();
        autoClick = in.readInt() != 0;
        visibility = in.readInt();
        targetParamType = in.readInt();
        targetWidth = in.readInt();
        targetHeight = in.readInt();
        timestamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (enable) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeString(label);
        dest.writeString(packageName);
        dest.writeString(matchVersionName);
        dest.writeInt(matchVersionCode);
        dest.writeInt(versionCode);
        dest.writeString(imagePath);
        dest.writeString(alias);
        dest.writeInt(alpha);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeIntArray(depth);
        dest.writeString(activityClass);
        dest.writeString(viewClass);
        dest.writeString(resourceName);
        dest.writeString(text);
        dest.writeString(description);
        if (autoClick) {
            dest.writeInt(1);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(visibility);
        dest.writeInt(targetParamType);
        dest.writeInt(targetWidth);
        dest.writeInt(targetHeight);
        dest.writeLong(timestamp);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ViewRule> CREATOR = new Creator<ViewRule>() {
        @Override
        public ViewRule createFromParcel(Parcel in) {
            return new ViewRule(in);
        }

        @Override
        public ViewRule[] newArray(int size) {
            return new ViewRule[size];
        }
    };

    @NonNull
    @Override
    public ViewRule clone() {
        ViewRule viewRule = new ViewRule(
                enable,
                label,
                packageName,
                matchVersionName,
                matchVersionCode,
                versionCode,
                imagePath,
                alias,
                alpha,
                x,
                y,
                width,
                height,
                depth,
                activityClass,
                viewClass,
                resourceName,
                text,
                description,
                autoClick,
                visibility,
                targetParamType,
                targetWidth,
                targetHeight,
                timestamp);
        return viewRule;
    }

    public int getViewId(Resources res) {
        if (!TextUtils.isEmpty(resourceName)) {
            String[] start = resourceName.split(":");
            String[] end = start[1].split("/");
            String resourcePackageName = start[0];
            String resourceTypeName = end[0];
            String resourceEntryName = end[1];
            return res.getIdentifier(resourceEntryName, resourceTypeName, resourcePackageName);
        } else {
            return View.NO_ID;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewRule viewRule = (ViewRule) o;

        if (enable != viewRule.enable) return false;
//        if (targetParamType != viewRule.targetParamType) return false;
        if (!activityClass.equals(viewRule.activityClass)) return false;
        if (!viewClass.equals(viewRule.viewClass)) return false;
        return Arrays.equals(depth, viewRule.depth);
    }

    public boolean equalsSimple(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ViewRule viewRule = (ViewRule) o;

//        if (!enable == viewRule.enable) return false;
        if (!activityClass.equals(viewRule.activityClass)) return false;
        if (!viewClass.equals(viewRule.viewClass)) return false;
        return Arrays.equals(depth, viewRule.depth);
    }

    @Override
    public int hashCode() {
        int result = activityClass.hashCode();
        result = 31 * result + viewClass.hashCode();
        result = 31 * result + Arrays.hashCode(depth);
        return result;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ViewRule{");
        sb.append("label='").append(label).append('\'');
        sb.append(", packageName='").append(packageName).append('\'');
        sb.append(", matchVersionName='").append(matchVersionName).append('\'');
        sb.append(", matchVersionCode=").append(matchVersionCode);
        sb.append(", versionCode=").append(versionCode);
        sb.append(", imagePath='").append(imagePath).append('\'');
        sb.append(", alias='").append(alias).append('\'');
        sb.append(", alpha=").append(alpha);
        sb.append(", x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", width=").append(width);
        sb.append(", height=").append(height);
        sb.append(", depth=");
        if (depth == null) sb.append("null");
        else {
            sb.append('[');
            for (int i = 0; i < depth.length; ++i)
                sb.append(i == 0 ? "" : ", ").append(depth[i]);
            sb.append(']');
        }
        sb.append(", activityClass='").append(activityClass).append('\'');
        sb.append(", viewClass='").append(viewClass).append('\'');
        sb.append(", resourceName='").append(resourceName).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", autoClick=").append(autoClick);
        sb.append(", visibility=").append(visibility);
        sb.append(", targetParamType=").append(targetParamType);
        sb.append(", targetWidth=").append(targetWidth);
        sb.append(", targetHeight=").append(targetHeight);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", enable=").append(enable);
        sb.append('}');
        return sb.toString();
    }
}
