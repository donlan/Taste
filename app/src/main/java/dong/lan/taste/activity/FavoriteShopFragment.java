package dong.lan.taste.activity;

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

import dong.lan.avoscloud.bean.AVOShop;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseFragment;
import dong.lan.taste.R;
import dong.lan.taste.adapter.FavoriteShopAdapter;

/**
 * Created by 梁桂栋 on 2017/5/13.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class FavoriteShopFragment extends BaseFragment implements OnLoadMoreListener, BaseItemClickListener<AVOShop> {


    private FavoriteShopAdapter adapter;

    public static FavoriteShopFragment newInstance(String tittle) {
        FavoriteShopFragment fragment = new FavoriteShopFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tittle", tittle);
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
            onLoadMore();
        }
        return content;
    }


    int count = 0;
    int limit = 100;

    @Override
    public void onLoadMore() {
        AVOUser user = AVOUser.getCurrentUser();

        AVQuery<AVOShop> query = new AVQuery<>("Shop");
        query.skip(count);
        query.include("likes");
        query.limit(limit);
        query.whereEqualTo("likes", user);
        query.orderByDescending("updatedAt");
        query.findInBackground(new FindCallback<AVOShop>() {
            @Override
            public void done(List<AVOShop> list, AVException e) {
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        toast("无店铺收藏");
                    } else {
                        showFeed(list);
                    }
                } else {
                    dialog("获取店铺收藏失败，错误码：" + e.getCode());
                }
            }
        });
    }

    private void showFeed(List<AVOShop> list) {
        int s = list.size();
        if (adapter == null) {
            adapter = new FavoriteShopAdapter();
            adapter.loadMore(list);
            adapter.setClickListener(this);
            shopList.setAdapter(new LRecyclerViewAdapter(adapter));
        } else {
            adapter.loadMore(list);
        }
        if (s < limit) {
            shopList.setLoadMoreEnabled(false);
        }
    }

    @Override
    public void onClick(AVOShop data, int action, int position) {

    }


}
