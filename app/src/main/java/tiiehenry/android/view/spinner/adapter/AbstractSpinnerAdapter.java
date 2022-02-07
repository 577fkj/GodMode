package tiiehenry.android.view.spinner.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import tiiehenry.android.view.spinner.holder.SpinnerViewHolder;

/**
 * @author TIIEHenry
 */
public abstract class AbstractSpinnerAdapter<IADAPTER extends AbstractSpinnerAdapter
        , DATATYPE> extends BaseAdapter
        implements ISpinnerAdapter<IADAPTER, DATATYPE, SpinnerViewHolder> {
    private List<DATATYPE> mData = new ArrayList<>();

    public AbstractSpinnerAdapter() {
        super();
    }

    public AbstractSpinnerAdapter(@NonNull Collection<DATATYPE> list) {
        this();
        mData.addAll(list);
    }

    public AbstractSpinnerAdapter(@NonNull DATATYPE[] data) {
        this();
        if (data.length > 0) {
            mData.addAll(Arrays.asList(data));
        }
    }

    @NonNull
    public abstract SpinnerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position);

    @NonNull
    public abstract SpinnerViewHolder onCreateDropDownViewHolder(@NonNull ViewGroup parent, int position);

    public abstract void bindData(@NonNull SpinnerViewHolder holder, @NonNull DATATYPE item, int position);

    public abstract void bindDropDownData(@NonNull SpinnerViewHolder holder, @NonNull DATATYPE item, int position);

    @Override
    public int getCount() {
        return getDataCount();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpinnerViewHolder holder = onCreateViewHolder(parent, position);
        DATATYPE data = getData(position);
//        holder.setData(data);
        bindData(holder, data, position);
        holder.getItemView().setTag(holder);
        return holder.getItemView();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        SpinnerViewHolder holder = onCreateDropDownViewHolder(parent, position);
        DATATYPE data = getData(position);
//        holder.setData(data);
        bindDropDownData(holder, data, position);
        holder.getItemView().setTag(holder);
        return holder.getItemView();
    }

    @Override
    public List<DATATYPE> getDataList() {
        return mData;
    }

    @Override
    public Object getItem(int position) {
        return getData(position);
    }

    @Override
    public DATATYPE getData(int position) {
        return getDataList().get(position);
    }
}

