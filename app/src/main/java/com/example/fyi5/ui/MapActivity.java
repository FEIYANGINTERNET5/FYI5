package com.example.fyi5.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
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
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.example.fyi5.AppEnv;
import com.example.fyi5.R;

import java.util.List;

public class MapActivity extends AppCompatActivity implements View.OnClickListener {

    private MapView mMapView;
    private EditText mPoiEditText;
    private BaiduMap mBaiduMap;
    private LocationClient mLocationClient;

    private PoiSearch mPoiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        initialUI();


        startLocation();

    }

    private void initialUI() {
        mMapView = findViewById(R.id.baidu_map_view);
        mPoiEditText = findViewById(R.id.poi_edit);
    }

    private void startLocation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //定位初始化
                mLocationClient = new LocationClient(MapActivity.this);

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

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        mBaiduMap = mMapView.getMap();
        //开启交通图
        mBaiduMap.setTrafficEnabled(true);
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.baidu_map_point_btn:
                setPoint();
                break;
            case R.id.baidu_map_search_btn:
                pointSearch(mPoiEditText.getText().toString());
                break;
            case R.id.baidu_map_search_nearby_btn:
                nearbySearch(mPoiEditText.getText().toString());
                break;
            default:
                Log.d(AppEnv.TAG, "意料之外的触发");
                break;

        }
    }

    private void nearbySearch(String poi) {
        mPoiSearch = PoiSearch.newInstance();

        OnGetPoiSearchResultListener listener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                //PoiInfo 检索到的第一条信息
                List<PoiInfo> l = poiResult.getAllPoi();
                PoiInfo poiInfo = poiResult.getAllPoi().get(0);
                Log.d(AppEnv.TAG, poiResult.toString());
                //通过第一条检索信息对应的uid发起详细信息检索
                // uid的集合，最多可以传入10个uid，多个uid之间用英文逗号分隔
//                mPoiSearch.searchPoiDetail((new PoiDetailSearchOption()).poiUids(poiInfo.uid));
            }

            @Override
            public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

            }

            @Override
            public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
                PoiDetailInfo poiInfo = poiDetailSearchResult.getPoiDetailInfoList().get(0);
                Log.d(AppEnv.TAG, poiInfo.toString());
            }

            @Override
            public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

            }
        };

        mPoiSearch.setOnGetPoiSearchResultListener(listener);

        mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(new LatLng(39.915446, 116.403869))
                //单位：米
                .radius(10000)
                .keyword(poi)
                .pageNum(10));

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

    private void setPoint() {
        //定义Maker坐标点
        LatLng point = new LatLng(39.963175, 116.400244);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.mainpoint1);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option1 = new MarkerOptions()
                .position(point)
                .icon(bitmap);
        OverlayOptions option2 = new MarkerOptions()
                .position(new LatLng(39.99, 116.42))
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option1);
        mBaiduMap.addOverlay(option2);


        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            //marker被点击时回调的方法
            //若响应点击事件，返回true，否则返回false
            //默认返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getPosition().latitude == 39.99) {
                    Log.d(AppEnv.TAG, "点击事件1");
                }
                if (marker.getPosition().latitude == 39.963175) {
                    Log.d(AppEnv.TAG, "点击事件2");
                }
                Log.d(AppEnv.TAG, "点击事件");
                return true;
            }
        });

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