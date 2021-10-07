package com.example.taobao.presenter;

import com.example.taobao.base.IBaseCallback;
import com.example.taobao.base.IBasePresenter;
import com.example.taobao.model.domain.SelectedPageCategory;
import com.example.taobao.view.ISelectedPageCallback;

public interface ISelectedPagePresenter extends IBasePresenter<ISelectedPageCallback> {
    /*获取分类*/
    void getCategories();

    /*获取分类内容*/
    void getContentByCategory(SelectedPageCategory.DataDTO item);

    /*重新加载内容*/
    void reloadContent();

}
