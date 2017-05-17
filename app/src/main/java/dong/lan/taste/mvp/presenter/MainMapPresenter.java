
package dong.lan.taste.mvp.presenter;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.BikingRouteOverlay;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.blankj.ALog;

import java.util.ArrayList;
import java.util.List;

import dong.lan.avoscloud.bean.AVOShop;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.map.service.LocationService;
import dong.lan.taste.mvp.contract.MainMapContract;

/**
 */

public class MainMapPresenter implements MainMapContract.Presenter {

    private MainMapContract.View view;
    private BNRoutePlanNode.CoordinateType coType = BNRoutePlanNode.CoordinateType.BD09LL;

    public MainMapPresenter(MainMapContract.View view) {
        this.view = view;
    }

    private RoutePlanSearch planSearch;
    private WalkingRouteResult nowResultwalk;

    /**
     * 收藏店铺
     *
     * @param data
     */
    @Override
    public void likeShop(final PoiInfo data) {
        if (data != null) {
            AVQuery<AVOShop> query = new AVQuery<>("Shop");
            query.whereEqualTo("uid", data.uid);
            query.findInBackground(new FindCallback<AVOShop>() {
                @Override
                public void done(List<AVOShop> list, AVException e) {
                    if (e == null) {
                        if (list == null || list.isEmpty()) {
                            final AVOShop avoShop = new AVOShop();
                            avoShop.setAddress(data.address);
                            avoShop.setName(data.name);
                            avoShop.setUid(data.uid);
                            avoShop.setPhone(data.phoneNum);
                            avoShop.setLocation(data.location.latitude,data.location.longitude);
                            avoShop.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null)
                                        avoShop.addLike(AVOUser.getCurrentUser());
                                }
                            });
                        } else {
                            list.get(0).addLike(AVOUser.getCurrentUser());
                        }
                    } else {
                        view.toast("收藏店铺失败，错误码：" + e.getCode());
                    }
                }
            });
        }
    }

    @Override
    public void saveUserLocation() {
        AVOUser user = AVOUser.getCurrentUser();
        BDLocation location = LocationService.service().getLastLocation();
        if (user != null && location != null) {
            user.setLastLocation(location.getLatitude(), location.getLongitude());
            user.saveInBackground();
        }
    }

    @Override
    public void queryRoute(LatLng position, final BaiduMap baiduMap) {
        BDLocation location = LocationService.service().getLastLocation();
        if (location == null)
            return;
        if (planSearch == null) {
            planSearch = RoutePlanSearch.newInstance();
            planSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
                @Override
                public void onGetWalkingRouteResult(WalkingRouteResult result) {
                    if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR && !result.getRouteLines().isEmpty()) {
                        result.getRouteLines().get(0);
                        WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    } else {
                        view.toast("无步行路线");
                    }
                }

                @Override
                public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
                    ALog.d(transitRouteResult);
                }

                @Override
                public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {
                    ALog.d(massTransitRouteResult);
                }

                @Override
                public void onGetDrivingRouteResult(DrivingRouteResult result) {
                    if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR && !result.getRouteLines().isEmpty()) {
                        result.getRouteLines().get(0);
                        DrivingRouteOverlay overlay = new DrivingRouteOverlay(baiduMap);
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    } else {
                        view.toast("无驾车路线");
                    }
                }

                @Override
                public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {
                    ALog.d(indoorRouteResult);
                }

                @Override
                public void onGetBikingRouteResult(BikingRouteResult result) {
                    if (result != null && result.error == SearchResult.ERRORNO.NO_ERROR && !result.getRouteLines().isEmpty()) {
                        result.getRouteLines().get(0);
                        BikingRouteOverlay overlay = new BikingRouteOverlay(baiduMap);
                        overlay.setData(result.getRouteLines().get(0));
                        overlay.addToMap();
                        overlay.zoomToSpan();
                    } else {
                        view.toast("无骑行路线");
                    }
                }
            });
        }
        BNRoutePlanNode sNode = new BNRoutePlanNode(location.getLongitude(), location.getLatitude(), "起点", null, coType);
        BNRoutePlanNode eNode = new BNRoutePlanNode(position.longitude, position.latitude, "终点", null, coType);

        List<BNRoutePlanNode> list = new ArrayList<>(2);
        list.add(sNode);
        list.add(eNode);

        PlanNode sPlanNode = PlanNode.withLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        PlanNode ePlanNode = PlanNode.withLocation(position);

        DrivingRoutePlanOption drivingRoutePlanOption = new DrivingRoutePlanOption();
        drivingRoutePlanOption.from(sPlanNode);
        drivingRoutePlanOption.to(ePlanNode);
        WalkingRoutePlanOption option = new WalkingRoutePlanOption();
        option.from(sPlanNode);
        option.to(ePlanNode);
        BikingRoutePlanOption bikingRoutePlanOption = new BikingRoutePlanOption();
        bikingRoutePlanOption.from(sPlanNode);
        bikingRoutePlanOption.to(ePlanNode);

        planSearch.bikingSearch(bikingRoutePlanOption);
        planSearch.walkingSearch(option);
        planSearch.drivingSearch(drivingRoutePlanOption);
    }


}
