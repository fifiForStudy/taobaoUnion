package com.example.taobao.presenter.impl;

import com.example.taobao.model.Api;
import com.example.taobao.model.domain.History;
import com.example.taobao.model.domain.SearchRecommend;
import com.example.taobao.model.domain.SearchResult;
import com.example.taobao.presenter.ISearchPagePresenter;
import com.example.taobao.utils.JsonCacheUtil;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.RetrofitManager;
import com.example.taobao.view.ISearchCallback;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchPresenterImpl implements ISearchPagePresenter {

    private final Api mApi;
    private ISearchCallback mSearchViewCallback = null;

    public static final int DEFAULT_PAGE = 0;
    /*搜索的当前页面*/
    private int mCurrentPage = DEFAULT_PAGE;
    private String mCurrentKeyword = null;
    private final JsonCacheUtil mJsonCacheUtil;

    public SearchPresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
        mJsonCacheUtil = JsonCacheUtil.getInstance();
    }

    @Override
    public void getHistories() {
        History histories = mJsonCacheUtil.getValue(KEY_HISTORY, History.class);
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onHistoryLoaded(histories);
        }
    }

    @Override
    public void delHistories() {
        mJsonCacheUtil.delCache(KEY_HISTORY);
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onHistoriesDeleted();
        }
    }

    public static final String KEY_HISTORY = "key_history";

    public static final int DEFAULT_HISTORIES_SIZE = 10;
    private int mHistoriesMaxSize = DEFAULT_HISTORIES_SIZE;

    /*添加历史记录*/
    private void saveHistory(String history) {
        //delHistories();
        History histories = mJsonCacheUtil.getValue(KEY_HISTORY, History.class);
        //如果历史记录已经存在，就先删除，再添加
        //去重
        List<String> historiesList = null;
        if (histories != null && histories.getHistories() != null) {
            historiesList = histories.getHistories();
            if (historiesList.contains(history)) {
                historiesList.remove(history);
            }
        }
        //处理没有数据的情况
        if (historiesList == null) {
            historiesList = new ArrayList<>();
        }
        if (histories == null) {
            histories = new History();
        }
        histories.setHistories(historiesList);
        //对个数进行限制
        if (historiesList.size() > mHistoriesMaxSize) {
            historiesList = historiesList.subList(0, mHistoriesMaxSize);
        }
        //添加记录
        historiesList.add(history);
        //保存记录
        mJsonCacheUtil.saveCache(KEY_HISTORY, histories);
    }

    @Override
    public void doSearch(String keyword) {
        //TODO:!!!!!!!!!!!!!!!!!!!!!!!!!!!
        if (mCurrentKeyword == null || !mCurrentKeyword.equals(keyword)) {
            this.saveHistory(keyword);
            this.mCurrentKeyword = keyword;
        }
        //更新UI
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onLoading();

        }
        Call<SearchResult> task = mApi.doSearch(mCurrentPage, keyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "doSearch code --> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    handleSearchResult(response.body());
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable throwable) {
                throwable.printStackTrace();
                onError();
            }
        });
    }

    private void onError() {
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onError();
        }
    }

    private void handleSearchResult(SearchResult result) {
        if (mSearchViewCallback != null) {
            if (isEmpty(result)) {
                //数据为空
                mSearchViewCallback.onEmpty();
            } else {
                mSearchViewCallback.onSearchSuccess(result);

            }
        }
    }

    private boolean isEmpty(SearchResult result) {
        try {
            return result == null || result.getData().getTbk_dg_material_optional_response().getResult_list().getMap_data().size() == 0;
        } catch (Exception e) {

            return false;
        }
    }

    @Override
    public void research() {
        if (mCurrentKeyword == null) {
            if (mSearchViewCallback != null) {
                mSearchViewCallback.onEmpty();

            }
        } else {
            //重新搜索
            this.doSearch(mCurrentKeyword);
        }
    }

    @Override
    public void loaderMore() {
        mCurrentPage++;
        if (mCurrentKeyword == null) {
            if (mSearchViewCallback != null) {
                mSearchViewCallback.onEmpty();

            }
        } else {
            //重新搜索
            doSearchMore();
        }
    }

    private void doSearchMore() {
        Call<SearchResult> task = mApi.doSearch(mCurrentPage, mCurrentKeyword);
        task.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "doSearchMore code --> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    handleMoreSearchResult(response.body());
                } else {
                    onLoaderMoreError();
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable throwable) {
                throwable.printStackTrace();
                onLoaderMoreError();
            }
        });
    }


    private void handleMoreSearchResult(SearchResult result) {
        if (mSearchViewCallback != null) {
            if (isEmpty(result)) {
                //数据为空
                mSearchViewCallback.onMoreLoaderEmpty();
            } else {
                mSearchViewCallback.onMoreLoaded(result);

            }
        }
    }

    private void onLoaderMoreError() {
        mCurrentPage--;
        if (mSearchViewCallback != null) {
            mSearchViewCallback.onMoreLoaderError();
        }
    }

    @Override
    public void getRecommendWords() {
        Call<SearchRecommend> task = mApi.getRecommendWords();
        task.enqueue(new Callback<SearchRecommend>() {
            @Override
            public void onResponse(Call<SearchRecommend> call, Response<SearchRecommend> response) {
                int code = response.code();
                LogUtils.d(SearchPresenterImpl.this, "getRecommendWords code --> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    //处理结果
                    if (mSearchViewCallback != null) {
                        mSearchViewCallback.onRecommendWordsLoaded(response.body().getData());
                    }
                }
            }

            @Override
            public void onFailure(Call<SearchRecommend> call, Throwable throwable) {

                throwable.printStackTrace();
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback callback) {
        this.mSearchViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISearchCallback callback) {
        this.mSearchViewCallback = null;
    }
}
