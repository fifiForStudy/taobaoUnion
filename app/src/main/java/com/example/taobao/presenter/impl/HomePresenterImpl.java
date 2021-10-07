package com.example.taobao.presenter.impl;

import com.example.taobao.model.Api;
import com.example.taobao.model.domain.Categories;
import com.example.taobao.presenter.IHomePresenter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.RetrofitManager;
import com.example.taobao.view.IHomeCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomePresenterImpl implements IHomePresenter {
    private IHomeCallback mCallback = null;

    @Override
    public void getCategories() {
        if (mCallback != null) {
            mCallback.onLoading();
        }
        //加载分类数据
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<Categories> task = api.getCategories();
        task.enqueue(new Callback<Categories>() {
            @Override
            public void onResponse(Call<Categories> call, Response<Categories> response) {
                //数据结果
                int code = response.code();
                LogUtils.d(HomePresenterImpl.this, "result code is --> " + code);
                if (HttpURLConnection.HTTP_OK==code) {
                    //请求成功
                    Categories categories = response.body();
//                    LogUtils.d(HomePresenterImpl.this, categories.toString());
                    //通知
                    if (mCallback != null) {
                        if (categories==null||categories.getData().size()==0) {
                            mCallback.onEmpty();
                        }else {
                            mCallback.onCategoriesLoaded(categories);
                        }
                    }
                }else{
                    //请求失败
                    LogUtils.i(HomePresenterImpl.this, "请求失败......");
                    if (mCallback != null) {
                        mCallback.onError();
                    }
                }
            }

            @Override
            public void onFailure(Call<Categories> call, Throwable throwable) {
                //加载失败的结果
                LogUtils.e(HomePresenterImpl.this, "请求错误......" + throwable);
                if (mCallback != null) {
                    mCallback.onError();
                }
            }
        });
    }

    @Override
    public void registerViewCallback(IHomeCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void unregisterViewCallback(IHomeCallback callback) {
        mCallback = null;
    }
}