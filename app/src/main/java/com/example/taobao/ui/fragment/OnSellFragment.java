package com.example.taobao.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobao.R;
import com.example.taobao.base.BaseFragment;
import com.example.taobao.model.domain.IBaseInfo;
import com.example.taobao.model.domain.OnSellContent;
import com.example.taobao.presenter.IOnSellPagePresenter;
import com.example.taobao.presenter.ITicketPresenter;
import com.example.taobao.ui.activity.TicketActivity;
import com.example.taobao.ui.adapter.OnSellContentAdapter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.PresenterManager;
import com.example.taobao.utils.SizeUtils;
import com.example.taobao.utils.TicketUtil;
import com.example.taobao.utils.ToastUtil;
import com.example.taobao.view.IOnSellPageCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import butterknife.BindView;

public class OnSellFragment extends BaseFragment implements IOnSellPageCallback, OnSellContentAdapter.OnSellPageItemClickListener {

    private IOnSellPagePresenter mOnSellPagePresenter;
    public static final int DEFAULT_SPAN_COUNT = 2;

    @BindView(R.id.on_sell_content_list)
    public RecyclerView mContentRv;

    @BindView(R.id.on_sell_refresh_layout)
    public TwinklingRefreshLayout mTwinklingRefreshLayout;

    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;

    private OnSellContentAdapter mOnSellContentAdapter;


    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container, false);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mOnSellPagePresenter = PresenterManager.getInstance().getOnSellPagePresenter();
        mOnSellPagePresenter.registerViewCallback(this);
        //拿内容
        mOnSellPagePresenter.getOnSellContent();
    }

    @Override
    protected void release() {
        super.release();
        if (mOnSellPagePresenter != null) {
            mOnSellPagePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_on_sell;
    }

    @Override
    protected void initView(View rootView) {
        //TODO:>>>>>>>>>>>>>>>>>>>>>>>>>
        barTitleTv.setText("特惠宝贝");
        LogUtils.d(this,"initView.....");
        //创建适配器
        mOnSellContentAdapter = new OnSellContentAdapter();
        //设置布局管理器
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), DEFAULT_SPAN_COUNT);
        mContentRv.setLayoutManager(gridLayoutManager);
        //设置适配器
        mContentRv.setAdapter(mOnSellContentAdapter);
        //设置间距
        mContentRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndButtom = SizeUtils.dip2px(getContext(), 2.5f);
                outRect.top = topAndButtom;
                outRect.bottom = topAndButtom;
                outRect.left = topAndButtom;
                outRect.right = topAndButtom;
            }
        });

        //设置相关属性
        mTwinklingRefreshLayout.setEnableLoadmore(true);
        mTwinklingRefreshLayout.setEnableRefresh(false);
        //允许开启越界回弹模式
        mTwinklingRefreshLayout.setEnableOverScroll(true);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTwinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多的内容
                if (mOnSellPagePresenter != null) {
                    mOnSellPagePresenter.loaderMore();
                }
            }
        });

        mOnSellContentAdapter.setOnSellPageItemClickListener(this);
    }

    @Override
    public void onContentLoadedSuccess(OnSellContent result) {
        LogUtils.d(this,"onContentLoadedSuccess.....");
        setUpState(State.SUCCESS);
        //数据回来了
        //更新UI,给适配器设置数据
        mOnSellContentAdapter.setData(result);
    }

    @Override
    public void onMoreLoaded(OnSellContent moreResult) {
        //结束刷新
        mTwinklingRefreshLayout.finishLoadmore();
        int size = moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        ToastUtil.showToast("加载了"+size+"个宝贝");
        //添加内容到列表后面
        mOnSellContentAdapter.onMoreLoaded(moreResult);

    }

    @Override
    public void onMoreLoadedError() {
        //结束刷新
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtil.showToast("网络异常，请稍后重试...");
    }

    @Override
    public void onMoreLoadedEmpty() {
        //结束刷新
        mTwinklingRefreshLayout.finishLoadmore();
        ToastUtil.showToast("没有更多内容了...");
    }

    @Override
    public void onError() {
        LogUtils.d(this,"onError......");
        setUpState(State.ERROR);
    }

    @Override
    public void onLoading() {
        LogUtils.d(this,"onLoading......");
        setUpState(State.LOADING);
    }

    @Override
    public void onEmpty() {
        LogUtils.d(this,"onEmpty......");
        setUpState(State.EMPTY);
    }

    @Override
    public void onSellItemClick(IBaseInfo item) {
        TicketUtil.toTicketPage(getContext(), item);
//        //特惠列表内容被点击了
//        //处理数据
//        String title = item.getTitle();
//        //详情的地址
//        //String url = item.getClick_url();
//        //领券的地址
//        String url = item.getCoupon_click_url();
//        if (TextUtils.isEmpty(url)) {
//            url = item.getClick_url();
//        }
//
//        String cover = item.getPict_url();
//        //拿到Ticketpresenter去加载数据
//        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
//        ticketPresenter.getTicket(title, url, cover);
//
//        startActivity(new Intent(getContext(), TicketActivity.class));
    }
}
