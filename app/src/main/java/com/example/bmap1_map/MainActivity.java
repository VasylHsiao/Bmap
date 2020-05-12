package com.example.bmap1_map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.bmap1_map.service.LocationService;

public class MainActivity extends Activity {

    public MapView mMapView;
    public BaiduMap mMap;
    private LocationService locationService;
    private TextView LocationResult;
    private TextView LocationDiagnostic;
    private Button startLocation;
    private int locTimes = 0;//定位次数，用于控制地图更新动作（仅第一次调整中心和比例）


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location);

        // -----------demo view config ------------
        startLocation = (Button) findViewById(R.id.addfence);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMap = mMapView.getMap();//获取地图控件对象
        mMap.setMyLocationEnabled(true);//开启定位地图图层

        //获取locationservice实例
        locationService = ((Map) getApplication()).locationService;
        //注册监听
        locationService.registerListener(mListener);
        //设置定位参数
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();

        startLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (startLocation.getText().toString().equals(getString(R.string.startlocation))) {
                    locationService.start();// 定位SDK
                    locTimes = 0;//定位次数置零
                    startLocation.setText(getString(R.string.stoplocation));
                } else {
                    locationService.stop();
                    startLocation.setText(getString(R.string.startlocation));
                }
            }
        });
    }

    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }

            //仅当第一次定位，调整中心和比例
            locTimes++;
            if (locTimes == 1) {
                //构造地理坐标数据
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                //（以动画方式）改变地图状态（中心、倍数等）
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

            //构造定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            //设置定位数据，设置并显示定位蓝点
//            BitmapDescriptor BitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.location_marker);//自定义图标
            MyLocationConfiguration mLocationConfig = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
            mMap.setMyLocationConfiguration(mLocationConfig);
            mMap.setMyLocationData(locData);//显示定位蓝点
            //输出
            System.out.println("lat:" + location.getLatitude() + "\n" +
                    "lon:" + location.getLongitude() + "\n"
                    + "Radius:" + location.getRadius() + "\n"
                    + "Direc:" + location.getDirection() + "\n"
                    + "locTimes:" + locTimes + "\n"
                    + "Code:" + location.getLocType());
            if (location.getPoiList() == null) {
                System.out.println("失败！！！！！！！！！！！！！！！！！！");
            }

//            ||

//            //周边POI信息获取
//            Poi poi = location.getPoiList().get(0);
//            String poiName = poi.getName();    //获取POI名称
//            String poiTags = poi.getTags();    //获取POI类型
//            String poiAddr = poi.getAddr();    //获取POI地址
//
//            PoiRegion poiRegion = location.getPoiRegion();
//            String poiDerectionDesc = poiRegion.getDerectionDesc();    //获取PoiRegion位置关系
//            String poiRegionName = poiRegion.getName();    //获取PoiRegion名称
//            String poiReTags = poiRegion.getTags();    //获取PoiRegion类型
//
//            System.out.println(
//                     "poiDerectionDesc:" + poiDerectionDesc + "\n"
//                    + "poiRegionName:" + poiRegionName + "\n"
//                    + "poiReTags:" + poiReTags + "\n");
            //输出日志
//            Log.i("tag", "lat:" + location.getLatitude() + "\n" +
//                    "lon:" + location.getLongitude() + "\n"
//                    + "Radius:" + location.getRadius() + "\n"
//                    + "Direc:" + location.getDirection() + "\n"
//                    + "locTimes:" + locTimes);

//                int tag = 1;
//                StringBuffer sb = new StringBuffer(256);
//                sb.append("time : ");
//                /**
//                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
//                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
//                 */
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlongtitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nProvince : ");// 获取省份
//                sb.append(location.getProvince());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nTown : ");// 获取镇信息
//                sb.append(location.getTown());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nStreetNumber : ");// 获取街道号码
//                sb.append(location.getStreetNumber());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ");// POI信息
//                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
//                    for (int i = 0; i < location.getPoiList().size(); i++) {
//                        Poi poi = (Poi) location.getPoiList().get(i);
//                        sb.append("poiName:");
//                        sb.append(poi.getName() + ", ");
//                        sb.append("poiTag:");
//                        sb.append(poi.getTags() + "\n");
//                    }
//                }
//                if (location.getPoiRegion() != null) {
//                    sb.append("PoiRegion: ");// 返回定位位置相对poi的位置关系，仅在开发者设置需要POI信息时才会返回，在网络不通或无法获取时有可能返回null
//                    PoiRegion poiRegion = location.getPoiRegion();
//                    sb.append("DerectionDesc:"); // 获取POIREGION的位置关系，ex:"内"
//                    sb.append(poiRegion.getDerectionDesc() + "; ");
//                    sb.append("Name:"); // 获取POIREGION的名字字符串
//                    sb.append(poiRegion.getName() + "; ");
//                    sb.append("Tags:"); // 获取POIREGION的类型
//                    sb.append(poiRegion.getTags() + "; ");
//                    sb.append("\nSDK版本: ");
//                }
//                sb.append(locationService.getSDKVersion()); // 获取SDK版本
//                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
//                    sb.append("\nspeed : ");
//                    sb.append(location.getSpeed());// 速度 单位：km/h
//                    sb.append("\nsatellite : ");
//                    sb.append(location.getSatelliteNumber());// 卫星数目
//                    sb.append("\nheight : ");
//                    sb.append(location.getAltitude());// 海拔高度 单位：米
//                    sb.append("\ngps status : ");
//                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
//                    sb.append("\ndescribe : ");
//                    sb.append("gps定位成功");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
//                    // 运营商信息
//                    if (location.hasAltitude()) {// *****如果有海拔高度*****
//                        sb.append("\nheight : ");
//                        sb.append(location.getAltitude());// 单位：米
//                    }
//                    sb.append("\noperationers : ");// 运营商信息
//                    sb.append(location.getOperators());
//                    sb.append("\ndescribe : ");
//                    sb.append("网络定位成功");
//                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
//                    sb.append("\ndescribe : ");
//                    sb.append("离线定位成功，离线定位结果也是有效的");
//                } else if (location.getLocType() == BDLocation.TypeServerError) {
//                    sb.append("\ndescribe : ");
//                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
//                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
//                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
//                    sb.append("\ndescribe : ");
//                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
//                }
        }
    };


}