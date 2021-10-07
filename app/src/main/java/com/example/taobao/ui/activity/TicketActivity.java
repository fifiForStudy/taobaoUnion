package com.example.taobao.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.taobao.R;
import com.example.taobao.base.BaseActivity;
import com.example.taobao.model.domain.TicketResult;
import com.example.taobao.presenter.ITicketPresenter;
import com.example.taobao.utils.LogUtils;
import com.example.taobao.utils.PresenterManager;
import com.example.taobao.utils.ToastUtil;
import com.example.taobao.utils.UrlUtils;
import com.example.taobao.view.ITicketPagerCallback;

import butterknife.BindView;

public class TicketActivity extends BaseActivity implements ITicketPagerCallback {

    private boolean mHasTaobaoApp = false;

    private ITicketPresenter mTicketPresenter;

    @BindView(R.id.ticket_cover)
    public ImageView mCover;

    @BindView(R.id.ticket_back_press)
    public View backPress;

    @BindView(R.id.ticket_code)
    public EditText mTicketCode;

    @BindView(R.id.ticket_cpoy_or_open_btn)
    public TextView mOpenOrCopyBtn;

    @BindView(R.id.ticket_cover_loading)
    public View loadingView;

    @BindView(R.id.ticket_load_retry)
    public TextView retryLoadText;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_ticket;
    }

    @Override
    protected void initPresenter() {

        mTicketPresenter = PresenterManager.getInstance().getTicketPresenter();
        if (mTicketPresenter != null) {
            mTicketPresenter.registerViewCallback(this);
        }
        //判断是否安装了淘宝
        //安装系统中，以包名作为id来存在，保证他的唯一性
        // act=android.intent.action.MAIN
        // cat=[android.intent.category.LAUNCHER]
        // flg=0x30200000
        // cmp=com.taobao.taobao/com.taobao.tao.welcome.Welcome
        // bnds=[132,705][384,985] (has extras)} from uid 10033
        //包名：com.taobao.taobao
        //检查是否安装淘宝应用
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo("com.taobao.taobao", PackageManager.MATCH_UNINSTALLED_PACKAGES);
            mHasTaobaoApp = packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            mHasTaobaoApp = false;
        }
        LogUtils.d(this, "hasTaoBaoApp +++++++> " + mHasTaobaoApp);
        //根据这个值去修改UI
        mOpenOrCopyBtn.setText(mHasTaobaoApp ? "打开淘宝领券" : "复制淘口令");
    }

    private void updateTipsText() {

    }

    @Override
    protected void release() {
        if (mTicketPresenter != null) {
            mTicketPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initEvent() {
        backPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        mOpenOrCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //复制淘口令
                //拿到内容
                String ticketCode = mTicketCode.getText().toString().trim();
                LogUtils.d(TicketActivity.this, "ticketCode ----> " + ticketCode);
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                //复制到粘贴板
                ClipData clipData = ClipData.newPlainText("sob_taobao_ticket_code", ticketCode);
                cm.setPrimaryClip(clipData);
                //判断有没有淘宝
                if (mHasTaobaoApp) {
                    //有就打开淘宝
                    Intent taobaoIntent = new Intent();
                    //taobaoIntent.setAction("android.intent.action.MAIN");
                    //taobaoIntent.addCategory("android.intent.category.LAUNCHER");

                    // Attempting to destroy on removed layer: com.taobao.taobao/com.taobao.tao.TBMainActivity#1
                    ComponentName componentName = new ComponentName("com.taobao.taobao", "com.taobao.tao.TBMainActivity");
                    taobaoIntent.setComponent(componentName);
                    startActivity(taobaoIntent);


                } else {
                    //没有就显示复制成功
                    ToastUtil.showToast("复制成功，黏贴分享，或打开淘宝");
                }

            }
        });
    }

    @Override
    public void onTicketLoaded(String cover, TicketResult result) {

        //图片
        if (mCover != null && !TextUtils.isEmpty(cover)) {

            ViewGroup.LayoutParams layoutParams = mCover.getLayoutParams();
            int targetWidth = layoutParams.width / 2;
            LogUtils.d(this, "cover width ----> " + targetWidth);
            String coverPath = UrlUtils.getCoverPath(cover);
            LogUtils.d(this, "coverPath ====> " + coverPath);

            Glide.with(this).load(coverPath).into(mCover);
        }if (TextUtils.isEmpty(cover)){
            mCover.setImageResource(R.mipmap.placehoder_pic);
        }
        //口令
        try {
            String model = result.getData().getTbk_tpwd_create_response().getData().getModel();
            if (result != null && model != null) {
                mTicketCode.setText(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onError() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoading() {
        if (retryLoadText != null) {
            retryLoadText.setVisibility(View.GONE);
        }
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEmpty() {

    }

}
