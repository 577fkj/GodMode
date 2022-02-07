package tiiehenry.android.view.base.holder;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * 列表条目点击监听
 */
public interface OnItemClickListener<DATATYPE> {
    /**
     * 条目点击
     *
     * @param itemView 条目
     * @param item     数据
     * @param position 索引
     */
    void onItemClick(@NonNull View itemView, @NonNull DATATYPE item, int position);
}
