package com.example.taobao.ui.activity;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.taobao.R;
import com.example.taobao.base.BaseActivity;
import com.example.taobao.base.BaseFragment;
import com.example.taobao.ui.fragment.HomeFragment;
import com.example.taobao.ui.fragment.OnSellFragment;
import com.example.taobao.ui.fragment.SearchFragment;
import com.example.taobao.ui.fragment.SelectedFragment;
import com.example.taobao.utils.LogUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements IMainActivity{

    private static final String TAG = "MainActivity";

    @BindView(R.id.main_navigation_bar)
    public BottomNavigationView mNavigationView;
    private HomeFragment mHomeFragment;
    private OnSellFragment mRedPacketFragment;
    private SelectedFragment mSelectedFragment;
    private SearchFragment mSearchFragment;
    private FragmentManager mFm;

    @Override
    protected void initPresenter() {

    }



    @Override
    protected void initEvent() {
        initListener();
    }

    @Override
    protected void initView() {
        initFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }


    private void initFragment() {
        mHomeFragment = new HomeFragment();
        mRedPacketFragment = new OnSellFragment();
        mSelectedFragment = new SelectedFragment();
        mSearchFragment = new SearchFragment();

        mFm = getSupportFragmentManager();
        switchFragment(mHomeFragment);
    }

    private void initListener() {
        mNavigationView.setOnNavigationItemSelectedListener(item -> {
//                Log.d(TAG, "title ===> " + item.getTitle());
            if (item.getItemId() == R.id.home) {

                LogUtils.d(this, "切换到首页");
                switchFragment(mHomeFragment);

            } else if (item.getItemId() == R.id.selected) {

                LogUtils.i(this, "切换到精选");
                switchFragment(mSelectedFragment);

            } else if (item.getItemId() == R.id.red_packet) {

                LogUtils.w(this, "切换到特惠");
                switchFragment(mRedPacketFragment);

            } else if (item.getItemId() == R.id.search) {

                LogUtils.e(this, "切换到搜索");
                switchFragment(mSearchFragment);

            }
            return true;
        });
    }

    //持有一个引用来保存上一个fragment
    //上一次显示的fragment
    private BaseFragment lastOneFragment = null;


    private void switchFragment(BaseFragment targetFragment) {
        //如果上一个fragment和当前要切换的fragment是同一个，那么，不需要切换
        if (lastOneFragment==targetFragment) {
            return;
        }
        //修改成add和hide的方式来控制Fragment的切换

        //拿到管理器
        FragmentTransaction transaction = mFm.beginTransaction();
        if (!targetFragment.isAdded()) {

            transaction.add(R.id.main_page_container, targetFragment);
        } else {
            transaction.show(targetFragment);
        }
        if (lastOneFragment != null) {
            transaction.hide(lastOneFragment);
        }
        lastOneFragment = targetFragment;

        //transaction.replace(R.id.main_page_container, targetFragment);
        transaction.commit();
    }

    /*跳转到搜索界面*/
    @Override
    public void switch2Search() {
        //switchFragment(mSearchFragment);
        //切换底部导航栏的选中项
        mNavigationView.setSelectedItemId(R.id.search);
    }
}