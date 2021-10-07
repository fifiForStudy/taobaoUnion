package com.example.taobao.ui.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.taobao.R;
import com.example.taobao.utils.LogUtils;
import com.lcodecore.tkrefreshlayout.utils.LogUtil;

/*功能：自动轮播*/
public class AutoLooperViewPager extends ViewPager {
    //切换间隔时长，单位毫秒
    public static final int DEFAULT_DURATION = 3000;

    private long mDuration = DEFAULT_DURATION;

    /*设置切换时长，单位毫秒*/
    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public AutoLooperViewPager(@NonNull Context context) {
        super(context,null);
    }


    public AutoLooperViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //读取属性
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.AutoLooperStyle);
        //获取属性
        mDuration = t.getInteger(R.styleable.AutoLooperStyle_duration, (int) DEFAULT_DURATION);
        LogUtils.d(this, "mDuration +++++++>"+mDuration);
        //回收
        t.recycle();
    }

    private boolean isLoop = false;

    public void startLoop() {
        isLoop = true;
        post(mTask);

    }
    private Runnable mTask=new Runnable() {
        @Override
        public void run() {
            //先拿到当前的位置
            int currentItem = getCurrentItem();
            currentItem++;
            setCurrentItem(currentItem);
            if (isLoop) {
                postDelayed(this, mDuration);
            }
        }
    };
    public void stopLoop() {
        isLoop = false;
        removeCallbacks(mTask);
    }
}
