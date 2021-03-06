package com.example.taobao.ui.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.taobao.R;
import com.example.taobao.base.BaseFragment;
import com.example.taobao.model.domain.Categories;
import com.example.taobao.model.domain.HomePagerContent;
import com.example.taobao.model.domain.IBaseInfo;
import com.example.taobao.presenter.ICategoryPagerPresenter;
import com.example.taobao.ui.adapter.LinearItemContentAdapter;
import com.example.taobao.ui.adapter.LooperPagerAdapter;
import com.example.taobao.ui.custom.AutoLooperViewPager;
import com.example.taobao.utils.Constants;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.PresenterManager;
import com.example.taobao.utils.SizeUtils;
import com.example.taobao.utils.TicketUtil;
import com.example.taobao.utils.ToastUtil;
import com.example.taobao.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.views.TbNestedScrollView;

import java.util.List;

import butterknife.BindView;

public class HomePagerFragment extends BaseFragment implements ICategoryPagerCallback, LinearItemContentAdapter.OnListItemClickListener, LooperPagerAdapter.OnLooperPagerItemClickListener {

    private ICategoryPagerPresenter mCategoryPagePresenter;
    private int mMaterialId;

    @BindView(R.id.home_pager_content_list)
    public RecyclerView mContentList;

    @BindView(R.id.looper_pager)
    public AutoLooperViewPager looperPager;

    @BindView(R.id.home_pager_title)
    public TextView currentCategoryTitleTv;

    @BindView(R.id.looper_point_container)
    public LinearLayout looperPointContainer;

    @BindView(R.id.home_pager_parent)
    public LinearLayout homePagerParent;

    @BindView(R.id.home_pager_header_container)
    public LinearLayout homeHeaderContainer;

    @BindView(R.id.home_pager_refresh)
    public TwinklingRefreshLayout twinklingRefreshLayout;

    @BindView(R.id.home_pager_nested_scroll)
    public TbNestedScrollView homePagerNestedView;

    private LinearItemContentAdapter mContentAdapter;
    private LooperPagerAdapter mLooperPagerAdapter;

    public static HomePagerFragment newInstance(Categories.DataDTO category) {
        HomePagerFragment homePagerFragment = new HomePagerFragment();
        //
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_HOME_PAGER_TITLE, category.getTitle());
        bundle.putInt(Constants.KEY_HOME_PAGER_MATERIAL_ID, category.getId());
        homePagerFragment.setArguments(bundle);

        return homePagerFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        //????????????????????????????????????
        looperPager.startLoop();
    }

    @Override
    public void onPause() {
        super.onPause();
        //???????????????????????????????????????
        looperPager.stopLoop();
    }

    @Override
    protected void initListener() {
        mContentAdapter.setOnListItemClickListener(this);
        mLooperPagerAdapter.setOnLooperPagerItemClickListener(this);

        twinklingRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                LogUtils.d(HomePagerFragment.this, "?????????Load More......");
                //????????????????????????
                if (mCategoryPagePresenter != null) {
                    mCategoryPagePresenter.loadMore(mMaterialId);
                }
            }
        });
        homePagerParent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (homeHeaderContainer == null) {
                    return;
                }
                int headerHeight = homeHeaderContainer.getMeasuredHeight();
                //LogUtils.d(HomePagerFragment.this, "headerHeight==> " + headerHeight);
                homePagerNestedView.setHeaderHeigth(headerHeight);

                int measuredHeight = homePagerParent.getMeasuredHeight();

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContentList.getLayoutParams();
                layoutParams.height = measuredHeight;
                mContentList.setLayoutParams(layoutParams);
                if (measuredHeight != 0) {
                    homePagerParent.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            }
        });

        looperPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //??????
                //???????????????????????????
            }

            @Override
            public void onPageSelected(int position) {
                //????????????????????????
                //????????????????????????
                if (mLooperPagerAdapter.getDataSize() == 0) {
                    return;
                }
                int targetPosition = position % mLooperPagerAdapter.getDataSize();
                //???????????????
                updateLooperIndicator(targetPosition);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //???????????????????????????
            }
        });
    }

    //???????????????
    private void updateLooperIndicator(int targetPosition) {
        for (int i = 0; i < looperPointContainer.getChildCount(); i++) {
            View point = looperPointContainer.getChildAt(i);

            if (i == targetPosition) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
        }
    }

    @Override
    protected int getRootViewResId() {

        return R.layout.fragment_home_pager;
    }

    @Override
    protected void initView(View rootView) {
//        setUpState(State.SUCCESS);
        //?????????????????????
        mContentList.setLayoutManager(new LinearLayoutManager(getContext()));
        //????????????item???????????????
        mContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndButtom = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.top = topAndButtom;
                outRect.bottom = topAndButtom;
            }
        });
        //???????????????
        mContentAdapter = new LinearItemContentAdapter();
        //???????????????
        mContentList.setAdapter(mContentAdapter);

        //???????????????????????????
        mLooperPagerAdapter = new LooperPagerAdapter();
        //???????????????????????????
        looperPager.setAdapter(mLooperPagerAdapter);
        //??????refresh????????????
        twinklingRefreshLayout.setEnableRefresh(false);//??????????????????????????????
        twinklingRefreshLayout.setEnableLoadmore(true);//??????????????????????????????
    }

    @Override
    protected void initPresenter() {
        mCategoryPagePresenter = PresenterManager.getInstance().getCategoryPagePresenter();
        mCategoryPagePresenter.registerViewCallback(this);
    }

    @Override
    protected void loadData() {
        Bundle arguments = getArguments();
        String title = arguments.getString(Constants.KEY_HOME_PAGER_TITLE);
        mMaterialId = arguments.getInt(Constants.KEY_HOME_PAGER_MATERIAL_ID);
        //????????????
//        LogUtils.d(this, "title ----> " + title);
//        LogUtils.d(this, "MaterialId ----> " + mMaterialId);
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.getContentByCategoryId(mMaterialId);
        }
        if (currentCategoryTitleTv != null) {
            currentCategoryTitleTv.setText(title);
        }

    }

    @Override
    public int getCategoryId() {
        return mMaterialId;
    }

    @Override
    public void onContentLoaded(List<HomePagerContent.DataDTO> contents) {
        //????????????????????????
        //TODO?????????UI
        mContentAdapter.setData(contents);
        setUpState(State.SUCCESS);
    }

    @Override
    public void onLoading() {
        setUpState(State.LOADING);
    }

    @Override
    public void onError() {
        //????????????
        setUpState(State.ERROR);
    }

    @Override
    public void onEmpty() {
        setUpState(State.EMPTY);
    }

    @Override
    public void onLoaderMoreError() {
        ToastUtil.showToast("??????????????????????????????");

        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreEmpty() {
        ToastUtil.showToast("??????????????????");

        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }
    }

    @Override
    public void onLoaderMoreLoaded(List<HomePagerContent.DataDTO> contents) {
        //?????????????????????????????????
        mContentAdapter.addData(contents);

        if (twinklingRefreshLayout != null) {
            twinklingRefreshLayout.finishLoadmore();
        }

        ToastUtil.showToast("?????????" + contents.size() + "?????????");
    }

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onLooperListLoaded(List<HomePagerContent.DataDTO> contents) {
        LogUtils.d(this, "looper size =======> " + contents.size());
        mLooperPagerAdapter.setData(contents);

        /*????????????????????????????????????*/
        //?????????%?????????size????????????0????????????????????????????????????
        //????????????
        int dx = (Integer.MAX_VALUE / 2) % contents.size();
        int targetPosition = (Integer.MAX_VALUE / 2) - dx;
        //??????????????????
        looperPager.setCurrentItem(targetPosition);
        //LogUtils.d(this, "url *****> " + contents.get(0).getPict_url());

        //?????????????????????????????????
        //???UI?????????????????????view?????????

        looperPointContainer.removeAllViews();
        //?????????
        for (int i = 0; i < contents.size(); i++) {
            View point = new View(getContext());
            //????????????
            int size = SizeUtils.dip2px(getContext(), 8);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(size, size);
            //?????????????????????
            layoutParams.leftMargin = SizeUtils.dip2px(getContext(), 5);
            layoutParams.rightMargin = SizeUtils.dip2px(getContext(), 5);

            point.setLayoutParams(layoutParams);
            if (i == 0) {
                point.setBackgroundResource(R.drawable.shape_indicator_point_selected);
            } else {
                point.setBackgroundResource(R.drawable.shape_indicator_point_normal);
            }
//
            looperPointContainer.addView(point);
        }

    }


    @Override
    protected void release() {
        if (mCategoryPagePresenter != null) {
            mCategoryPagePresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(IBaseInfo item) {
        //????????????????????????
        LogUtils.d(this, "list item click ------>" + item.getTitle());
        handleItemClick(item);
    }

    private void handleItemClick(IBaseInfo item) {
//        //????????????
//        String title = item.getTitle();
//        //???????????????
//        //String url = item.getClick_url();
//        //???????????????
//        String url = item.getCoupon_click_url();
//        if (TextUtils.isEmpty(url)) {
//            url = item.getClick_url();
//        }
//
//        String cover = item.getPict_url();
//        //??????Ticketpresenter???????????????
//        ITicketPresenter ticketPresenter = PresenterManager.getInstance().getTicketPresenter();
//        ticketPresenter.getTicket(title, url, cover);
//
//        startActivity(new Intent(getContext(), TicketActivity.class));

        //TODO:????????????????????????????????????
        TicketUtil.toTicketPage(getContext(),item);
    }

    @Override
    public void onLooperItemClick(IBaseInfo item) {
        //???????????????????????????
        LogUtils.d(this,"looper item click ------>"+item.getTitle());
        handleItemClick(item);
    }
}
