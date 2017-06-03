package dong.lan.taste.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.location.BDLocation;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseFragment;
import dong.lan.base.ui.base.Config;
import dong.lan.map.service.LocationService;
import dong.lan.taste.R;
import dong.lan.taste.adapter.UserAdapter;
import dong.lan.taste.event.MarkerEvent;

/**
 * 附近的用户页面
 */

public class NearUserFragment extends BaseFragment implements OnLoadMoreListener, BaseItemClickListener<AVOUser> {

    public static NearUserFragment newInstance(String tittle, double latitude, double longitude) {
        NearUserFragment fragment = new NearUserFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tittle", tittle);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        fragment.setArguments(bundle);
        return fragment;
    }

    private LRecyclerView nearUserList;
    private UserAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (content == null) {
            content = inflater.inflate(R.layout.fragment_near_feed, container, false);
            nearUserList = (LRecyclerView) content.findViewById(R.id.near_feed_list);
            nearUserList.setLayoutManager(new GridLayoutManager(getContext(), 1));
            nearUserList.setOnLoadMoreListener(this);
            nearUserList.setPullRefreshEnabled(false);
            start(null);
        }
        return content;
    }


    int count = 0;
    int limit = 100;

    @Override
    public void onLoadMore() {
        BDLocation location = LocationService.service().getLastLocation();
        if (location == null) {
            toast("无法获取当前位置信息");
            return;
        }
        //获取附近10公里范围内的用户
        AVQuery<AVOUser> query = new AVQuery<>("MyUser");
        AVGeoPoint point = new AVGeoPoint();
        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        query.whereWithinKilometers("lastLocation", point, 50);
        query.limit(100);
        query.include("avatar");
        query.include("user");
        // query.whereEqualTo("shareLoc",true);
        query.whereNotEqualTo("objectId", AVOUser.getCurrentUser().getObjectId());
        query.findInBackground(new FindCallback<AVOUser>() {
            @Override
            public void done(List<AVOUser> list, AVException e) {
                if (e == null && list!=null) {
                    toast("附近有 " + list.size() + " 个食友");
                    showNearUser(list);
                } else {
                    dialog("找不到附近的用户，错误码：" + e.getCode());
                }
            }
        });
    }

    private void showNearUser(List<AVOUser> list) {
        if (adapter == null) {
            adapter = new UserAdapter(list, this);
            nearUserList.setAdapter(new LRecyclerViewAdapter(adapter));
        }
    }


    @Override
    public void start(Object data) {
        if (isAdded() && isStart) {
            onLoadMore();
        }
        super.start(data);
    }

    @Override
    public void onClick(AVOUser data, int action, int position) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        bundle.putInt("type", Config.MARKER_TYPE_USER);
        bundle.putString("info", data.getDisplayName());
        MarkerEvent event = new MarkerEvent();
        event.latitude = data.getLastLocation().getLatitude();
        event.longitude = data.getLastLocation().getLongitude();
        event.data = bundle;
        event.isClearMap = true;

        EventBus.getDefault().post(event);
    }

}
