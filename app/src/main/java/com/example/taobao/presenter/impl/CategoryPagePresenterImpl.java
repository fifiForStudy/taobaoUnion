package com.example.taobao.presenter.impl;

import com.example.taobao.model.Api;
import com.example.taobao.model.domain.HomePagerContent;
import com.example.taobao.presenter.ICategoryPagerPresenter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.RetrofitManager;
import com.example.taobao.utils.UrlUtils;
import com.example.taobao.view.ICategoryPagerCallback;
import com.lcodecore.tkrefreshlayout.utils.LogUtil;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CategoryPagePresenterImpl implements ICategoryPagerPresenter {

    private Map<Integer, Integer> pagesInfo = new HashMap<>();
    public static final int DEFAULT_PAGE = 1;
    private Integer mCurrentPage;

//    //单例
//    private CategoryPagePresenterImpl() {
//    }

//    private static ICategoryPagerPresenter sInstance = null;

//    public static ICategoryPagerPresenter getInstance() {
//        if (sInstance == null) {
//            sInstance = ;
//        }
//        return sInstance;
//    }

    @Override
    public void getContentByCategoryId(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {

                callback.onLoading();
            }

        }

        //根据分类id去加载内容

        Integer targetPage = pagesInfo.get(categoryId);
        if (targetPage == null) {
            targetPage = DEFAULT_PAGE;
            pagesInfo.put(categoryId, targetPage);
        }

        Call<HomePagerContent> task = createTask(categoryId, targetPage);

        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                int code = response.code();
                LogUtils.d(CategoryPagePresenterImpl.this, "code ----> " + code);
                if (HttpURLConnection.HTTP_OK == code) {
                    HomePagerContent pageContent = response.body();
                    //LogUtils.d(CategoryPagePresenterImpl.this, "pageContent ----> " + pageContent);
                    //把数据给到UI更新
                    handleHomePageContentResult(pageContent, categoryId);
                } else {
                    //TODO:
                    handleNetWorkError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable throwable) {
                LogUtils.d(CategoryPagePresenterImpl.this, "onFailure ----> " + throwable);
                handleNetWorkError(categoryId);
            }
        });
    }

    private Call<HomePagerContent> createTask(int categoryId, Integer targetPage) {
        String homePagerUrl = UrlUtils.createHomePagerUrl(categoryId, targetPage);
        LogUtils.d(this, "home page url -----> " + homePagerUrl);

        Retrofit retrofit = RetrofitManager.getInstance().getRetrofit();
        Api api = retrofit.create(Api.class);
        Call<HomePagerContent> task = api.getHomePageContent(homePagerUrl);
        return task;
    }

    //处理网络错误
    private void handleNetWorkError(int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {

                callback.onError();
            }
        }
    }

    /*通知UI层更新数据*/
    private void handleHomePageContentResult(HomePagerContent pageContent, int categoryId) {
        List<HomePagerContent.DataDTO> data = pageContent.getData();
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                if (pageContent == null || pageContent.getData().size() == 0) {
                    callback.onEmpty();
                } else {
                    List<HomePagerContent.DataDTO> looperData = data.subList(data.size() - 5, data.size());
                    callback.onLooperListLoaded(looperData);
                    callback.onContentLoaded(data);
                }
            }
        }
    }

    //实现加载更多
    @Override
    public void loadMore(int categoryId) {
        //加载更多的数据
        //1.拿到当前的页面的id
        mCurrentPage = pagesInfo.get(categoryId);
        if (mCurrentPage ==null) {
            mCurrentPage = 1;//第一页
        }
        //2.页码++
        mCurrentPage++;
        pagesInfo.put(categoryId,mCurrentPage);
        LogUtils.d(this,"mCurrentPage*********************************>  "+mCurrentPage.toString());
        //3.加载数据
        Call<HomePagerContent> task = createTask(categoryId, mCurrentPage);
        //4.处理数据结果
        task.enqueue(new Callback<HomePagerContent>() {
            @Override
            public void onResponse(Call<HomePagerContent> call, Response<HomePagerContent> response) {
                //结果
                int code = response.code();
                LogUtils.d(CategoryPagePresenterImpl.this, "result code -----> " + code);
                if (code== HttpURLConnection.HTTP_OK) {
                    HomePagerContent result = response.body();
                    LogUtils.d(CategoryPagePresenterImpl.this,"result ---->" +result.toString());
                    handleLoaderResult(result,categoryId);

                }else{
                    handleLoaderMoreError(categoryId);
                }
            }

            @Override
            public void onFailure(Call<HomePagerContent> call, Throwable throwable) {
                //请求失败
                LogUtils.d(CategoryPagePresenterImpl.this, throwable.toString());
                handleLoaderMoreError(categoryId);
            }
        });
    }

    private void handleLoaderResult(HomePagerContent result, int categoryId) {
        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId()==categoryId) {
                if (result==null||result.getData().size()==0) {
                    callback.onLoaderMoreEmpty();
                    mCurrentPage--;
                    pagesInfo.put(categoryId, mCurrentPage);
                }else{
                    callback.onLoaderMoreLoaded(result.getData());
                }
            }
        }
    }

    private void handleLoaderMoreError(int categoryId) {


        for (ICategoryPagerCallback callback : callbacks) {
            if (callback.getCategoryId() == categoryId) {
                callback.onLoaderMoreError();
                mCurrentPage--;
                pagesInfo.put(categoryId, mCurrentPage);
            }
        }
    }

    @Override
    public void reload(int categoryId) {

    }

    //用集合保存注册过来的页面
    private List<ICategoryPagerCallback> callbacks = new ArrayList<>();

    @Override
    public void registerViewCallback(ICategoryPagerCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(ICategoryPagerCallback callback) {
        callbacks.remove(callback);
    }
}
