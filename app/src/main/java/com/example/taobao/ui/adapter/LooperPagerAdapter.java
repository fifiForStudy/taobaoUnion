package com.example.taobao.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.taobao.model.domain.HomePagerContent;
import com.example.taobao.model.domain.IBaseInfo;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

public class LooperPagerAdapter extends PagerAdapter {
    private List<HomePagerContent.DataDTO> mData = new ArrayList<>();
    private OnLooperPagerItemClickListener mItemClickListener = null;

    //暴露接口
    public int getDataSize(){
        return mData.size();
    }
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        //处理越界问题
        int realPosition = position % mData.size();
        //获得图片url
        HomePagerContent.DataDTO dataDTO = mData.get(realPosition);
        int measuredHeight = container.getMeasuredHeight();
        int measuredWidth = container.getMeasuredWidth();
//        LogUtils.d(this,"measuredHeight *****> "+measuredHeight);
//        LogUtils.d(this,"measuredWidth *****> "+measuredWidth);
        int ivSize = (measuredWidth > measuredHeight ? measuredWidth : measuredHeight) / 2;

        String coverUrl = UrlUtils.getCoverPath(dataDTO.getPict_url(),ivSize);

        ImageView iv = new ImageView(container.getContext());



        //设置宽高
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        iv.setLayoutParams(layoutParams);

        //设置拉伸形式
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Glide.with(container.getContext()).load(coverUrl).into(iv);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    HomePagerContent.DataDTO item = mData.get(realPosition);
                    mItemClickListener.onLooperItemClick(item);
                }
            }
        });


        container.addView(iv);
        return iv;
    }
    public interface OnLooperPagerItemClickListener{
        void onLooperItemClick(IBaseInfo item);
    }

    public void setOnLooperPagerItemClickListener(OnLooperPagerItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    public void setData(List<HomePagerContent.DataDTO> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }
}
