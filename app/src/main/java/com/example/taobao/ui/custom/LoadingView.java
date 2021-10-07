package com.example.taobao.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.taobao.R;

public class LoadingView extends AppCompatImageView {
    private float mDegrees = 30;
    private boolean mNeedRotate = true;


    public LoadingView(@NonNull Context context) {
        this(context,null);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        startRotate();

    }

    private void startRotate() {

        post(new Runnable() {
            @Override
            public void run() {
                mDegrees += 30;
                if (mDegrees>360) {
                    mDegrees = 0;
                }
                invalidate();
                //判断是否需要继续旋转
                //如果不可见或者已经onDetachedFromWindow就不再转动
                if(getVisibility()!=VISIBLE && !mNeedRotate){
                    removeCallbacks(this);
                }else{
                    postDelayed(this, 200);
                }
//                postDelayed(this, 20);

            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }

    private void stopRotate() {
        mNeedRotate = false;
    }

    //让图片转起来
    @Override
    protected void onDraw(Canvas canvas) {//绘制的方法
        canvas.rotate(mDegrees,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
