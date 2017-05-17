
package dong.lan.taste.mvp.contract;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.IActivityFunc;
import dong.lan.base.ui.ProgressView;

/**
 */

public interface MainMapContract {
    public interface View extends ProgressView,IActivityFunc {
        void showNearUser(List<AVOUser> users);

        void showNearFeed(List<AVOFeed> feeds);
    }

    public interface Presenter {
        void likeShop(PoiInfo data);

        void saveUserLocation();

        void queryRoute(LatLng position, BaiduMap baiduMap);
    }

    public interface Model {
    }
}
