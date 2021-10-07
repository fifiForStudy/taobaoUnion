package com.example.taobao.presenter.impl;

import com.example.taobao.base.IBaseCallback;
import com.example.taobao.model.Api;
import com.example.taobao.model.domain.SelectedContent;
import com.example.taobao.model.domain.SelectedPageCategory;
import com.example.taobao.presenter.ISelectedPagePresenter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.RetrofitManager;
import com.example.taobao.utils.UrlUtils;
import com.example.taobao.view.ISelectedPageCallback;

import java.net.HttpURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SelectedPagePresenterImpl implements ISelectedPagePresenter {
    private ISelectedPageCallback mViewCallback = null;
    private final Api mApi;
//    private SelectedPageCategory.DataDTO mCurrentCategoryItem = null;

    public SelectedPagePresenterImpl() {
        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        mApi = retrofit.create(Api.class);
    }

    @Override
    public void getCategories() {
        //拿到retrofit
        if (mViewCallback != null) {
            mViewCallback.onLoading();
        }
        Call<SelectedPageCategory> task = mApi.getSelectedPageCategories();
        task.enqueue(new Callback<SelectedPageCategory>() {
            @Override
            public void onResponse(Call<SelectedPageCategory> call, Response<SelectedPageCategory> response) {
                int resultCode = response.code();
                LogUtils.d(SelectedPagePresenterImpl.this, "resultCode ---> " + resultCode);
                if (resultCode == HttpURLConnection.HTTP_OK) {
                    SelectedPageCategory result = response.body();
                    //通知UI更新
                    if (mViewCallback != null) {
                        mViewCallback.onCategoriesLoaded(result);
                    }
                } else {
                    onLoadedError();
                }
            }
            @Override
            public void onFailure(Call<SelectedPageCategory> call, Throwable throwable) {
                onLoadedError();
            }
        });
    }

    private void onLoadedError() {
        if (mViewCallback != null) {

            mViewCallback.onError();
        }
    }

    @Override
    public void getContentByCategory(SelectedPageCategory.DataDTO item) {
        LogUtils.d(this,"getContentByCategory ###############3");
//        this.mCurrentCategoryItem = item;
        int categoryId = item.getFavorites_id();
        LogUtils.d(this,"categoryId ==> "+categoryId);

        String url = UrlUtils.getSelectedPageContentUrl(categoryId);

        Call<SelectedContent> task = mApi.getSelectedPageContent(url);
        task.enqueue(new Callback<SelectedContent>() {
            @Override
            public void onResponse(Call<SelectedContent> call, Response<SelectedContent> response) {
                int resultCode = response.code();
                LogUtils.d(SelectedPagePresenterImpl.this, "getContentByCategory resultCode ####### " + resultCode);
                if (resultCode == HttpURLConnection.HTTP_OK) {
                    SelectedContent result = response.body();
                    //通知UI更新
                    if (mViewCallback != null) {
                        mViewCallback.onContentLoaded(result);

                    }
                } else {
                    onLoadedError();
                }

            }

            @Override
            public void onFailure(Call<SelectedContent> call, Throwable throwable) {
                onLoadedError();
            }
        });
    }

    @Override
    public void reloadContent() {
//        if (mCurrentCategoryItem != null) {
//            this.getContentByCategory(mCurrentCategoryItem);
//        }
        this.getCategories();
    }


    @Override
    public void registerViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = callback;
    }

    @Override
    public void unregisterViewCallback(ISelectedPageCallback callback) {
        this.mViewCallback = null;
    }
}
