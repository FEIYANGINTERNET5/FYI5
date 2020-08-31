package com.example.fyi5.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.fyi5.AppEnv;
import com.example.fyi5.HelpHelper;
import com.example.fyi5.R;

public class MainActivity extends AppCompatActivity {

    private EditText mEditText;
    private ImageView mBackIcon;
    private RelativeLayout mSearchLayout;
    private RelativeLayout mCurrentLocationLayout;
    private RelativeLayout mSettingMenuBtn;
    private RelativeLayout mSettingMenuLayout;
    private LinearLayout mSearchCoverLayout;
    private ImageView mOneKeyHelp;
    private MapView mMapView;
    private TextView mSearchBtn;
    private ImageView mPoiDetailImg;


    private boolean startOneKeyHelp = false;
    private boolean showSetting = false;

    private HelpHelper mHelpHelper;
    private LocationClient mLocationClient;

    private BaiduMap mMainBaiduMap;
    private PoiSearch mPoiSearch;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //onGetPoiDetailResult
                case 1:
                    showPOIDetail(msg.getData());
                    break;
            }
        }
    };

    private void showPOIDetail(Bundle data) {
        String address = data.getString("address");
        String distance = data.getString("distance");
        double lat = data.getDouble("lat");
        double lon = data.getDouble("lon");
        Log.d(AppEnv.TAG, address + distance);
        mMainBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, false, null));
        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng latLng = new LatLng(lat, lon);
        builder.target(latLng).zoom(18.0f);
        mMainBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.dashazi_marker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(new LatLng(lat, lon))
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mMainBaiduMap.addOverlay(option);

        mEditText.setVisibility(View.GONE);
        mSearchCoverLayout.setVisibility(View.GONE);
        mCurrentLocationLayout.setVisibility(View.VISIBLE);
        mSettingMenuBtn.setVisibility(View.VISIBLE);
        mPoiDetailImg.setVisibility(View.VISIBLE);
        mPoiDetailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AppEnv.TAG, "mPoiDetailImg onClicked...");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialUI();

        startLocation();

        //实例化HelpHelper
        mHelpHelper = new HelpHelper(this);

        //搜索框点击事件
        mSearchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch2SearchMode();
            }
        });

        //后退键点击事件
        mBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch2NormalMode();
            }
        });

        //一键报警点击事件
        mOneKeyHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOneKeyHelp = !startOneKeyHelp;
                if (startOneKeyHelp) {
                    mHelpHelper.startOneKeyHelp();
                } else {
                    mHelpHelper.stopOneKeyHelp();
                }
            }
        });

        //设置页点击事件
        mSettingMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingMenu();
            }
        });

        mCurrentLocationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.FOLLOWING, false, null));
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.zoom(16.0f);
                mMainBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        mMainBaiduMap = mMapView.getMap();
        //开启交通图
//        mBaiduMap.setTrafficEnabled(true);
        //开启城市热力图
//        mBaiduMap.setBaiduHeatMapEnabled(true);
        //开启地图的定位图层
        mMainBaiduMap.setMyLocationEnabled(true);
        //设置缩放按钮是否显示
        mMapView.showZoomControls(false);

        //设置MyLocation参数
        mMainBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, false, null));

        //设置初始地图级别：16，比例尺200米。
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(16.0f);
        mMainBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


        mMainBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                mMainBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, false, null));
            }
        });

        mMainBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mPoiDetailImg.setVisibility(View.GONE);
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });

        //poi搜索
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pointSearch(mEditText.getText().toString());
                Log.d(AppEnv.TAG, "start search...");
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mMainBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    private void pointSearch(String poi) {
        //创建POI检索实例
        mPoiSearch = PoiSearch.newInstance();

        //创建POI检索监听器
        OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //PoiInfo 检索到的第一条信息
                PoiInfo poiInfo = poiResult.getAllPoi().get(0);
                Log.d(AppEnv.TAG, poiInfo.toString());
                //通过第一条检索信息对应的uid发起详细信息检索
                // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔
                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUids(poiInfo.uid));
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                PoiDetailInfo poiInfo = poiDetailSearchResult.getPoiDetailInfoList().get(0);
                Log.d(AppEnv.TAG, poiInfo.toString());
                Message msg = new Message();
                Bundle mBundle = new Bundle();
                mBundle.putString("address", poiInfo.getAddress());
                mBundle.putString("distance", "1.7km");
                mBundle.putDouble("lat", poiInfo.getLocation().latitude);
                mBundle.putDouble("lon", poiInfo.getLocation().longitude);
                msg.what = 1;
                msg.setData(mBundle);
                handler.sendMessage(msg);
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            }

            //废弃
            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            }
        };

        //设置检索监听器
        mPoiSearch.setOnGetPoiSearchResultListener(listener);

        //设置PoiCitySearchOption，发起检索请求
        /**
         *  PoiCiySearchOption 设置检索属性
         *  city 检索城市
         *  keyword 检索内容关键字
         *  pageNum 分页页码
         */
        mPoiSearch.searchInCity(new PoiCitySearchOption()
                .city("北京") //必填
                .keyword(poi) //必填
                .pageNum(5));


    }

    private void startLocation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //定位初始化
                mLocationClient = new LocationClient(com.example.fyi5.ui.MainActivity.this);

                //通过LocationClientOption设置LocationClient相关参数
                LocationClientOption option = new LocationClientOption();
                option.setOpenGps(true); // 打开gps
                option.setCoorType("bd09ll"); // 设置坐标类型
                option.setScanSpan(1000);

                //设置locationClientOption
                mLocationClient.setLocOption(option);

                //注册LocationListener监听器
                MyLocationListener myLocationListener = new MyLocationListener();
                mLocationClient.registerLocationListener(myLocationListener);
                //开启地图定位图层
                mLocationClient.start();
            }
        }).start();
    }

    private void showSettingMenu() {
        showSetting = !showSetting;
        Log.d(AppEnv.TAG, "show setting:" + String.valueOf(showSetting));
        if (showSetting) {
            mSettingMenuLayout.setVisibility(View.VISIBLE);
        } else {
            mSettingMenuLayout.setVisibility(View.GONE);
        }
    }

    private void switch2NormalMode() {
        //TODO:过于暴力，需要后期迭代修改
        Log.d(AppEnv.TAG, "switch2NormalMode");
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void switch2SearchMode() {
        Log.d(AppEnv.TAG, "switch2SearchMode");
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        mEditText.requestFocusFromTouch();
        InputMethodManager inputManager = (InputMethodManager) mEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(mEditText, 0);
        mBackIcon.setImageResource(R.mipmap.main_back_icon);
        mSearchCoverLayout.setVisibility(View.VISIBLE);
        mCurrentLocationLayout.setVisibility(View.GONE);
        mSettingMenuLayout.setVisibility(View.GONE);
        mSettingMenuBtn.setVisibility(View.GONE);
    }

    private void initialUI() {
        mEditText = findViewById(R.id.main_search_edit);
        mBackIcon = findViewById(R.id.main_back_icon);
        mSearchLayout = findViewById(R.id.main_search_layout);
        mSearchCoverLayout = findViewById(R.id.main_search_cover_layout);
        mCurrentLocationLayout = findViewById(R.id.main_current_location);
        mSettingMenuBtn = findViewById(R.id.main_setting_btn);
        mSettingMenuLayout = findViewById(R.id.main_setting_menu);
        mOneKeyHelp = findViewById(R.id.main_one_key_help);
        mMapView = findViewById(R.id.main_map_view);
        mSearchBtn = findViewById(R.id.main_search_btn);
        mPoiDetailImg = findViewById(R.id.poi_detail_img);
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mMainBaiduMap.setMyLocationData(locData);
        }
    }

}