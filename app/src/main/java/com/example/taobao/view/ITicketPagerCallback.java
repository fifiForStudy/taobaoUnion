package com.example.taobao.view;

import com.example.taobao.base.IBaseCallback;
import com.example.taobao.model.domain.TicketResult;

public interface ITicketPagerCallback extends IBaseCallback {

    /*淘口令加载结果*/
    void onTicketLoaded(String cover, TicketResult result);

}
