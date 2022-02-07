package tiiehenry.android.view.recyclerview.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tiiehenry.android.view.base.adapter.IAdapter;
import tiiehenry.android.view.base.holder.OnItemClickListener;
import tiiehenry.android.view.base.holder.OnItemLongClickListener;
import tiiehenry.android.view.recyclerview.holder.IRecyclerViewHolder;

public interface IRecyclerAdapter<IADAPTER extends IRecyclerAdapter, DATATYPE, VH extends IRecyclerViewHolder> extends IAdapter<IADAPTER,DATATYPE> {

    @NonNull
    IADAPTER setOnItemClickListener(@Nullable OnItemClickListener<DATATYPE> listener);

    @NonNull
    IADAPTER setOnItemLongClickListener(@Nullable OnItemLongClickListener<DATATYPE> listener);

}
