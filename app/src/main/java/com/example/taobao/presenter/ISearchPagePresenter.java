package com.example.taobao.presenter;

import com.example.taobao.base.IBasePresenter;
import com.example.taobao.view.ISearchCallback;

public interface ISearchPagePresenter extends IBasePresenter<ISearchCallback> {
    /*获取搜索历史*/
    void getHistories();

    /*删除搜索历史*/
    void delHistories();

    /*发起搜索*/
    void doSearch(String keyword);

    /*重新搜索*/
    void research();

    /*获得更多的搜索结果*/
    void loaderMore();

    /*获取推荐词*/
    void getRecommendWords();

}
