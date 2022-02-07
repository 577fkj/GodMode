package tiiehenry.android.view.recyclerview.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import tiiehenry.android.view.base.adapter.INotifier;
import tiiehenry.android.view.base.holder.OnItemClickListener;
import tiiehenry.android.view.base.holder.OnItemLongClickListener;
import tiiehenry.android.view.recyclerview.holder.RecyclerViewHolder;

public abstract class AbstractRecyclerAdapter<IADAPTER extends AbstractRecyclerAdapter
        , DATATYPE>
        extends RecyclerView.Adapter<RecyclerViewHolder>
        implements IRecyclerAdapter<IADAPTER, DATATYPE, RecyclerViewHolder>, INotifier {

    /**
     * 数据源
     */
    protected final List<DATATYPE> mData = new ArrayList<>();

    /**
     * 点击监听
     */
    private OnItemClickListener<DATATYPE> mClickListener;
    /**
     * 长按监听
     */
    private OnItemLongClickListener<DATATYPE> mLongClickListener;

    public AbstractRecyclerAdapter() {

    }

    public AbstractRecyclerAdapter(@NonNull Collection<DATATYPE> list) {
        mData.addAll(list);
    }

    public AbstractRecyclerAdapter(@NonNull DATATYPE[] data) {
        if (data.length > 0) {
            mData.addAll(Arrays.asList(data));
        }
    }

    /**
     * 构建自定义的ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    protected abstract RecyclerViewHolder getViewHolder(@NonNull ViewGroup parent, int viewType);

    /**
     * 绑定数据
     *
     * @param holder
     * @param position 索引
     * @param item     列表项
     */
    protected abstract void bindData(@NonNull RecyclerViewHolder holder, int position, @NonNull DATATYPE item);

    /**
     * 加载布局获取控件
     *
     * @param parent   父布局
     * @param layoutId 布局ID
     * @return
     */
    protected View inflateView(ViewGroup parent, @LayoutRes int layoutId) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }


    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final RecyclerViewHolder holder = getViewHolder(parent, viewType);
        if (mClickListener != null) {
            holder.getItemView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onItemClick(holder.getItemView(), getData(holder.getLayoutPosition()), holder.getLayoutPosition());
                }
            });
        }
        if (mLongClickListener != null) {
            holder.getItemView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onItemLongClick(holder.getItemView(), getData(holder.getLayoutPosition()), holder.getLayoutPosition());
                    return true;
                }
            });
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        bindData(holder, position, mData.get(position));
    }


    /**
     * 设置列表项点击监听
     *
     * @param listener
     * @return
     */
    @NonNull
    public IADAPTER setOnItemClickListener(OnItemClickListener<DATATYPE> listener) {
        mClickListener = listener;
        return getInstance();
    }

    /**
     * 设置列表项长按监听
     *
     * @param listener
     * @return
     */
    @NonNull
    public IADAPTER setOnItemLongClickListener(OnItemLongClickListener<DATATYPE> listener) {
        mLongClickListener = listener;
        return getInstance();
    }


    @Override
    public int getDataCount() {
        return getDataList().size();
    }

    @Override
    public List<DATATYPE> getDataList() {
        return mData;
    }

    @Override
    public int getItemCount() {
        return getDataCount();
    }

    @NonNull
    @Override
    public INotifier getNotifier() {
        return this;
    }
}
