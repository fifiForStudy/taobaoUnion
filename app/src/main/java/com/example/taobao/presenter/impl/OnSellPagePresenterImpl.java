package com.example.taobao.presenter.impl;

import com.example.taobao.model.Api;
import com.example.taobao.model.domain.OnSellContent;
import com.example.taobao.presenter.IOnSellPagePresenter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.RetrofitManager;
import com.example.taobao.utils.UrlUtils;
import com.example.taobao.view.IOnSellPageCallback;
import com.lcodecore.tkrefreshlayout.utils.LogUtil;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnSellPagePresenterImpl implements IOnSellPagePresenter {

    private final Api mApi;
    public static final int DEFAULT_PAGE = 1;
    private int mCurrentPage = DEFAULT_PAGE;
    private IOnSellPageCallback mOnSellPageCallback = null;
    //当前加载状态
    private boolean mIsLoading = false;

    public OnSellPagePresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }


    @Override
    public void getOnSellContent() {
        LogUtils.d(this,"getOnSellContent.....");
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        //通知UI状态为加载中
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onLoading();
        }
        //获取特惠内容
//        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
//        Api api = retrofit.create(Api.class);

        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        LogUtils.d(this,"targetUrl --->"+targetUrl);

        Call<OnSellContent> task = mApi.getOnSellPageContent(targetUrl);

        task.enqueue(new Callback<OnSellContent>() {
            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(OnSellPagePresenterImpl.this, "getOnSellContent code --> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    onSuccess(result);
                } else {
                    onError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable throwable) {
                LogUtils.d(OnSellPagePresenterImpl.this, "onFailure......");
                onError();
            }
        });
    }

    private boolean isEmpty(OnSellContent content) {
        int size = content.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size();
        return size == 0;
    }

    private void onSuccess(OnSellContent result) {
        if (mOnSellPageCallback != null) {
            try {
                if (isEmpty(result)) {
                    onEmpty();
                } else {
                    mOnSellPageCallback.onContentLoadedSuccess(result);
                }
            } catch (Exception e) {
                e.printStackTrace();
                onEmpty();
            }
        }
    }

    private void onEmpty() {
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onEmpty();

        }
    }

    private void onError() {
        mIsLoading = false;
        if (mOnSellPageCallback != null) {
            mOnSellPageCallback.onError();

        }
    }

    @Override
    public void reloaded() {
        //重新加载
        this.getOnSellContent();
    }



    @Override
    public void loaderMore() {
        if (mIsLoading) {
            return;
        }
        mIsLoading = true;
        //加载更多
        mCurrentPage++;
        //去加载更多内容
        String targetUrl = UrlUtils.getOnSellPageUrl(mCurrentPage);
        Call<OnSellContent> task = mApi.getOnSellPageContent(targetUrl);
        task.enqueue(new Callback<OnSellContent>() {

            @Override
            public void onResponse(Call<OnSellContent> call, Response<OnSellContent> response) {
                mIsLoading = false;
                int code = response.code();
                LogUtils.d(OnSellPagePresenterImpl.this, "getOnSellContent code --> " + code);
                if (code == HttpURLConnection.HTTP_OK) {
                    OnSellContent result = response.body();
                    onMoreLoaded(result);
                } else {
                    onMoreLoadedError();
                }
            }

            @Override
            public void onFailure(Call<OnSellContent> call, Throwable throwable) {
                onMoreLoadedError();
            }
        });
    }

    private void onMoreLoadedError() {
        mCurrentPage--;
        mOnSellPageCallback.onMoreLoadedError();
    }

    //加载更多的结果，通知UI更新
    private void onMoreLoaded(OnSellContent result) {
        if (mOnSellPageCallback != null) {
            if (!isEmpty(result)) {
                mOnSellPageCallback.onMoreLoaded(result);
            } else {
                mCurrentPage--;
                mOnSellPageCallback.onMoreLoadedEmpty();
            }

        }
    }

    @Override
    public void registerViewCallback(IOnSellPageCallback callback) {
        this.mOnSellPageCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IOnSellPageCallback callback) {
        this.mOnSellPageCallback = null;
    }
}
