package dong.lan.taste.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseFragment;
import dong.lan.taste.R;
import dong.lan.taste.adapter.FeedsAdapter;

/**
 * Created by 梁桂栋 on 2017/5/13.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class FeedFavoriteFragment extends BaseFragment implements OnLoadMoreListener, BaseItemClickListener<AVOFeed> {

    public static FeedFavoriteFragment newInstance(String tittle) {
        FeedFavoriteFragment fragment = new FeedFavoriteFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tittle", tittle);
        fragment.setArguments(bundle);
        return fragment;
    }


    private LRecyclerView nearFeedList;
    private FeedsAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(content == null){
            content = inflater.inflate(R.layout.fragment_favorite_feed,container,false);
            nearFeedList = (LRecyclerView) content.findViewById(R.id.favorite_feed_list);
            nearFeedList.setLayoutManager(new GridLayoutManager(getContext(),1));
            nearFeedList.setOnLoadMoreListener(this);
        }
        return content;
    }


    int count = 0;
    int limit = 100;
    @Override
    public void onLoadMore() {
        AVOUser user = AVOUser.getCurrentUser();

        AVQuery<AVOFeed> query = new AVQuery<>("Feed");
        query.include("labels");
        query.skip(count);
        query.limit(limit);
        query.whereEqualTo("likes", user);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<AVOFeed>() {
            @Override
            public void done(List<AVOFeed> list, AVException e) {
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        toast("无收藏");
                    } else {
                       showFeed(list);
                    }
                } else {
                    dialog("获取收藏失败，错误码：" + e.getCode());
                }
            }
        });
    }

    private void showFeed(List<AVOFeed> list) {
        int s = list.size();
        if(adapter == null){
            adapter = new FeedsAdapter(list,this);
            nearFeedList.setAdapter(new LRecyclerViewAdapter(adapter));
        }else{
            adapter.loadMore(list);
        }
        if(s<limit){
            nearFeedList.setLoadMoreEnabled(false);
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
        Intent feedIntent = new Intent(getContext(), FeedDetailActivity.class);
        feedIntent.putExtra("feed", data.toString());
        startActivity(feedIntent);
    }
}
