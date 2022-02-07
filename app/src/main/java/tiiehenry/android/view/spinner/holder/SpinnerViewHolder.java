package tiiehenry.android.view.spinner.holder;

import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;

public class SpinnerViewHolder implements ISpinnerViewHolder<SpinnerViewHolder> {
    @NonNull
    private final View itemView;

    private SparseArray<View> mViews;

    public SpinnerViewHolder(View itemView) {
        this.itemView = itemView;
        mViews = new SparseArray<>();
    }

    @NonNull
    @Override
    public View getItemView() {
        return itemView;
    }

    @Override
    public <T extends View> T findView(int id) {
        View view = mViews.get(id);
        if (view == null) {
            view = itemView.findViewById(id);
            mViews.put(id, view);
        }
        return (T) view;
    }

    @Override
    public void clearViewCache() {
        if (mViews != null) {
            mViews.clear();
        }
    }

    @NonNull
    @Override
    public SpinnerViewHolder getInstance() {
        return this;
    }
}
