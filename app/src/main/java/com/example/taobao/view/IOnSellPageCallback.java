package com.example.taobao.view;

import com.example.taobao.base.IBaseCallback;
import com.example.taobao.model.domain.OnSellContent;

public interface IOnSellPageCallback extends IBaseCallback {
    //特惠内容加载完成
    void onContentLoadedSuccess(OnSellContent result);

    //加载更多的结果
    void onMoreLoaded(OnSellContent moreResult);


    //加载更多异常
    void onMoreLoadedError();

    //没有更多的内容
    void onMoreLoadedEmpty();
}
