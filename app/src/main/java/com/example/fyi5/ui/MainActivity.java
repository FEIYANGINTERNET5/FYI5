package com.example.fyi5.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    private Button mToHereBtn;
    private Button mCommentBtn;
    private ImageView mRouteDetailImg;
    private RelativeLayout mCombineBarLayout;
    private RelativeLayout mFanceLayout;
    private RelativeLayout mFindRouteLayout;
    private RelativeLayout mIntelligentHelpLayout;
    private TextView mSettingText;


    private boolean startOneKeyHelp = false;
    private boolean showSetting = false;

    private HelpHelper mHelpHelper;
    private SmsManager smsManager;
    private LocationClient mLocationClient;

    private BaiduMap mBaiduMap;
    private PoiSearch mPoiSearch;

    private final static String fileName0 = "region_0_53.json";
    private final static String fileName1 = "region_1_7.json";
    private String addStr0;
    private String addStr1;
    private long lastKeyDownTime = 0;
    private int keyDownCount = 0;

    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final String action = intent.getAction();
            if (Intent.ACTION_SCREEN_OFF.equals(action) || Intent.ACTION_SCREEN_ON.equals(action)) {
                if (lastKeyDownTime == 0) {
                    lastKeyDownTime = System.currentTimeMillis();
                }
                if (System.currentTimeMillis() - lastKeyDownTime < 4000) {
                    keyDownCount++;
                } else {
                    lastKeyDownTime = 0;
                    keyDownCount = 0;
                }
                if (keyDownCount == 3) {
                    mHelpHelper.intelligentHelp();
                    Log.d(AppEnv.TAG, "按键报警逻辑触发");
                }
                Log.d(AppEnv.TAG, action + "  keyDownCount:" + keyDownCount + "  lastKeyDownTime" + lastKeyDownTime);
            }
        }
    };

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //json
                case 0:
                    showMarkers();
                    break;
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
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, false, null));
        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng latLng = new LatLng(lat, lon);
        builder.target(latLng).zoom(18.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.dashazi_marker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(new LatLng(40.001964, 116.486945))
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);

        mEditText.setVisibility(View.GONE);
        mSearchCoverLayout.setVisibility(View.GONE);
        mCurrentLocationLayout.setVisibility(View.VISIBLE);
        mSettingMenuBtn.setVisibility(View.VISIBLE);
        mPoiDetailImg.setVisibility(View.VISIBLE);
        mCommentBtn.setVisibility(View.VISIBLE);
        mToHereBtn.setVisibility(View.VISIBLE);

        mToHereBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch2FindRouteMode();
            }
        });

        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPoiDetailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(AppEnv.TAG, "mPoiDetailImg onClicked...");
            }
        });
    }

    private void switch2FindRouteMode() {
        Log.d(AppEnv.TAG, "switch2FindRouteMode");
        drawLines();
        mSearchLayout.setVisibility(View.GONE);
        mCurrentLocationLayout.setVisibility(View.GONE);
        mSettingMenuLayout.setVisibility(View.GONE);
        mFindRouteLayout.setVisibility(View.VISIBLE);
        mPoiDetailImg.setVisibility(View.GONE);
        mToHereBtn.setVisibility(View.GONE);
        mCommentBtn.setVisibility(View.GONE);
        mRouteDetailImg.setVisibility(View.VISIBLE);

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
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.FOLLOWING, false, null));
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.zoom(16.0f);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        });

        mFanceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch2FanceMode();
            }
        });

        mIntelligentHelpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHelpHelper.intelligentHelp();
            }
        });

        mSettingText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent jumpIntent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(jumpIntent);
            }
        });


        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mBatInfoReceiver, filter);

    }


    private void switch2FanceMode() {
        drawFance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        mBaiduMap = mMapView.getMap();
        //开启交通图
//        mBaiduMap.setTrafficEnabled(true);
        //开启城市热力图
//        mBaiduMap.setBaiduHeatMapEnabled(true);
        //开启地图的定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //设置缩放按钮是否显示
        mMapView.showZoomControls(false);

        //设置MyLocation参数
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.FOLLOWING, false, null));

        //设置初始地图级别：16，比例尺200米。
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(16.0f);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));


        mBaiduMap.setOnMapTouchListener(new BaiduMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent motionEvent) {
                mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.NORMAL, false, null));
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
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

        parseJson();

    }

    private void parseJson() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                addStr0 = getJson(fileName0);
                addStr1 = getJson(fileName1);
            }
        }).start();

        Message msg = Message.obtain();
        msg.what = 0;
        Bundle bundle = new Bundle();
        bundle.putString("addStr0", addStr0);
        bundle.putString("addStr1", addStr1);
        handler.sendMessage(msg);

    }

    private void showMarkers() {

        try {
            JSONArray jsonArray = new JSONArray(addStr0);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                double lat = jsonObject.getDouble("Y");
                double lon = jsonObject.getDouble("X");
                LatLng point = new LatLng(lat, lon);
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.mainpoint_0);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                mBaiduMap.addOverlay(option);
//                person.setId(i+"");
//                person.setName(jsonObject.getString("name"));
//                person.setAge(jsonObject.getString("age"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray(addStr1);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                double lat = jsonObject.getDouble("Y");
                double lon = jsonObject.getDouble("X");
                LatLng point = new LatLng(lat, lon);
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.mainpoint_1);
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap);
                mBaiduMap.addOverlay(option);
//                person.setId(i+"");
//                person.setName(jsonObject.getString("name"));
//                person.setAge(jsonObject.getString("age"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getJson(String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
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
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();

        if (mBatInfoReceiver != null) {
            try {
                unregisterReceiver(mBatInfoReceiver);
            } catch (Exception e) {
                Log.e(AppEnv.TAG, "unregisterReceiver mBatInfoReceiver failure :" + e.getCause());
            }
        }

    }

    private void switch2RouteMode() {
        mRouteDetailImg.setVisibility(View.VISIBLE);
    }

    private void drawLines() {
        List<LatLng> points = new ArrayList<LatLng>();
        //构建折线点坐标
        //1
        LatLng p1 = new LatLng(39.9885789080103, 116.49745071996887);
        LatLng p2 = new LatLng(39.98856508780282, 116.49666021112009);
        LatLng p3 = new LatLng(39.98851267591795, 116.4963907194671);
        points.add(p1);
        points.add(p2);
        points.add(p3);
        drawLine(points, 1, true);

        //0
        LatLng p31 = new LatLng(39.98851267591795, 116.4963907194671);
        LatLng p4 = new LatLng(39.99026495226589, 116.4963547872467);
        points.clear();
        points.add(p31);
        points.add(p4);
        drawLine(points, 0, true);

        //1
        LatLng p41 = new LatLng(39.99026495226589, 116.4963547872467);
        LatLng p5 = new LatLng(39.993934026546846, 116.4960134311529);
        points.clear();
        points.add(p41);
        points.add(p5);
        drawLine(points, 1, true);

        //2
        LatLng p51 = new LatLng(39.993934026546846, 116.4960134311529);
        LatLng p6 = new LatLng(39.99509482223617, 116.49444139651045);
        points.clear();
        points.add(p51);
        points.add(p6);
        drawLine(points, 2, true);

        //1
        LatLng p61 = new LatLng(39.99509482223617, 116.49444139651045);
        LatLng p7 = new LatLng(39.99746471835229, 116.49096495418684);
        points.clear();
        points.add(p61);
        points.add(p7);
        drawLine(points, 1, true);

        //0
        LatLng p71 = new LatLng(39.99746471835229, 116.49096495418684);
        LatLng p8 = new LatLng(39.99801054273875, 116.49011156395235);
        points.clear();
        points.add(p71);
        points.add(p8);
        drawLine(points, 0, true);

        //1
        LatLng p81 = new LatLng(39.99801054273875, 116.49011156395235);
        LatLng p9 = new LatLng(39.999889803193014, 116.49033614032984);
        points.clear();
        points.add(p81);
        points.add(p9);
        drawLine(points, 1, true);

        //0
        LatLng p91 = new LatLng(39.999889803193014, 116.49033614032984);
        LatLng p10 = new LatLng(40.0013544847498, 116.49016546228295);
        LatLng p11 = new LatLng(40.00272932211247, 116.48950071620557);
        points.clear();
        points.add(p91);
        points.add(p10);
        points.add(p11);
        drawLine(points, 0, true);

        //1
        LatLng p111 = new LatLng(40.00272932211247, 116.48950071620557);
        LatLng p12 = new LatLng(40.00270859612826, 116.48937495343417);
        points.clear();
        points.add(p111);
        points.add(p12);
        drawLine(points, 1, true);

        //0
        LatLng p121 = new LatLng(40.00270859612826, 116.48937495343417);
        LatLng p13 = new LatLng(40.00276386540545, 116.48927613982806);
        LatLng p122 = new LatLng(40.001964, 116.486945);
        points.clear();
        points.add(p121);
        points.add(p13);
        points.add(p122);
        drawLine(points, 0, true);

        //1
        LatLng p82 = new LatLng(39.99801054273875, 116.49011156395235);
        LatLng p92 = new LatLng(40.001674, 116.484393);
        LatLng p102 = new LatLng(40.002863, 116.485417);
        LatLng p112 = new LatLng(40.002545, 116.486136);
        points.clear();
        points.add(p82);
        points.add(p92);
        points.add(p102);
        points.add(p112);
        drawLine(points, 1, false);

        //0
        LatLng p1121 = new LatLng(40.002545, 116.486136);
        LatLng p1221 = new LatLng(40.001964, 116.486945);
        points.clear();
        points.add(p1121);
        points.add(p1221);
        drawLine(points, 0, false);


    }

    private void drawLine(List<LatLng> points, int level, boolean b) {
        int color = 0;
        switch (level) {
            case 0:
                color = getResources().getColor(R.color.route_level_0);
                break;
            case 1:
                color = getResources().getColor(R.color.route_level_1);
                break;
            case 2:
                color = getResources().getColor(R.color.route_level_2);
                break;
            default:
                break;
        }
        int width = b ? 15 : 8;
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(width)
                .color(color)
                .points(points);
        //在地图上绘制折线
        //mPloyline 折线对象
        Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);
    }

    private void drawFance() {
        //多边形顶点位置
        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(40.002098, 116.47896));
        points.add(new LatLng(39.98458, 116.481763));
        points.add(new LatLng(39.989007, 116.50403));
        points.add(new LatLng(39.999615, 116.50785));
        points.add(new LatLng(40.009315, 116.493297));

        //构造PolygonOptions
        PolygonOptions mPolygonOptions = new PolygonOptions().points(points)
                //填充颜色
                .fillColor(0x2601C2CE);

        //在地图上显示多边形
        mBaiduMap.addOverlay(mPolygonOptions);
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
                Message msg = Message.obtain();
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
        mPoiDetailImg = findViewById(R.id.poi_detail_image);
        mRouteDetailImg = findViewById(R.id.route_detail_image);
        mCombineBarLayout = findViewById(R.id.main_combine_bar);
        mFanceLayout = findViewById(R.id.main_fance_btn);
        mToHereBtn = findViewById(R.id.main_to_here_btn);
        mCommentBtn = findViewById(R.id.main_comment_btn);
        mFindRouteLayout = findViewById(R.id.start_end_layout);
        mIntelligentHelpLayout = findViewById(R.id.main_intelligent_help);
        mSettingText = findViewById(R.id.main_fun_setting_text);
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
            mBaiduMap.setMyLocationData(locData);
        }
    }

}