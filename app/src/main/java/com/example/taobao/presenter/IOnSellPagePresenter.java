package com.example.taobao.presenter;

import com.example.taobao.base.IBasePresenter;
import com.example.taobao.view.IOnSellPageCallback;

public interface IOnSellPagePresenter extends IBasePresenter<IOnSellPageCallback> {
    //加载特惠内容
    void getOnSellContent();

    //重新加载内容
    void reloaded();

    //加载更多内容
    void loaderMore();
}
