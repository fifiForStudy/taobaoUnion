package com.example.taobao.view;

import com.example.taobao.base.IBaseCallback;
import com.example.taobao.model.domain.HomePagerContent;

import java.util.List;

public interface ICategoryPagerCallback extends IBaseCallback {
    int getCategoryId();
    /*内容加载了*/
    void onContentLoaded(List<HomePagerContent.DataDTO> contents);



    /*加载更多时网络错误*/
    void onLoaderMoreError();

    /*没有更多的内容*/
    void onLoaderMoreEmpty();

    /*加载到了更多的内容*/
    void onLoaderMoreLoaded(List<HomePagerContent.DataDTO> contents);

    /*轮播图内容加载了*/
    void onLooperListLoaded(List<HomePagerContent.DataDTO> contents);



}
