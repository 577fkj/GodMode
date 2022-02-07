package tiiehenry.android.view.base.holder;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

public interface IViewHolder<IVIEWHOLDER> {

    @NonNull
    View getItemView();


    /**
     * 寻找控件
     * 使用缓存
     *
     * @param id
     * @return
     */
    <T extends View> T findView(@IdRes int id);

    /**
     * 清除控件缓存
     */
    void clearViewCache();

    /**
     * you need return this in child class
     * @return
     */
    @NonNull
    IVIEWHOLDER getInstance();

    @NonNull
    default Context getContext() {
        return getItemView().getContext();
    }

    /**
     * 官方调用
     *
     * @param viewId
     * @param <T>
     * @return
     */
    default <T extends View> T findViewById(@IdRes int viewId) {
        return getItemView().findViewById(viewId);
    }

    default View getView(@IdRes int viewId) {
        return findViewById(viewId);
    }

    default ImageView getImageView(@IdRes int viewId) {
        return findView(viewId);
    }

    default ImageButton getImageButton(@IdRes int viewId) {
        return findView(viewId);
    }

    default TextView getTextView(@IdRes int viewId) {
        return findView(viewId);
    }

    default Button getButton(@IdRes int viewId) {
        return findView(viewId);
    }

    default EditText getEditText(@IdRes int viewId) {
        return findView(viewId);
    }

    default CheckBox getCheckBox(@IdRes int viewId) {
        return findView(viewId);
    }

    default Switch getSwitch(@IdRes int viewId) {
        return findView(viewId);
    }

    default SwitchCompat getSwitchCompat(@IdRes int viewId) {
        return findView(viewId);
    }


    /**
     * 设置文字
     *
     * @param id
     * @param sequence
     * @return
     */
    default IVIEWHOLDER text(@IdRes int id, CharSequence sequence) {
        getTextView(id).setText(sequence);
        return getInstance();
    }

    /**
     * 设置文字
     *
     * @param id
     * @param stringRes
     * @return
     */
    default IVIEWHOLDER text(@IdRes int id, @StringRes int stringRes) {
        getTextView(id).setText(stringRes);
        return getInstance();
    }

    /**
     * 设置文字的颜色
     *
     * @param id
     * @param colorId
     * @return
     */
    default IVIEWHOLDER textColorId(@IdRes int id, @ColorRes int colorId) {
        TextView view = getTextView(id);
        view.setTextColor(ContextCompat.getColor(view.getContext(), colorId));
        return getInstance();
    }

    /**
     * 设置图片
     *
     * @param id
     * @param imageId
     * @return
     */
    default IVIEWHOLDER image(@IdRes int id, @DrawableRes int imageId) {
        getImageView(id).setImageResource(imageId);
        return getInstance();
    }

    /**
     * 设置图片
     *
     * @param id
     * @param drawable
     * @return
     */
    default IVIEWHOLDER image(@IdRes int id, @Nullable Drawable drawable) {
        getImageView(id).setImageDrawable(drawable);
        return getInstance();
    }

    /**
     * 设置图片的等级
     *
     * @param id
     * @param level
     * @return
     */
    default IVIEWHOLDER imageLevel(@IdRes int id, int level) {
        getImageView(id).setImageLevel(level);
        return getInstance();
    }

    /**
     * 给图片着色
     *
     * @param id
     * @param tint 颜色
     * @return
     */
    default IVIEWHOLDER tint(@IdRes int id, @Nullable ColorStateList tint) {
        getImageView(id).setImageTintList(tint);
        return getInstance();
    }

    /**
     * 设置布局内控件的点击事件【包含索引】
     *
     * @param id
     * @param listener
     * @param position
     * @return
     */
    default <T> IVIEWHOLDER viewClick(@IdRes int id, @Nullable OnViewItemClickListener<T> listener, T item, int position) {
        View view = findView(id);
        view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewItemClick(v, item, position);
            }
        });
        return getInstance();
    }

    /**
     * 设置控件的点击监听
     *
     * @param id
     * @param listener
     * @return
     */
    default IVIEWHOLDER click(@IdRes int id, @Nullable View.OnClickListener listener) {
        findView(id).setOnClickListener(listener);
        return getInstance();
    }

    /**
     * 设置控件是否可显示
     *
     * @param id
     * @param visibility
     * @return
     */
    default IVIEWHOLDER visible(@IdRes int id, int visibility) {
        findView(id).setVisibility(visibility);
        return getInstance();
    }

    /**
     * 设置输入框是否可编辑
     *
     * @param id
     * @param enable
     * @return
     */
    default IVIEWHOLDER enable(@IdRes int id, boolean enable) {
        View view = findView(id);
        view.setEnabled(enable);
        if (view instanceof EditText) {
            view.setFocusable(enable);
            view.setFocusableInTouchMode(enable);
        }
        return getInstance();
    }

    /**
     * 这是CompoundButton控件选中状态
     *
     * @param id
     * @param checked
     * @return
     */
    default IVIEWHOLDER checked(@IdRes int id, boolean checked) {
        CompoundButton view = findView(id);
        view.setChecked(checked);
        return getInstance();
    }

    /**
     * 设置控件选择监听
     *
     * @param id
     * @param listener
     * @return
     */
    default IVIEWHOLDER checkedListener(@IdRes int id, CompoundButton.OnCheckedChangeListener listener) {
        CompoundButton view = findView(id);
        view.setOnCheckedChangeListener(listener);
        return getInstance();
    }

    /**
     * 设置控件是否选中
     *
     * @param id
     * @param selected
     * @return
     */
    default IVIEWHOLDER select(@IdRes int id, boolean selected) {
        View view = findView(id);
        view.setSelected(selected);
        return getInstance();
    }

    /**
     * 设置文字变化监听
     *
     * @param id
     * @param watcher
     * @return
     */
    default IVIEWHOLDER textListener(@IdRes int id, TextWatcher watcher) {
        getTextView(id).addTextChangedListener(watcher);
        return getInstance();
    }

    /**
     * 设置背景
     *
     * @param viewId
     * @param resId
     * @return
     */
    default IVIEWHOLDER background(@IdRes int viewId, @DrawableRes int resId) {
        getView(viewId).setBackgroundResource(resId);
        return getInstance();
    }

    /**
     * 设置背景
     *
     * @param viewId
     * @param drawable
     * @return
     */
    default IVIEWHOLDER background(@IdRes int viewId, Drawable drawable) {
        getView(viewId).setBackground(drawable);
        return getInstance();
    }

}
