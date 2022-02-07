package tiiehenry.android.view.base.holder;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * 列表条目长按监听
 */
public interface OnItemLongClickListener<DATATYPE> {
    /**
     * 条目长按
     *
     * @param itemView 条目
     * @param item     数据
     * @param position 索引
     */
    void onItemLongClick(@NonNull View itemView, @NonNull DATATYPE item, int position);
}
