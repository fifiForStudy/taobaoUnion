package com.example.taobao.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.taobao.model.domain.Categories;
import com.example.taobao.ui.fragment.HomePagerFragment;
import com.example.taobao.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private List<Categories.DataDTO> categoryList = new ArrayList<>();

    public HomePagerAdapter(@NonNull FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categoryList.get(position).getTitle();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        LogUtils.d(this,"getItem ---> "+position);
        Categories.DataDTO dataDTO = categoryList.get(position);
        HomePagerFragment homePagerFragment = HomePagerFragment.newInstance(dataDTO);

        return homePagerFragment;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    public void setCategories(Categories categories) {
        categoryList.clear();
        List<Categories.DataDTO> data = categories.getData();
        this.categoryList.addAll(data);
        notifyDataSetChanged();
    }
}
