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
import com.github.jdsjlzx.interfaces.OnRefreshListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseFragment;
import dong.lan.base.ui.base.Config;
import dong.lan.map.service.LocationService;
import dong.lan.taste.R;
import dong.lan.taste.adapter.FeedsAdapter;
import dong.lan.taste.event.MarkerEvent;

/**
 附近食趣页面
 */

public class NearFeedFragment extends BaseFragment implements OnLoadMoreListener, BaseItemClickListener<AVOFeed>, OnRefreshListener {

    public static NearFeedFragment newInstance(String tittle, double latitude, double longitude) {
        NearFeedFragment fragment = new NearFeedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tittle", tittle);
        bundle.putDouble("latitude", latitude);
        bundle.putDouble("longitude", longitude);
        fragment.setArguments(bundle);
        return fragment;
    }


    private LRecyclerView nearFeedList;
    private FeedsAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(content == null){
            content = inflater.inflate(R.layout.fragment_near_feed,container,false);
            nearFeedList = (LRecyclerView) content.findViewById(R.id.near_feed_list);
            nearFeedList.setLayoutManager(new GridLayoutManager(getContext(),1));
            nearFeedList.setOnLoadMoreListener(this);
            nearFeedList.setPullRefreshEnabled(false);
            start(null);
        }
        return content;
    }


    int count = 0;
    int limit = 100;
    @Override
    public void onLoadMore() {
        AVQuery<AVOFeed> query = new AVQuery<>("Feed");
        BDLocation location = LocationService.service().getLastLocation();
        AVGeoPoint point = new AVGeoPoint();
        point.setLatitude(location.getLatitude());
        point.setLongitude(location.getLongitude());
        query.whereWithinKilometers("location", point, 50);
        query.limit(limit);
        query.skip(count);
        query.orderByAscending("like");
        query.include("labels");
        query.include("creator");
        query.include("creator.user");
        query.include("creator.avatar");
        //query.whereEqualTo("isPublic", true);
        query.findInBackground(new FindCallback<AVOFeed>() {
            @Override
            public void done(List<AVOFeed> list, AVException e) {
                if (e == null) {
                    count+=list.size();
                    toast("附近有 " + list.size() + " 个食趣");
                    if(list.size()<limit){
                        nearFeedList.setNoMore(true);
                    }
                    showNearFeed(list);
                } else {
                    dialog("获取附近的食趣失败，错误码：" + e.getCode());
                }
            }
        });
    }

    private void showNearFeed(List<AVOFeed> list) {
        if(adapter == null){
            adapter = new FeedsAdapter(list,this);
            nearFeedList.setAdapter(new LRecyclerViewAdapter(adapter));
        }else{
            adapter.loadMore(list);
        }
    }


    @Override
    public void start(Object data) {
        if(isAdded() && isStart){
            onLoadMore();
        }
        super.start(data);
    }

    @Override
    public void onClick(AVOFeed data, int action, int position) {

        Bundle bundle = new Bundle();
        bundle.putParcelable("data", data);
        bundle.putInt("type", Config.MARKER_TYPE_FEED);
        bundle.putString("info", data.getContent());
        MarkerEvent event = new MarkerEvent();
        event.latitude = data.getLocation().getLatitude();
        event.longitude = data.getLocation().getLongitude();
        event.data = bundle;
        event.isClearMap = true;

        EventBus.getDefault().post(event);


    }

    @Override
    public void onRefresh() {

    }
}
