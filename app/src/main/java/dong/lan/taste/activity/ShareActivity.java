package dong.lan.taste.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

import dong.lan.avoscloud.bean.AVOShare;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.BaseActivity;
import dong.lan.taste.R;
import dong.lan.taste.adapter.UserSelectAdapter;

/**
 * 分享页面
 */

public class ShareActivity extends BaseActivity {

    private TextView shareInfo;
    private EditText shareExtra;
    private RecyclerView userList;

    private String uid;
    private String type;
    private String json;
    private UserSelectAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        shareInfo = (TextView) findViewById(R.id.share_info);
        shareExtra = (EditText) findViewById(R.id.share_extra);
        userList = (RecyclerView) findViewById(R.id.usersList);
        userList.setLayoutManager(new GridLayoutManager(this, 1));
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        uid = getIntent().getStringExtra("uid");
        type = getIntent().getStringExtra("type");
        json = getIntent().getStringExtra("json");
        String desc = getIntent().getStringExtra("desc");

        shareInfo.setText(desc);

        final AVOUser avoUser = AVOUser.getCurrentUser();
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
                        adapter = new UserSelectAdapter(list);
                        userList.setAdapter(adapter);
                    }
                } else {
                    e.printStackTrace();
                    dialog("获取好友失败，错误码：" + e.getCode());
                }
            }
        });
    }

    private void share() {
        //获取分享给指定的用户，没有选定分享用户则不能分享
        if (adapter == null || adapter.getSelectUsers() == null || adapter.getSelectUsers().isEmpty()) {
            toast("无分享用户");
            return;
        }
        //创建分享
        AVOShare share = new AVOShare();
        share.setUid(uid);
        share.setCreator(AVOUser.getCurrentUser());
        share.setDescribe(shareExtra.getText().toString());
        share.setType(type);
        share.setJsonObject(json);
        share.getLikes().addAll(adapter.getSelectUsers());
        share.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    toast("分享成功");
                } else {
                    toast("分享失败：" + e.getCode());
                }
            }
        });
    }
}
