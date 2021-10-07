package com.example.taobao.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.taobao.R;
import com.example.taobao.base.BaseFragment;
import com.example.taobao.model.domain.Categories;
import com.example.taobao.presenter.IHomePresenter;
import com.example.taobao.presenter.impl.HomePresenterImpl;
import com.example.taobao.ui.activity.IMainActivity;
import com.example.taobao.ui.activity.MainActivity;
import com.example.taobao.ui.activity.ScanQrCodeActivity;
import com.example.taobao.ui.adapter.HomePagerAdapter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.PresenterManager;
import com.example.taobao.view.IHomeCallback;
import com.google.android.material.tabs.TabLayout;
import com.vondear.rxfeature.activity.ActivityScanerCode;

import butterknife.BindView;

public class HomeFragment extends BaseFragment implements IHomeCallback {

    @BindView(R.id.home_indicator)
    public TabLayout mTabLayout;

    @BindView(R.id.home_pager)
    public ViewPager homePager;

    @BindView(R.id.home_search_input_box)
    public View mSearchInputBox;

    @BindView(R.id.scan_icon)
    public View scanBtn;


    private IHomePresenter mHomePresenter;
    private HomePagerAdapter mHomePagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtils.d(this, "onCreateView==========");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        LogUtils.d(this, "onDestroyView==========");
        super.onDestroyView();
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.base_home_fragment_layout, container, false);
    }

    @Override
    protected void initView(View rootView) {
        mTabLayout.setupWithViewPager(homePager);
        //给viewPager设置适配器
        mHomePagerAdapter = new HomePagerAdapter(getChildFragmentManager());
        //设置适配器
        homePager.setAdapter(mHomePagerAdapter);
    }

    @Override
    protected void initPresenter() {
        //创建presenter
        mHomePresenter = PresenterManager.getInstance().getHomePresenter();
        mHomePresenter.registerViewCallback(this);
        mSearchInputBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索界面
                FragmentActivity activity = getActivity();
                if (activity instanceof IMainActivity) {
                    ((MainActivity) activity).switch2Search();
                }

            }
        });
    }

    @Override
    protected void initListener() {
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到扫码界面
                startActivity(new Intent(getContext(), ScanQrCodeActivity.class));

            }
        });
    }

    @Override
    protected void loadData() {

        //加载数据
        mHomePresenter.getCategories();
    }

    @Override
    public void onCategoriesLoaded(Categories categories) {

        setUpState(State.SUCCESS);
        LogUtils.d(this, "onCategoriesLoaded......");
        //加载的数据从这里回来
        //判空，避免UI销毁了，数据还没有回来的情况
        if (mHomePagerAdapter != null) {

            //全部加载
            //homePager.setOffscreenPageLimit(categories.getData().size());

            mHomePagerAdapter.setCategories(categories);
        }
    }

    @Override
    public void onError() {
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    protected void release() {
        //取消回调注册
        if (mHomePresenter != null) {
            mHomePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void onRetryClick() {
        //重新加载分类内容
        if (mHomePresenter != null) {
            mHomePresenter.getCategories();
        }
    }
}
