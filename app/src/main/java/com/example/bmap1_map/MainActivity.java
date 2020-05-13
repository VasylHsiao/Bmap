package com.example.bmap1_map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.location.PoiRegion;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.bmap1_map.service.LocationService;
import com.example.bmap1_map.service.PoiSearchService;

public class MainActivity extends Activity {

    public MapView mMapView;
    public BaiduMap mMap;
    private LocationService locationService;
    private TextView LocationResult;
    private TextView LocationDiagnostic;
    private Button startLocation;
    private int locTimes = 0;//定位次数，用于控制地图更新动作（仅第一次调整中心和比例）
    private PoiSearchService poiSearchService;
    private EditText mEditRadius;
    private RelativeLayout mPoiDetailView;
    private ListView mPoiList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poinearbysearch);

        // -----------demo view config ------------
        LocationResult = (TextView) findViewById(R.id.textView);//定位结果展示栏
        mEditRadius = (EditText) findViewById(R.id.edit_radius);//半径输入栏
        startLocation = (Button) findViewById(R.id.loc_search);//按钮
        mMapView = (MapView) findViewById(R.id.bmapView);//地图
        mMap = mMapView.getMap();//获取地图控件对象
        mMap.setMyLocationEnabled(true);//开启定位地图图层
        mPoiDetailView = (RelativeLayout) findViewById(R.id.poi_detail);
        mPoiList = (ListView) findViewById(R.id.poi_list);//POI检索信息展示

        //获取locationservice实例
        locationService = ((Map) getApplication()).locationService;
        //注册监听
        locationService.registerListener(mListener);
        //设置定位参数
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        //开始定位
        locationService.start();// 定位SDK
        locTimes = 0;//定位次数置零

        //创建POI检索实例并注册监听
//        poiSearchService = new PoiSearchService(poiListener);
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
                if (startLocation.getText().toString().equals(getString(R.string.startsearch))) {
                    //开始检索

                    startLocation.setText(getString(R.string.stopsearch));
                } else {
                    //停止检索

                    startLocation.setText(getString(R.string.startsearch));
                }
            }
        });
    }

    //更新textview
    public void updateMap(final String str) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationResult.post(new Runnable() {
                    @Override
                    public void run() {
                        LocationResult.setText(str);
                    }
                });
            }
        }).start();
    }

    //实现定位监听
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
                builder.target(ll).zoom(17.0f);
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
            mMap.setMyLocationData(locData);//显示定位蓝点、

            //检测定位结果
            StringBuffer sb = new StringBuffer(256);
            sb.append("结果：\n");
            sb.append("lat:");
            sb.append(location.getLatitude() + "\n");
            sb.append("lon:");
            sb.append(location.getLongitude() + "\n");
            sb.append("Radius:");
            sb.append(location.getRadius() + "\n");
            sb.append("Direc:");
            sb.append(location.getDirection() + "\n");
            sb.append("locTimes:");
            sb.append(locTimes + "\n");
            sb.append("Code:");
            sb.append(location.getLocType() + "\n");
            //检测POI获取结果
            if (location.getPoiList() == null || location.getPoiList().isEmpty()) {
                System.out.println("POI为空！！！！！！！！！！！！！！！！！！");
            } else {
                for (int i = 0; i < location.getPoiList().size(); i++) {
                    Poi poi = (Poi) location.getPoiList().get(i);
                    sb.append("poiName:");
                    sb.append(poi.getName() + ", ");
                    sb.append("poiTag:");
                    sb.append(poi.getTags() + "\n");
                }
            }
            if (location.getPoiRegion() == null) {
                sb.append("Region为空！！！！！！！！！！！！！！！\n");
            } else {
                sb.append("PoiRegion ");// 返回定位位置相对poi的位置关系，仅在开发者设置需要POI信息时才会返回，在网络不通或无法获取时有可能返回null
                PoiRegion poiRegion = location.getPoiRegion();
                sb.append("DerectionDesc:"); // 获取POIREGION的位置关系
                sb.append(poiRegion.getDerectionDesc() + "; ");
                sb.append("Name:"); // 获取POIREGION的名字字符串
                sb.append(poiRegion.getName() + "; ");
                sb.append("Tags:"); // 获取POIREGION的类型
                sb.append(poiRegion.getTags() + "; ");
                sb.append("\nSDK版本: ");
            }

            //更新textview
            updateMap(sb.toString());


            //开始POI检索
//            int radius = Integer.parseInt(mEditRadius.getText().toString());
//            String keyword = "停车场";
//            int PageNum = 10;
//            if (poiSearchService.poiSearchNearby(new LatLng(location.getLatitude(), location.getLongitude()), radius, keyword, 0)) {
//            }
        }
    };

    //实现POI检索监听
//    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
//        @Override
//        public void onGetPoiResult(final PoiResult result) {
//        }
//
//        @Override
//        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
//
//        }
//
//        @Override
//        public void onGetPoiDetailResult(PoiDetailSearchResult poiDetailSearchResult) {
//
//        }
//
//        @Override
//        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
//
//        }
//    };

//    private class MyPoiOverlay extends PoiOverlay {
//        MyPoiOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//
//        @Override
//        public boolean onPoiClick(int index) {
//            super.onPoiClick(index);
//            PoiInfo poi = getPoiResult().getAllPoi().get(index);
//            Toast.makeText(MainActivity.this, poi.address, Toast.LENGTH_LONG).show();
//            return true;
//        }
//    }


}