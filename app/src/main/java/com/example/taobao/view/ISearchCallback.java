package com.example.taobao.view;

import com.example.taobao.base.IBaseCallback;
import com.example.taobao.model.domain.History;
import com.example.taobao.model.domain.SearchRecommend;
import com.example.taobao.model.domain.SearchResult;

import java.util.List;

public interface ISearchCallback extends IBaseCallback {
    /*搜索历史结果*/
    void onHistoryLoaded(History histories);

    /*历史记录删除完成*/
    void onHistoriesDeleted();

    /*搜索结果:成功*/
    void onSearchSuccess(SearchResult result);

    /*加载到了更多的内容*/
    void onMoreLoaded(SearchResult result);

    /*加载更多失败*/
    void onMoreLoaderError();

    /*没有更多的内容*/
    void onMoreLoaderEmpty();

    /*推荐词获取结果*/
    void onRecommendWordsLoaded(List<SearchRecommend.DataDTO > recommendWords);

}
