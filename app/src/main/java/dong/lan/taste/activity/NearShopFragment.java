package dong.lan.taste.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.blankj.ALog;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseFragment;
import dong.lan.base.ui.base.Config;
import dong.lan.map.service.LocationService;
import dong.lan.taste.R;
import dong.lan.taste.adapter.ShopAdapter;
import dong.lan.taste.event.MarkerEvent;

/**
 * Created by 梁桂栋 on 2017/5/13.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class NearShopFragment extends BaseFragment implements OnLoadMoreListener, BaseItemClickListener<PoiInfo>, OnRefreshListener {

    private OnGetPoiSearchResultListener poiSearchResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult result) {
            if (result.error != SearchResult.ERRORNO.NO_ERROR) {
                ALog.d(result.error);
            } else {
                List<PoiInfo> infos = result.getAllPoi();
                if (adapter == null) {
                    adapter = new ShopAdapter();
                    adapter.init(infos);
                    adapter.setClickListener(NearShopFragment.this);
                    shopList.setAdapter(new LRecyclerViewAdapter(adapter));
                } else {
                    adapter.loadMore(infos);
                }
                toast("查询到 " + infos.size() + " 个记录");
                if (isQuery) {
                    adapter.setCache(infos);
                    adapter.showCache(true);
                    shopList.refreshComplete(0);
                    for (PoiInfo info : infos) {
                        ALog.d(info.name);
                    }
                } else {
                    adapter.showCache(false);
                }
                if (infos.size() < size) {
                    shopList.setNoMore(true);
                    shopList.setLoadMoreEnabled(false);
                } else {
                    page++;
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {

        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

        }
    };

    private ShopAdapter adapter;

    public static NearShopFragment newInstance(String tittle, double latitude, double longitude) {
        NearShopFragment fragment = new NearShopFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tittle", tittle);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        fragment.setArguments(bundle);
        return fragment;
    }

    private LRecyclerView shopList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (content == null) {
            content = inflater.inflate(R.layout.fragment_near_shop, container, false);
            shopList = (LRecyclerView) content.findViewById(R.id.shop_list);
            shopList.setLayoutManager(new GridLayoutManager(getContext(), 1));
            shopList.setOnLoadMoreListener(this);
            shopList.setOnRefreshListener(this);
            start(null);
        }

        return content;
    }


    int page = 0;
    int size = 100;

    @Override
    public void onLoadMore() {
        BDLocation location = LocationService.service().getLastLocation();
        PoiSearch search = PoiSearch.newInstance();
        search.setOnGetPoiSearchResultListener(poiSearchResultListener);
        search.searchInCity(new PoiCitySearchOption().city(location.getCity()).keyword("美食").pageCapacity(size).pageNum(page));
    }

    @Override
    public void start(Object data) {
        if (isAdded() && isStart) {
            onLoadMore();
        }
        super.start(data);
    }

    @Override
    public void onClick(PoiInfo data, int action, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        bundle.putInt("type", Config.MARKER_TYPE_SHOP);
        bundle.putString("info", data.name);
        MarkerEvent event = new MarkerEvent();
        event.latitude = data.location.latitude;
        event.longitude = data.location.longitude;
        event.data = bundle;
        event.isClearMap = true;

        EventBus.getDefault().post(event);
    }

    @Override
    public void onRefresh() {
        isQuery = false;
        page = 0;
        adapter.showCache(false);
        shopList.refreshComplete(0);
    }

    boolean isQuery = false;

    public void query(String query) {
        ALog.d(query);
        isQuery = true;
        BDLocation location = LocationService.service().getLastLocation();
        PoiSearch search = PoiSearch.newInstance();
        search.setOnGetPoiSearchResultListener(poiSearchResultListener);
        search.searchInCity(new PoiCitySearchOption().city(location.getCity()).keyword(query).pageCapacity(size).pageNum(page));
    }
}
