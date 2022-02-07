package com.kaisar.xposed.godmode.rule;

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
    //View可见性
    @SerializedName("visibility")
    public int visibility;
    //规则记录时间
    @SerializedName("timestamp")
    public final long timestamp;

    public ViewRule(String label, String packageName, String matchVersionName, int matchVersionCode, int versionCode, String imagePath, String alias, int x, int y, int width, int height, int[] depth, String activityClass, String viewClass, String resourceName, String text, String description, int visibility, long timestamp) {
        this.label = label;
        this.packageName = packageName;
        this.matchVersionName = matchVersionName;
        this.matchVersionCode = matchVersionCode;
        this.versionCode = versionCode;
        this.imagePath = imagePath;
        this.alias = alias;
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
        this.visibility = visibility;
        this.timestamp = timestamp;
    }

    protected ViewRule(Parcel in) {
        label = in.readString();
        packageName = in.readString();
        matchVersionName = in.readString();
        matchVersionCode = in.readInt();
        versionCode = in.readInt();
        imagePath = in.readString();
        alias = in.readString();
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
        visibility = in.readInt();
        timestamp = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(packageName);
        dest.writeString(matchVersionName);
        dest.writeInt(matchVersionCode);
        dest.writeInt(versionCode);
        dest.writeString(imagePath);
        dest.writeString(alias);
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
        dest.writeInt(visibility);
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
                label,
                packageName,
                matchVersionName,
                matchVersionCode,
                versionCode,
                imagePath,
                alias,
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
                visibility,
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
        sb.append(", visibility=").append(visibility);
        sb.append(", timestamp=").append(timestamp);
        sb.append('}');
        return sb.toString();
    }
}
