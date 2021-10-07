package com.example.taobao.view;

import com.example.taobao.base.IBaseCallback;
import com.example.taobao.model.domain.SelectedContent;
import com.example.taobao.model.domain.SelectedPageCategory;

public interface ISelectedPageCallback extends IBaseCallback {
    /*分类内容结果*/
    void onCategoriesLoaded(SelectedPageCategory categories);


    /*内容*/
    void onContentLoaded(SelectedContent content);
}
