package tiiehenry.android.view.recyclerview.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.Collection;

public abstract class BaseIdRecyclerAdapter<T> extends InflateRecyclerAdapter<T> {

    public BaseIdRecyclerAdapter() {
        super();
    }

    public BaseIdRecyclerAdapter(@NonNull Collection<T> list) {
        super(list);
    }

    public BaseIdRecyclerAdapter(@NonNull T[] data) {
        super(data);
    }

    /**
     * 适配的布局
     *
     * @param viewType
     * @return
     */
    protected abstract int getItemLayoutId(int viewType);

    @Override
    protected View inflateItemLayout(ViewGroup parent, int viewType) {
        return inflateView(parent, getItemLayoutId(viewType));
    }

    @NonNull
    @Override
    public InflateRecyclerAdapter<T> getInstance() {
        return this;
    }
}
