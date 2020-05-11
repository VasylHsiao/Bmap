package com.example.bmap1_map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends Activity {
    private MapView mMapView = null;
    BaiduMap mMap;
    public LocationClient mLocationClient = null;
    private MyLocationListener myListener = null;
    double mCurrentLat;
    double mCurrentLon;
    float mCurrentAccracy;
    float mCurrentDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMap = mMapView.getMap();
        mMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);//开启定位图层
        View child = mMapView.getChildAt(1);
        if (child != null) {
            child.setVisibility(View.INVISIBLE);
        }

        //初始化LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener(myListener);//注册监听函数
        //配置参数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，设置定位模式，默认高精度
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        option.setOpenGps(true);
        option.setLocationNotify(true);
        option.setIgnoreKillProcess(false);//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.setIsNeedAddress(true);//可选，是否需要地址信息，默认为不需要，即参数为false
        option.setNeedNewVersionRgc(true);
        option.setIsNeedLocationDescribe(true);
        mLocationClient.setLocOption(option);//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用

        mLocationClient.start();//调用LocationClient的start()方法，便可发起定位请求


    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
        mLocationClient.restart();//调用LocationClient的start()方法，便可发起定位请求
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

//            double latitude = location.getLatitude();    //获取纬度信息
//            double longitude = location.getLongitude();    //获取经度信息
//            float radius = location.getRadius();    //获取定位精度，默认值为0.0f
//            String addr = location.getAddrStr();    //获取详细地址信息
//            String country = location.getCountry();    //获取国家
//            String province = location.getProvince();    //获取省份
//            String city = location.getCity();    //获取城市
//            String district = location.getDistrict();    //获取区县
//            String street = location.getStreet();    //获取街道信息
//            String adcode = location.getAdCode();    //获取adcode
//            String town = location.getTown();    //获取乡镇信息
//            String locationDescribe = location.getLocationDescribe();    //获取位置描述信息
//            String coorType = location.getCoorType();
//            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
//
//            int errorCode = location.getLocType();
//            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明

            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            //构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            //设置定位数据，显示定位蓝点
            mMap.setMyLocationData(locData);

            //构造地理坐标数据
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            //（以动画方式）改变地图状态（中心、倍数等）
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(17.0f);
            mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            mCurrentDirection = location.getDirection();
            Log.i("tag", "lat:" + mCurrentLat + "\n" +
                    "lon:" + mCurrentLon + "\n" + "Radius:" + mCurrentAccracy + "\n" + "Direc:" + mCurrentDirection);
        }
    }
}
