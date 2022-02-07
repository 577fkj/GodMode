package tiiehenry.android.view.recyclerview.holder;

import tiiehenry.android.view.base.holder.IViewHolder;

public interface IRecyclerViewHolder<IVIEWHOLDER> extends IViewHolder<IVIEWHOLDER> {
    int getLayoutPosition();

    int getPosition();
}
