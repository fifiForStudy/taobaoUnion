package com.example.taobao.presenter;

import com.example.taobao.base.IBasePresenter;
import com.example.taobao.view.ITicketPagerCallback;

public interface ITicketPresenter extends IBasePresenter<ITicketPagerCallback> {

    void getTicket(String title, String url, String cover);

}
