package tiiehenry.android.fragment.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BaseFragmentAdapter<IADAPTER extends BaseFragmentAdapter
        , DATATYPE> extends FragmentPagerAdapter implements IFragmentAdapter<IADAPTER, DATATYPE> {

    private final FragmentManager fragmentManager;
    private List<DATATYPE> dataList = new ArrayList<>();


    public BaseFragmentAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentManager = fm;
    }

    public BaseFragmentAdapter(FragmentManager fm, List<DATATYPE> items) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentManager = fm;
        dataList.addAll(items);
    }

    public BaseFragmentAdapter(FragmentManager fm, DATATYPE[] items) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragmentManager = fm;
        dataList.addAll(Arrays.asList(items));
    }

    @Override
    public List<DATATYPE> getDataList() {
        return dataList;
    }

    public int getPosition(@NonNull Fragment item) {
        for (int i = 0; i < dataList.size(); i++) {
            if (getItem(i) == item)
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

//
//
//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        if (!((Fragment) object).isAdded() || !contains((Fragment) object)) {
//            return POSITION_NONE;
//        }
//        return getPosition((Fragment)object);
//    }
//
//    @NonNull
//    @Override
//    public Object instantiateItem(@NonNull ViewGroup container, int position) {
//        Fragment instantiateItem = ((Fragment) super.instantiateItem(container, position));
//        Fragment item = getItem(position);
//        if (instantiateItem == item) {
//            return instantiateItem;
//        } else {
//            //如果集合中对应下标的fragment和fragmentManager中的对应下标的fragment对象不一致，那么就是新添加的，所以自己add进入；这里为什么不直接调用super方法呢，因为fragment的mIndex搞的鬼，以后有机会再补一补。
//            fragmentManager.beginTransaction().add(container.getId(), item).commitNowAllowingStateLoss();
//            return item;
//        }
//    }
//
//    @Override
//    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
//        Fragment fragment = (Fragment) object;
//        //如果getItemPosition中的值为PagerAdapter.POSITION_NONE，就执行该方法。
//        if (contains(fragment)) {
//            super.destroyItem(container, position, fragment);
//            return;
//        }
//        //自己执行移除。因为mFragments在删除的时候就把某个fragment对象移除了，所以一般都得自己移除在fragmentManager中的该对象。
//        fragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss();
//
//    }

}
