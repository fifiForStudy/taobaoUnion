package com.example.taobao.ui.fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobao.R;
import com.example.taobao.base.BaseFragment;
import com.example.taobao.model.domain.IBaseInfo;
import com.example.taobao.model.domain.SelectedContent;
import com.example.taobao.model.domain.SelectedPageCategory;
import com.example.taobao.presenter.ISelectedPagePresenter;
import com.example.taobao.presenter.ITicketPresenter;
import com.example.taobao.ui.activity.TicketActivity;
import com.example.taobao.ui.adapter.SelectedPageContentAdapter;
import com.example.taobao.ui.adapter.SelectedPageLeftAdapter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.PresenterManager;
import com.example.taobao.utils.SizeUtils;
import com.example.taobao.utils.TicketUtil;
import com.example.taobao.view.ISelectedPageCallback;

import butterknife.BindView;

public class SelectedFragment extends BaseFragment implements ISelectedPageCallback, SelectedPageLeftAdapter.OnLeftItemClickListener, SelectedPageContentAdapter.OnSelectedPageContentItemClickListener {

    @BindView(R.id.left_category_list)
    public RecyclerView leftCategoryList;

    @BindView(R.id.right_content_list)
    public RecyclerView rightContentList;

    @BindView(R.id.fragment_bar_title_tv)
    public TextView barTitleTv;

    private ISelectedPagePresenter mSelectedPagePresenter;
    private SelectedPageLeftAdapter mLeftAdapter;
    private SelectedPageContentAdapter mRightAdapter;
    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_bar_layout, container, false);
    }

    @Override
    protected void initPresenter() {
        super.initPresenter();
        mSelectedPagePresenter = PresenterManager.getInstance().getSelectedPagePresenter();
        mSelectedPagePresenter.registerViewCallback(this);
        //??????category?????????
        mSelectedPagePresenter.getCategories();
    }

    @Override
    protected void release() {
        super.release();
        if (mSelectedPagePresenter != null) {
            mSelectedPagePresenter.unregisterViewCallback(this);


        }
    }

    @Override
    protected int getRootViewResId() {

        return R.layout.fragment_selected;
    }

    @Override
    protected void initView(View rootView) {
        setUpState(State.SUCCESS);
        //TODO:>>>>>>>>>>>>>>>>>>>>>>>>>
        barTitleTv.setText("????????????");
        //?????????
        leftCategoryList.setLayoutManager(new LinearLayoutManager(getContext()));
        mLeftAdapter = new SelectedPageLeftAdapter();
        leftCategoryList.setAdapter(mLeftAdapter);

        //?????????
        rightContentList.setLayoutManager(new LinearLayoutManager(getContext()));
//        LogUtils.d(this, "rightContentList initView ******");
        mRightAdapter = new SelectedPageContentAdapter();
        rightContentList.setAdapter(mRightAdapter);
        //????????????
        rightContentList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndBottom = SizeUtils.dip2px(getContext(), 4);
                int leftAndRight = SizeUtils.dip2px(getContext(), 6);
                outRect.top = topAndBottom;
                outRect.bottom = topAndBottom;
                outRect.left = leftAndRight;
                outRect.right = leftAndRight;
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        mLeftAdapter.setOnLeftItemClickListener(this);
        mRightAdapter.setOnSelectedPageContentItemClickListener(this);
    }

    @Override
    public void onCategoriesLoaded(SelectedPageCategory categories) {
        setUpState(State.SUCCESS);
        //?????????????????????????????????
        mLeftAdapter.setData(categories);
        //????????????
        LogUtils.d(this, "onCategoriesLoaded -----> " + categories.toString());
        //TODO:??????UI
        //??????????????????????????????????????????????????????
//        List<SelectedPageCategory.DataDTO> data = categories.getData();
//        mSelectedPagePresenter.getContentByCategory(data.get(0));
    }


    @Override
    public void onContentLoaded(SelectedContent content) {
//        LogUtils.d(this, "onContentLoaded -----> " + content.toString());
        //?????????????????????????????????
        mRightAdapter.setData(content);
        //????????????
//        LogUtils.d(this, "onCategoriesLoaded -----> " + categories.toString());
        rightContentList.scrollToPosition(0);
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

    }

    @Override
    protected void onRetryClick() {
        //??????
        if (mSelectedPagePresenter != null) {
            mSelectedPagePresenter.reloadContent();
        }
    }

    @Override
    public void onLeftItemClick(SelectedPageCategory.DataDTO item) {
        //????????????????????????
        mSelectedPagePresenter.getContentByCategory(item);
        LogUtils.d(this, "current selected item ---> " + item.getFavorites_title());
    }

    @Override
    public void onContentItemClick(IBaseInfo item) {
//        //content??????????????????
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

        TicketUtil.toTicketPage(getContext(),item);
    }
}
