package dong.lan.taste.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationQuery;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMConversationQueryCallback;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.List;

import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseFragment;
import dong.lan.base.ui.base.Config;
import dong.lan.taste.App;
import dong.lan.taste.R;
import dong.lan.taste.adapter.ConversationAdapter;

/**
 * Created by 梁桂栋 on 2017/5/13.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */

public class ConversationFragment extends BaseFragment implements OnLoadMoreListener, BaseItemClickListener<AVIMConversation> {

    public static ConversationFragment newInstance(String tittle) {
        ConversationFragment fragment = new ConversationFragment();
        Bundle bundle = new Bundle();
        bundle.putString("tittle", tittle);
        fragment.setArguments(bundle);
        return fragment;
    }


    private LRecyclerView nearFeedList;
    private ConversationAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(content == null){
            content = inflater.inflate(R.layout.fragment_near_feed,container,false);
            nearFeedList = (LRecyclerView) content.findViewById(R.id.near_feed_list);
            nearFeedList.setLayoutManager(new GridLayoutManager(getContext(),1));
//            nearFeedList.setOnLoadMoreListener(this);
            start(null);
        }
        return content;
    }


    int count = 0;
    int limit = 100;
    @Override
    public void onLoadMore() {
        AVIMConversationQuery query = App.myApp().getAvimClient().getQuery();
        query.limit(limit)
                .setSkip(count)
                .setQueryPolicy(AVQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findInBackground(new AVIMConversationQueryCallback() {
            @Override
            public void done(List<AVIMConversation> list, AVIMException e) {
                if(e == null){
                    if(list == null || list.isEmpty()){
                        toast("无会话");
                    }else{
                        showConversation(list);
                    }
                }else{
                    toast("获取会话列表失败，错误码："+e.getCode());
                }
            }
        });
    }

    private void showConversation(List<AVIMConversation> list) {
        if(adapter == null){
            adapter = new ConversationAdapter(list,this);
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
    public void onClick(AVIMConversation data, int action, int position) {
        Intent intent = new Intent(getContext(),ChatActivity.class);
        intent.putExtra(Config.INTENT_CONVERSATION,data.getConversationId());
        startActivity(intent);
    }
}
