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

import dong.lan.avoscloud.bean.AVOShare;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.BaseFragment;
import dong.lan.taste.R;
import dong.lan.taste.adapter.SharesAdapter;

/**
 *用户的分享列表页面，显示的分享包括，自己发送给别人的，别人分享给自己的
 */

public class ShareListFragment extends BaseFragment implements OnLoadMoreListener {

    public static ShareListFragment newInstance(String tittle) {
        ShareListFragment fragment = new ShareListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tittle", tittle);
        fragment.setArguments(bundle);
        return fragment;
    }

    private LRecyclerView shareList;
    private SharesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (content == null) {
            content = inflater.inflate(R.layout.fragment_share_list, container, false);
            shareList = (LRecyclerView) content.findViewById(R.id.shareList);
            shareList.setLayoutManager(new GridLayoutManager(getContext(), 1));
            shareList.setOnLoadMoreListener(this);
            shareList.setPullRefreshEnabled(false);
            onLoadMore();
        }
        return content;
    }


    int count = 0;
    int limit = 100;

    @Override
    public void onLoadMore() {
        //获取所有与当前用户有关的分享内容

        AVQuery<AVOShare> query = new AVQuery<>("Share");
        query.whereEqualTo("likes", AVOUser.getCurrentUser());
        query.orderByDescending("updatedAt");
        query.include("creator");
        query.findInBackground(new FindCallback<AVOShare>() {
            @Override
            public void done(List<AVOShare> list, AVException e) {
                if (e == null) {
                    showNearUser(list);
                } else {
                    dialog("无分享，错误码：" + e.getCode());
                }
            }
        });
    }

    private void showNearUser(List<AVOShare> list) {
        if (adapter == null) {
            adapter = new SharesAdapter(list);
            shareList.setAdapter(new LRecyclerViewAdapter(adapter));
        }
    }


}
