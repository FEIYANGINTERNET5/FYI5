package com.example.fyi5.utils.overlayutil;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Polyline;
import com.example.fyi5.R;

public class MyWalkingRouteOverlay extends WalkingRouteOverlay {
    public MyWalkingRouteOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    @Override
    public BitmapDescriptor getStartMarker() {
        return BitmapDescriptorFactory.fromResource(R.drawable.icon_start_point);
    }

    @Override
    public BitmapDescriptor getTerminalMarker() {
        return BitmapDescriptorFactory.fromResource(R.drawable.icon_end_point);
    }

    @Override
    public int getLineColor() {
        return super.getLineColor();
    }

    @Override
    public boolean onRouteNodeClick(int i) {
        return super.onRouteNodeClick(i);
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        return super.onPolylineClick(polyline);
    }
}
