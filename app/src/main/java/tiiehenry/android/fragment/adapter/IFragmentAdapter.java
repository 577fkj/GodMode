package tiiehenry.android.fragment.adapter;

import androidx.annotation.NonNull;

import tiiehenry.android.view.base.adapter.IAdapter;
import tiiehenry.android.view.base.adapter.INotifier;
import tiiehenry.android.view.base.adapter.wrapped.IAllChangedNotifier;

public interface IFragmentAdapter<IADAPTER extends IFragmentAdapter
        , DATATYPE>
        extends IAdapter<IADAPTER, DATATYPE>, IAllChangedNotifier {

    @NonNull
    @Override
    default INotifier getNotifier() {
        return getInstance();
    }
}
