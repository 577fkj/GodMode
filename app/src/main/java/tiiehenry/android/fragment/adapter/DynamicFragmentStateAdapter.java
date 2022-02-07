package tiiehenry.android.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 动态删改，不重建
 *
 * @param <DATATYPE>
 */

public abstract class DynamicFragmentStateAdapter<IADAPTER extends DynamicFragmentStateAdapter
        , DATATYPE> extends FragmentNoStatePagerAdapter implements IFragmentAdapter<IADAPTER, DATATYPE> {

    private List<DATATYPE> dataList = new ArrayList<>();


    public DynamicFragmentStateAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public List<DATATYPE> getDataList() {
        return dataList;
    }


    public DynamicFragmentStateAdapter(@NonNull FragmentManager fm, @NonNull List<DATATYPE> items) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        dataList.addAll(items);
    }

    public DynamicFragmentStateAdapter(@NonNull FragmentManager fm, @NonNull DATATYPE[] items) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        dataList.addAll(Arrays.asList(items));
    }

    public boolean contains(Fragment item) {
        for (int i = 0; i < dataList.size(); i++) {
            if (getData(i) == item)
                return true;
        }
        return false;
    }

    public int getPosition(@NonNull Fragment item) {
        for (int i = 0; i < dataList.size(); i++) {
            if (getData(i) == item)
                return i;
        }
        return -1;
    }

    @Override
    public int getCount() {
        return getDataCount();
    }

    @NonNull
    abstract public Fragment getItem(int position);

    @NonNull
    abstract public CharSequence getPageTitle(int position);

    @Override
    public int getItemPosition(@NonNull Object object) {
        if (dataList != null && dataList.size() == 0) {
            return POSITION_NONE;
        }
        return POSITION_NONE;
    }


    //    @Override
//    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
//        return view==object;
//    }
}
