package com.example.taobao.presenter;

import com.example.taobao.base.IBasePresenter;
import com.example.taobao.view.ICategoryPagerCallback;

public interface ICategoryPagerPresenter extends IBasePresenter<ICategoryPagerCallback> {
    //根据分类id去获取内容
    void getContentByCategoryId(int categoryId);

    void loadMore(int categoryId);

    void reload(int categoryId);


}
