package dong.lan.taste.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.List;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseBarActivity;
import dong.lan.taste.R;
import dong.lan.taste.adapter.UserAdapter;

/**
 * 好友列表页面
 */

public class FriendsActivity extends BaseBarActivity implements BaseItemClickListener<AVOUser> {
    private LRecyclerView friendsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        bindView("食友");

        friendsList = (LRecyclerView) findViewById(R.id.friends_list);

        friendsList.setLayoutManager(new GridLayoutManager(this, 1));

        //查找出用户的所有好友
        AVOUser avoUser = AVOUser.getCurrentUser();
        AVQuery<AVOUser> query = avoUser.getFriends().getQuery();
        query.include("user");
        query.include("avatar");
        query.findInBackground(new FindCallback<AVOUser>() {
            @Override
            public void done(List<AVOUser> list, AVException e) {
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        toast("无好友");
                    } else {
                        friendsList.setAdapter(new LRecyclerViewAdapter(new UserAdapter(list, FriendsActivity.this)));
                        friendsList.setPullRefreshEnabled(false);
                        friendsList.setNoMore(true);
                    }
                } else {
                    e.printStackTrace();
                    dialog("获取好友失败，错误码：" + e.getCode());
                }
            }
        });
    }


    //点击一个好友，则跳转到聊天页面
    @Override
    public void onClick(AVOUser data, int action, int position) {
        Intent intent = new Intent(this, UserCenterActivity.class);
        intent.putExtra("userSeq", data.toString());
        startActivity(intent);
    }
}
