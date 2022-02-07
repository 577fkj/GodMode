package tiiehenry.android.view.recyclerview.holder;


import android.util.SparseArray;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class RecyclerViewHolder extends RecyclerView.ViewHolder implements IRecyclerViewHolder<RecyclerViewHolder> {

    private SparseArray<View> mViews;

    public RecyclerViewHolder(View itemView) {
        super(itemView);
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
    public RecyclerViewHolder getInstance() {
        return this;
    }
}
