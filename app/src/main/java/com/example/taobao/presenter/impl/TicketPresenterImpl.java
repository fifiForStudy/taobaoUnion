package com.example.taobao.presenter.impl;

import com.example.taobao.model.Api;
import com.example.taobao.model.domain.TicketParams;
import com.example.taobao.model.domain.TicketResult;
import com.example.taobao.presenter.ITicketPresenter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.RetrofitManager;
import com.example.taobao.utils.UrlUtils;
import com.example.taobao.view.ITicketPagerCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TicketPresenterImpl implements ITicketPresenter {
    private ITicketPagerCallback mViewCallback = null;
    private String mCover = null;
    private TicketResult mTicketResult;

    enum LoadState {
        LOADING, SUCCESS, ERROR, NONE
    }

    private LoadState mCurrentState = LoadState.NONE;

    @Override
    public void getTicket(String title, String url, String cover) {

        this.onTicketLoading();
        this.mCover = cover;
        LogUtils.d(this, "title ----->" + title);
        LogUtils.d(this, "url ----->" + url);
        LogUtils.d(this, "cover ----->" + cover);

        String targetUrl = UrlUtils.getTicketUrl(url);
        LogUtils.d(this, "targetUrl *****>" + targetUrl);
        //去获取淘口令
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        TicketParams ticketParams = new TicketParams(targetUrl, title);

        Call<TicketResult> task = api.getTicket(ticketParams);
        task.enqueue(new Callback<TicketResult>() {
            @Override
            public void onResponse(Call<TicketResult> call, Response<TicketResult> response) {
                int code = response.code();
                if (code == HttpURLConnection.HTTP_OK) {
                    //请求成功
                    mTicketResult = response.body();
                    LogUtils.d(TicketPresenterImpl.this, "ticketResult ---> " + mTicketResult);
                    //通知UI更新

                    onTicketLoadedSuccess();
                } else {
                    //请求失败
                    onLoadedTicketError();


                }
            }

            @Override
            public void onFailure(Call<TicketResult> call, Throwable throwable) {
                //请求失败
                onLoadedTicketError();
                mCurrentState = LoadState.ERROR;

            }
        });

    }

    private void onTicketLoadedSuccess() {
        if (mViewCallback != null) {
            mViewCallback.onTicketLoaded(mCover, mTicketResult);
        }else{
            mCurrentState = LoadState.SUCCESS;
        }
    }

    private void onLoadedTicketError() {
        if (mViewCallback != null) {
            mViewCallback.onError();
        }else{
            mCurrentState = LoadState.ERROR;
        }
    }

    @Override
    public void registerViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = callback;

        if (mCurrentState != LoadState.NONE) {
            //说明状态已经改变了
            //更新UI
            if (mCurrentState == LoadState.SUCCESS) {
                onTicketLoadedSuccess();
            } else if(mCurrentState==LoadState.ERROR){
                onLoadedTicketError();
            }else if(mCurrentState==LoadState.LOADING){
                onTicketLoading();
            }
        }

    }

    @Override
    public void unregisterViewCallback(ITicketPagerCallback callback) {
        this.mViewCallback = null;

    }

    private void onTicketLoading() {
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }else {
            mCurrentState = LoadState.LOADING;
        }
    }
}
