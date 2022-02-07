package tiiehenry.android.view.base.adapter.wrapped;

import tiiehenry.android.view.base.adapter.INotifier;

public interface IAllChangedNotifier extends INotifier {


    @Override
    default void notifyItemChanged(int position) {
        notifyDataSetChanged();
    }

    @Override
    default void notifyItemRangeChanged(int positionStart, int itemCount) {
        notifyDataSetChanged();
    }

    @Override
    default void notifyItemInserted(int position) {
        notifyDataSetChanged();
    }

    @Override
    default void notifyItemRangeInserted(int positionStart, int itemCount) {
        notifyDataSetChanged();
    }

    @Override
    default void notifyItemMoved(int fromPosition, int toPosition) {
        notifyDataSetChanged();
    }

    @Override
    default void notifyItemRemoved(int position) {
        notifyDataSetChanged();
    }

    @Override
    default void notifyItemRangeRemoved(int positionStart, int itemCount) {
        notifyDataSetChanged();
    }

}
