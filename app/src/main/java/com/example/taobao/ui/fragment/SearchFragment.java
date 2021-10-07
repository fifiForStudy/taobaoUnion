package com.example.taobao.ui.fragment;

import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobao.R;
import com.example.taobao.base.BaseFragment;
import com.example.taobao.model.domain.History;
import com.example.taobao.model.domain.IBaseInfo;
import com.example.taobao.model.domain.SearchRecommend;
import com.example.taobao.model.domain.SearchResult;
import com.example.taobao.presenter.ISearchPagePresenter;
import com.example.taobao.ui.adapter.LinearItemContentAdapter;
import com.example.taobao.ui.custom.TextFlowLayout;
import com.example.taobao.utils.KeyboardUtil;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.PresenterManager;
import com.example.taobao.utils.SizeUtils;
import com.example.taobao.utils.TicketUtil;
import com.example.taobao.utils.ToastUtil;
import com.example.taobao.view.ISearchCallback;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchFragment extends BaseFragment implements ISearchCallback, TextFlowLayout.OnFlowTextItemClickListener {

    @BindView(R.id.search_recommend_view)
    public TextFlowLayout mRecommendView;

    @BindView(R.id.search_history_view)
    public TextFlowLayout mHistoriesView;

    @BindView(R.id.search_recommend_container)
    public View mRecommendContainer;

    @BindView(R.id.search_history_container)
    public View mHistoriesContainer;

    @BindView(R.id.search_history_delete)
    public View mHistoriesDelete;

    @BindView(R.id.search_result_list)
    public RecyclerView mSearchList;

    @BindView(R.id.search_result_container)
    public TwinklingRefreshLayout mRefreshContainer;

    @BindView(R.id.search_btn)
    public TextView mSearchBtn;

    @BindView(R.id.search_clean_btn)
    public ImageView mCleanInputBtn;

    @BindView(R.id.search_input_box)
    public EditText mSearchInputBox;


    private ISearchPagePresenter mSearchPagePresenter;
    private LinearItemContentAdapter mSearchResultAdapter;

    @Override
    protected void initPresenter() {
        mSearchPagePresenter = PresenterManager.getInstance().getSearchPagePresenter();
        mSearchPagePresenter.registerViewCallback(this);
        //获取搜索推荐词
        mSearchPagePresenter.getRecommendWords();

        //mSearchPagePresenter.doSearch("iPhone");

        mSearchPagePresenter.getHistories();
    }
    //销毁的时候释放资源
    @Override
    protected void release() {
        if (mSearchPagePresenter != null) {
            mSearchPagePresenter.unregisterViewCallback(this);
        }
    }
    //加载顶部搜索框
    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }
    //绑定界面
    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initView(View rootView) {
        //设置布局管理器
        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置适配器
        mSearchResultAdapter = new LinearItemContentAdapter();
        mSearchList.setAdapter(mSearchResultAdapter);
        //设置搜索到的list之间的间距
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndButtom = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.top = topAndButtom;
                outRect.bottom = topAndButtom;
            }
        });
        //设置刷新控件
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableOverScroll(true);
    }

    @Override
    protected void initListener() {
        /*点击历史或者推荐的词之后，跳转到搜索*/
        mHistoriesView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);
        /*点击删除历史记录的图标*/
        mHistoriesDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除历史
                mSearchPagePresenter.delHistories();
            }
        });
        /*上拉，recycleView加载更多的内容*/
        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //去加载更多的内容
                if (mSearchPagePresenter != null) {
                    mSearchPagePresenter.loaderMore();
                }
            }
        });
        /*点击列表里的商品，跳转到淘口令界面*/
        mSearchResultAdapter.setOnListItemClickListener(new LinearItemContentAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(IBaseInfo item) {
                //搜索列表的内容被点击了
                TicketUtil.toTicketPage(getContext(), item);
            }
        });
        /*在搜索框里输入内容，按回车进行查找*/
        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPagePresenter != null) {
                    //判断拿到的内容是否为空
                    String keyword = v.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {
                        return false;
                    }
                    //发起搜索
                    //mSearchPagePresenter.doSearch(keyword);
                    toSearch(keyword);
                }
                return false;
            }
        });
        /*监听输入框的变化*/
        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //变化的时候通知、
                //如果长度不为0，那么显示删除按钮
                //否则隐藏删除按钮
                mCleanInputBtn.setVisibility(hasInput(true) ? View.VISIBLE : View.GONE);
                //输入框有内容，右边的文字就从”取消“-->”搜索“
                mSearchBtn.setText(hasInput(false) ? "搜索" : "取消");
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*清除输入框里面的内容*/
        mCleanInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                //点击清除按钮之后，回到历史记录界面
                switch2HistoryPager();
            }
        });
        /*发起搜索*/
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasInput(false)) {
                    //如果有内容，就搜索
                    if (mSearchPagePresenter!=null) {
                        //mSearchPagePresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        toSearch(mSearchInputBox.getText().toString().trim());
                        KeyboardUtil.hide(getContext(),v);
                    }
                } else {
                    //如果没有内容，就取消,点击之后会隐藏键盘
                    KeyboardUtil.hide(getContext(),v);


                }
            }
        });
    }

    /*切换到历史和推荐界面*/
    private void switch2HistoryPager() {
        //回到界面就获取一次历史记录
        if (mSearchPagePresenter != null) {
            mSearchPagePresenter.getHistories();
        }
//        if (mHistoriesView.getContentSize()!=0) {
//            mHistoriesContainer.setVisibility(View.VISIBLE);
//        }else{
//            mHistoriesContainer.setVisibility(View.GONE);
//
//        }
        if (mRecommendView.getContentSize()!=0) {
            mRecommendContainer.setVisibility(View.VISIBLE);
        }else{
            mRecommendContainer.setVisibility(View.GONE);
        }
        //隐藏内容
        mRefreshContainer.setVisibility(View.GONE);
    }

    private boolean hasInput(boolean containSpace) {
        if (containSpace) {
            //包含空格
            return mSearchInputBox.getText().toString().length() > 0;
        } else {
            //不包含空格
            return mSearchInputBox.getText().toString().trim().length() > 0;
        }
    }

    @Override
    protected void onRetryClick() {
        //重新加载
        if (mSearchPagePresenter != null) {
            mSearchPagePresenter.research();
        }
    }

    @Override
    public void onHistoryLoaded(History histories) {
        setUpState(State.SUCCESS);
        LogUtils.d(this, "histories ---> " + histories);
        if (histories == null || histories.getHistories().size() == 0) {
            mHistoriesContainer.setVisibility(View.GONE);

        } else {
            mHistoriesContainer.setVisibility(View.VISIBLE);
            mHistoriesView.setTextList(histories.getHistories());

        }
    }

    @Override
    public void onHistoriesDeleted() {
        //更新历史
        if (mSearchPagePresenter != null) {
            mSearchPagePresenter.getHistories();
        }
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpState(State.SUCCESS);
//        LogUtils.d(this, "result ---> " + result);
        // 影藏历史记录和推荐
        mRecommendContainer.setVisibility(View.GONE);
        mHistoriesContainer.setVisibility(View.GONE);

        // 显示搜索结果界面
        mRefreshContainer.setVisibility(View.VISIBLE);

        // 设置数据
        try {
            mSearchResultAdapter.setData(result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data());
        } catch (Exception e) {
            e.printStackTrace();
            //切换到搜索内容为空
            setUpState(State.EMPTY);
        }
    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        //加载更多的结果
        //拿到结果，添加到适配器的尾部
        List<SearchResult.DataDTO.TbkDgMaterialOptionalResponseDTO.ResultListDTO.MapDataDTO> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchResultAdapter.addData(moreData);
        //提示用户，加载到的数据
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("加载到了" + moreData.size() + "条记录");
    }

    @Override
    public void onMoreLoaderError() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("网络异常，请稍后重试...");
    }

    @Override
    public void onMoreLoaderEmpty() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("没有更多数据了");
    }

    @Override
    public void onRecommendWordsLoaded(List<SearchRecommend.DataDTO> recommendWords) {
        setUpState(State.SUCCESS);
        //LogUtils.d(this, "recommendWords size---> " + recommendWords.size());
        List<String> keywords = new ArrayList<>();
        for (SearchRecommend.DataDTO item : recommendWords) {
            keywords.add(item.getKeyword());
        }
        if (recommendWords == null || recommendWords.size() == 0) {
            mRecommendContainer.setVisibility(View.GONE);

        } else {
            mRecommendView.setTextList(keywords);
            mRecommendContainer.setVisibility(View.VISIBLE);
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
    public void onFlowItemClick(String text) {
        //点击了历史记录或者推荐之后，发起搜索
        toSearch(text);
    }

    private void toSearch(String text) {
        if (mSearchPagePresenter != null) {
            //搜索之后，跳转到顶部
            mSearchList.scrollToPosition(0);
            mSearchInputBox.setText(text);
            mSearchInputBox.setFocusable(true);
            mSearchInputBox.requestFocus();
            mSearchInputBox.setSelection(text.length(),text.length());
            mSearchPagePresenter.doSearch(text);
        }
    }
}


