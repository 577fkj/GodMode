package tiiehenry.android.view.spinner.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tiiehenry.android.view.base.adapter.IAdapter;
import tiiehenry.android.view.base.adapter.INotifier;
import tiiehenry.android.view.base.adapter.wrapped.IAllChangedNotifier;
import tiiehenry.android.view.base.holder.OnItemClickListener;
import tiiehenry.android.view.base.holder.OnItemLongClickListener;
import tiiehenry.android.view.spinner.holder.ISpinnerViewHolder;

public interface ISpinnerAdapter<IADAPTER extends ISpinnerAdapter
        , DATATYPE
        , VH extends ISpinnerViewHolder>
        extends IAdapter<IADAPTER, DATATYPE>, IAllChangedNotifier {


    @NonNull
    @Override
    default INotifier getNotifier() {
        return getInstance();
    }
}
