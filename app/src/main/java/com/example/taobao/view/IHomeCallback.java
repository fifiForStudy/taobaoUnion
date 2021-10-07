package com.example.taobao.view;

import com.example.taobao.base.IBaseCallback;
import com.example.taobao.model.domain.Categories;

public interface IHomeCallback extends IBaseCallback {
    //通知UI更新
    void onCategoriesLoaded(Categories categories);


}
