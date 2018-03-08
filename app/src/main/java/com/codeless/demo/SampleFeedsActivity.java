package com.codeless.demo;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.codeless.tracker.Tracker;
import com.netease.youliao.newsfeeds.core.NNewsFeedsSDK;
import com.netease.youliao.newsfeeds.ui.base.activity.BaseNavigationBarActivity;
import com.netease.youliao.newsfeeds.ui.core.NNFeedsFragment;
import com.netease.youliao.newsfeeds.ui.core.NNewsFeedsUI;
import com.netease.youliao.newsfeeds.ui.core.NNewsFeedsUISDK;
import com.netease.youliao.newsfeeds.utils.NNFLogUtil;

import java.util.HashMap;
import java.util.Map;

public class SampleFeedsActivity extends BaseNavigationBarActivity {

    private final static String TAG = "SampleFeedsActivity";
    // 高德定位
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private NNFeedsFragment mFeedsFragment;
    public static SampleFeedsActivity sInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRealContentView(R.layout.activity_feeds);

        setTitle("网易有料");
        // 不显示左侧返回按钮
        setLeftViewVisible(false);
        getNavigationBarView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFeedsFragment != null) {
                    // 点击导航栏返回到顶部，模仿ios点击状态栏返回到顶部的功能
                    mFeedsFragment.scrollToTop();
                }
            }
        });

        sInstance = SampleFeedsActivity.this;

        Tracker.init(this.getApplicationContext());
        /**
         * 初始化网易有料SDK：在自定义Application中初始化网易有料API SDK
         */
        new NNewsFeedsUISDK.Builder()
                .setAppKey(BuildConfig.APP_KEY)
                .setAppSecret(BuildConfig.APP_SECRET)
                .setContext(getApplicationContext())
                .setMaxCacheNum(60)
                .setMaxCacheTime(60 * 60 * 1000)
                .setAutoRefreshInterval(60 * 60 * 1000)
                .setLogLevel(NNFLogUtil.LOG_NONE)
                .build();
        /********* 集成方式请二选一 *********/

        // 快速集成
        initFeedsByOneStep();
        initLocation();
    }

    /**
     * 第一步：接入信息流UI SDK，快速集成信息流主页 NNFeedsFragment
     */
    private void initFeedsByOneStep() {
        Map<String, Map<String, Object>> map = new HashMap<>();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        mFeedsFragment = NNewsFeedsUI.createFeedsFragment(null, null, map);
        ft.replace(R.id.fragment_container, mFeedsFragment);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocation();
        destroyLocation();
    }

    /***************接入定位SDK获取经纬度****************/

    /**
     * 为了准确推荐本地新闻，NNewsFeedsSDK 内部需要获取经纬度。用户可以通过调用以下接口传入最新的经纬度：
     *
     * NNewsFeedsSDK.getInstance().setLocation(double longitude, double latitude);
     *
     * 由于经纬度的获取涉及面较广，所以我们推荐App层自己实现，SDK内部不主动读取经纬度。
     * 作为演示，本DEMO接入的是高德定位SDK，用户也可以使用其他定位SDK。
     */

    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        mLocationClient = new AMapLocationClient(this.getApplicationContext());
        mLocationOption = getDefaultOption();
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 设置定位监听
        mLocationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        // 设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 启动定位
        mLocationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        mLocationClient.stopLocation();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != mLocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mLocationClient.onDestroy();
            mLocationClient = null;
            mLocationOption = null;
        }
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    /**
                     * 将实时变化的经纬度传入 NNewsFeedsSDK
                     */
                    NNewsFeedsSDK.getInstance().setLocation(location.getLongitude(), location.getLatitude());
                    Log.v(TAG, "定位成功" + "\n");
                    Log.v(TAG, "定位类型: " + location.getLocationType() + "\n");
//                    Toast.makeText(SampleFeedsActivity.this, "经    度    : " + location.getLongitude() + "\n纬    度    : " + location.getLatitude(), Toast.LENGTH_LONG).show();
                    Log.v(TAG, "经    度    : " + location.getLongitude() + "\n");
                    Log.v(TAG, "纬    度    : " + location.getLatitude() + "\n");
                    Log.v(TAG, "精    度    : " + location.getAccuracy() + "米" + "\n");
                    Log.v(TAG, "提供者    : " + location.getProvider() + "\n");

                    Log.v(TAG, "速    度    : " + location.getSpeed() + "米/秒" + "\n");
                    Log.v(TAG, "角    度    : " + location.getBearing() + "\n");
                    // 获取当前提供定位服务的卫星个数
                    Log.v(TAG, "星    数    : " + location.getSatellites() + "\n");
                    Log.v(TAG, "国    家    : " + location.getCountry() + "\n");
                    Log.v(TAG, "省            : " + location.getProvince() + "\n");
                    Log.v(TAG, "市            : " + location.getCity() + "\n");
                    Log.v(TAG, "城市编码 : " + location.getCityCode() + "\n");
                    Log.v(TAG, "区            : " + location.getDistrict() + "\n");
                    Log.v(TAG, "区域 码   : " + location.getAdCode() + "\n");
                    Log.v(TAG, "地    址    : " + location.getAddress() + "\n");
                    Log.v(TAG, "兴趣点    : " + location.getPoiName() + "\n");
                } else {
                    //定位失败
                    Log.e(TAG, "定位失败" + "\n");
                    Log.e(TAG, "错误码:" + location.getErrorCode() + "\n");
                    Log.e(TAG, "错误信息:" + location.getErrorInfo() + "\n");
                    Log.e(TAG, "错误描述:" + location.getLocationDetail() + "\n");
                }

            } else {
                Log.e(TAG, "定位失败，loc is null");
            }
        }
    };

    /***************接入定位SDK获取经纬度****************/
}
