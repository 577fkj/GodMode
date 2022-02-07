package tiiehenry.android.view.recyclerview.adapter;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import java.util.Collection;

import tiiehenry.android.view.recyclerview.adapter.InflateRecyclerAdapter;

public abstract class SimpleIdRecyclerAdapter<T> extends InflateRecyclerAdapter<T> {

    private final int layoutId;

    public SimpleIdRecyclerAdapter(@LayoutRes int layoutId) {
        super();
        this.layoutId = layoutId;
    }

    public SimpleIdRecyclerAdapter(@LayoutRes int layoutId, @NonNull Collection<T> list) {
        super(list);
        this.layoutId = layoutId;
    }

    public SimpleIdRecyclerAdapter(@LayoutRes int layoutId,@NonNull  T[] data) {
        super(data);
        this.layoutId = layoutId;
    }

    /**
     * 适配的布局
     *
     * @param parent
     * @param viewType
     * @return layout
     */
    protected View inflateItemLayout(ViewGroup parent, int viewType) {
        return inflateView(parent, layoutId);
    }

    @NonNull
    @Override
    public InflateRecyclerAdapter<T> getInstance() {
        return this;
    }

}
