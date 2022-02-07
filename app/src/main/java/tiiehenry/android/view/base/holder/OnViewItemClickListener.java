package tiiehenry.android.view.base.holder;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * 布局内控件点击事件
 */
public interface OnViewItemClickListener<DATATYPE> {
    /**
     * 控件被点击
     *
     * @param view     被点击的控件
     * @param item     数据
     * @param position 索引
     */
    void onViewItemClick(@NonNull View view, @NonNull DATATYPE item, int position);
}
