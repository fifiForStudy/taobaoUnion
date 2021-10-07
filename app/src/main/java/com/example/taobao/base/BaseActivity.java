package com.example.taobao.base;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder mBind;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        //==================================================================================//
        //设置清明灰
        /*ColorMatrix cm = new ColorMatrix();//颜色矩阵
        cm.setSaturation(0);//设置饱和度,默认值为1
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        View contentContainer = getWindow().getDecorView();
        contentContainer.setLayerType(View.LAYER_TYPE_SOFTWARE,paint);//软件层*/
        //==================================================================================//

        mBind = ButterKnife.bind(this);
        initView();
        initEvent();
        initPresenter();
    }

    protected abstract void initPresenter();

    protected void initEvent() {
    }

    protected abstract void initView();

    protected abstract int getLayoutId() ;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBind != null) {
            mBind.unbind();

        }
        this.release();
    }
    /*子类需要释放资源，复写即可*/
    protected void release() {

    }
}
