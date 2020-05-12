package com.example.bmap1_map.service;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiSearch;

public class PoiSearchService {
    private PoiSearch mPoiSearch = null;

    public PoiSearchService(OnGetPoiSearchResultListener listener) {
        //创建POI检索实例
        mPoiSearch = PoiSearch.newInstance();
        //注册POI检索监听器
        mPoiSearch.setOnGetPoiSearchResultListener(listener);
    }

    //城市内检索
    public boolean poiSearchCity(String city, String keyword, int pageNum) {
        return mPoiSearch.searchInCity(new PoiCitySearchOption()
                .city(city) //必填
                .keyword(keyword) //必填
                .pageNum(pageNum));
    }

    //周边检索
    public boolean poiSearchNearby(LatLng loc,int radius,String keyword,int pageNum){
        return mPoiSearch.searchNearby(new PoiNearbySearchOption()
                .location(loc)
                .radius(radius)
                .keyword(keyword)
                .pageNum(pageNum));
    }

    //矩形区域检索
    public void poiSearchBounds(LatLng loc1,LatLng loc2,String keyword){
        LatLngBounds searchBounds = new LatLngBounds.Builder()
                .include(loc1)
                .include(loc2)
                .build();
        mPoiSearch.searchInBound(new PoiBoundSearchOption()
                .bound(searchBounds)
                .keyword(keyword));
    }

    public PoiSearch getPoiOb(){
        return this.mPoiSearch;
    }
}
