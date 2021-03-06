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
        //?????????????????????
        mSearchPagePresenter.getRecommendWords();

        //mSearchPagePresenter.doSearch("iPhone");

        mSearchPagePresenter.getHistories();
    }
    //???????????????????????????
    @Override
    protected void release() {
        if (mSearchPagePresenter != null) {
            mSearchPagePresenter.unregisterViewCallback(this);
        }
    }
    //?????????????????????
    @Override
    protected View loadRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_search_layout, container, false);
    }
    //????????????
    @Override
    protected int getRootViewResId() {
        return R.layout.fragment_search;
    }

    @Override
    protected void initView(View rootView) {
        //?????????????????????
        mSearchList.setLayoutManager(new LinearLayoutManager(getContext()));
        //???????????????
        mSearchResultAdapter = new LinearItemContentAdapter();
        mSearchList.setAdapter(mSearchResultAdapter);
        //??????????????????list???????????????
        mSearchList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int topAndButtom = SizeUtils.dip2px(getContext(), 1.5f);
                outRect.top = topAndButtom;
                outRect.bottom = topAndButtom;
            }
        });
        //??????????????????
        mRefreshContainer.setEnableRefresh(false);
        mRefreshContainer.setEnableLoadmore(true);
        mRefreshContainer.setEnableOverScroll(true);
    }

    @Override
    protected void initListener() {
        /*??????????????????????????????????????????????????????*/
        mHistoriesView.setOnFlowTextItemClickListener(this);
        mRecommendView.setOnFlowTextItemClickListener(this);
        /*?????????????????????????????????*/
        mHistoriesDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????????
                mSearchPagePresenter.delHistories();
            }
        });
        /*?????????recycleView?????????????????????*/
        mRefreshContainer.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                //????????????????????????
                if (mSearchPagePresenter != null) {
                    mSearchPagePresenter.loaderMore();
                }
            }
        });
        /*???????????????????????????????????????????????????*/
        mSearchResultAdapter.setOnListItemClickListener(new LinearItemContentAdapter.OnListItemClickListener() {
            @Override
            public void onItemClick(IBaseInfo item) {
                //?????????????????????????????????
                TicketUtil.toTicketPage(getContext(), item);
            }
        });
        /*???????????????????????????????????????????????????*/
        mSearchInputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && mSearchPagePresenter != null) {
                    //?????????????????????????????????
                    String keyword = v.getText().toString().trim();
                    if (TextUtils.isEmpty(keyword)) {
                        return false;
                    }
                    //????????????
                    //mSearchPagePresenter.doSearch(keyword);
                    toSearch(keyword);
                }
                return false;
            }
        });
        /*????????????????????????*/
        mSearchInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //????????????????????????
                //??????????????????0???????????????????????????
                //????????????????????????
                mCleanInputBtn.setVisibility(hasInput(true) ? View.VISIBLE : View.GONE);
                //??????????????????????????????????????????????????????-->????????????
                mSearchBtn.setText(hasInput(false) ? "??????" : "??????");
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*??????????????????????????????*/
        mCleanInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchInputBox.setText("");
                //???????????????????????????????????????????????????
                switch2HistoryPager();
            }
        });
        /*????????????*/
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasInput(false)) {
                    //???????????????????????????
                    if (mSearchPagePresenter!=null) {
                        //mSearchPagePresenter.doSearch(mSearchInputBox.getText().toString().trim());
                        toSearch(mSearchInputBox.getText().toString().trim());
                        KeyboardUtil.hide(getContext(),v);
                    }
                } else {
                    //??????????????????????????????,???????????????????????????
                    KeyboardUtil.hide(getContext(),v);


                }
            }
        });
    }

    /*??????????????????????????????*/
    private void switch2HistoryPager() {
        //???????????????????????????????????????
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
        //????????????
        mRefreshContainer.setVisibility(View.GONE);
    }

    private boolean hasInput(boolean containSpace) {
        if (containSpace) {
            //????????????
            return mSearchInputBox.getText().toString().length() > 0;
        } else {
            //???????????????
            return mSearchInputBox.getText().toString().trim().length() > 0;
        }
    }

    @Override
    protected void onRetryClick() {
        //????????????
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
        //????????????
        if (mSearchPagePresenter != null) {
            mSearchPagePresenter.getHistories();
        }
    }

    @Override
    public void onSearchSuccess(SearchResult result) {
        setUpState(State.SUCCESS);
//        LogUtils.d(this, "result ---> " + result);
        // ???????????????????????????
        mRecommendContainer.setVisibility(View.GONE);
        mHistoriesContainer.setVisibility(View.GONE);

        // ????????????????????????
        mRefreshContainer.setVisibility(View.VISIBLE);

        // ????????????
        try {
            mSearchResultAdapter.setData(result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data());
        } catch (Exception e) {
            e.printStackTrace();
            //???????????????????????????
            setUpState(State.EMPTY);
        }
    }

    @Override
    public void onMoreLoaded(SearchResult result) {
        //?????????????????????
        //??????????????????????????????????????????
        List<SearchResult.DataDTO.TbkDgMaterialOptionalResponseDTO.ResultListDTO.MapDataDTO> moreData = result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data();
        mSearchResultAdapter.addData(moreData);
        //?????????????????????????????????
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("????????????" + moreData.size() + "?????????");
    }

    @Override
    public void onMoreLoaderError() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("??????????????????????????????...");
    }

    @Override
    public void onMoreLoaderEmpty() {
        mRefreshContainer.finishLoadmore();
        ToastUtil.showToast("?????????????????????");
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
        //??????????????????????????????????????????????????????
        toSearch(text);
    }

    private void toSearch(String text) {
        if (mSearchPagePresenter != null) {
            //??????????????????????????????
            mSearchList.scrollToPosition(0);
            mSearchInputBox.setText(text);
            mSearchInputBox.setFocusable(true);
            mSearchInputBox.requestFocus();
            mSearchInputBox.setSelection(text.length(),text.length());
            mSearchPagePresenter.doSearch(text);
        }
    }
}


