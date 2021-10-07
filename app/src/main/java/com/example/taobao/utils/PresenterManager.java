package com.example.taobao.utils;

import com.example.taobao.presenter.ICategoryPagerPresenter;
import com.example.taobao.presenter.IHomePresenter;
import com.example.taobao.presenter.IOnSellPagePresenter;
import com.example.taobao.presenter.ISearchPagePresenter;
import com.example.taobao.presenter.ISelectedPagePresenter;
import com.example.taobao.presenter.ITicketPresenter;
import com.example.taobao.presenter.impl.CategoryPagePresenterImpl;
import com.example.taobao.presenter.impl.HomePresenterImpl;
import com.example.taobao.presenter.impl.OnSellPagePresenterImpl;
import com.example.taobao.presenter.impl.SearchPresenterImpl;
import com.example.taobao.presenter.impl.SelectedPagePresenterImpl;
import com.example.taobao.presenter.impl.TicketPresenterImpl;


public class PresenterManager {
    //返回可以返回对应的接口，这样就不会被人看出里面的东西，不过这个是对内的使用，没必要也这样
    private final ICategoryPagerPresenter mCategoryPagePresenter;
    private final IHomePresenter mHomePresenter;
    private final ITicketPresenter mTicketPresenter;
    private final ISelectedPagePresenter mSelectedPagePresenter;
    private final IOnSellPagePresenter mOnSellPagePresenter;

    //单例
    public static final PresenterManager ourInstance = new PresenterManager();
    private final ISearchPagePresenter mSearchPagePresenter;

    public static PresenterManager getInstance() {
        return ourInstance;
    }

    /*getter*/
    public ICategoryPagerPresenter getCategoryPagePresenter() {
        return mCategoryPagePresenter;
    }

    /*getter*/
    public IHomePresenter getHomePresenter() {
        return mHomePresenter;
    }

    /*getter*/
    public ITicketPresenter getTicketPresenter() {
        return mTicketPresenter;
    }

    /*getter*/
    public ISelectedPagePresenter getSelectedPagePresenter() { return mSelectedPagePresenter; }

    /*getter*/
    public IOnSellPagePresenter getOnSellPagePresenter() {
        return mOnSellPagePresenter;
    }

    /*getter*/
    public ISearchPagePresenter getSearchPagePresenter() {
        return mSearchPagePresenter;
    }

    private PresenterManager() {

        mCategoryPagePresenter = new CategoryPagePresenterImpl();
        mHomePresenter = new HomePresenterImpl();
        mTicketPresenter = new TicketPresenterImpl();
        mSelectedPagePresenter = new SelectedPagePresenterImpl();
        mOnSellPagePresenter = new OnSellPagePresenterImpl();
        mSearchPagePresenter = new SearchPresenterImpl();
    }

}
