package dong.lan.taste.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;
import com.avos.avoscloud.im.v2.AVIMMessageHandler;
import com.avos.avoscloud.im.v2.AVIMMessageManager;

import java.util.List;

import dong.lan.base.ui.BaseBarActivity;
import dong.lan.map.activity.PickLocationActivity;
import dong.lan.map.service.Config;
import dong.lan.taste.R;
import dong.lan.taste.adapter.ChatAdapter;
import dong.lan.taste.mvp.contract.ChatContract;
import dong.lan.taste.mvp.presenter.ChatPresenter;

/**
 * 聊天页面
 */

public class ChatActivity extends BaseBarActivity implements View.OnClickListener, ChatContract.View {

    private SwipeRefreshLayout refreshLayout;
    private LinearLayout chatToolLayout;
    private RecyclerView chatList;
    private LinearLayout chatInputLayout;
    private EditText chatInput;
    private Button sendBtn;
    private ImageButton chatToggle;


    private ChatContract.Presenter presenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatList = (RecyclerView) findViewById(R.id.chatList);
        chatInput = (EditText) findViewById(R.id.chat_input);
        chatInputLayout = (LinearLayout) findViewById(R.id.chat_input_layout);
        sendBtn = (Button) findViewById(R.id.chat_send);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.chatRefresher);
        chatToolLayout = (LinearLayout) findViewById(R.id.chat_tool_layout);
        chatToggle = (ImageButton) findViewById(R.id.chat_panel_toggle);
        chatToggle.setOnClickListener(this);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.sendTextMessage(chatInput.getText().toString());
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
            }
        });

        findViewById(R.id.send_location_msg)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, PickLocationActivity.class);
                        startActivityForResult(intent, Config.RESULT_LOCATION);
                    }
                });
        presenter = new ChatPresenter(this);


        String conversationId = getIntent().getStringExtra(dong.lan.base.ui.base.Config.INTENT_CONVERSATION);
        String userStr = getIntent().getStringExtra(dong.lan.base.ui.base.Config.INTENT_USER);

        if (TextUtils.isEmpty(conversationId))
            presenter.start(userStr);
        else {
            presenter.startById(conversationId);
        }


    }

    private ChatAdapter adapter;


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.chat_panel_toggle:
                if (chatToolLayout.getVisibility() == View.GONE) {
                    chatToolLayout.setVisibility(View.VISIBLE);
                } else {
                    chatToolLayout.setVisibility(View.GONE);
                }
                break;
        }
    }


    private double latitude;
    private double longitude;
    private String address;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //返回的地理位置信息
        if (requestCode == Config.RESULT_LOCATION && resultCode == Config.RESULT_LOCATION) {
            address = data.getStringExtra(Config.LOC_ADDRESS);
            latitude = data.getDoubleExtra(Config.LATITUDE, 0);
            longitude = data.getDoubleExtra(Config.LONGITUDE, 0);
            if (latitude != 0 && longitude != 0) {
                presenter.newGuide(latitude, longitude, address);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private MyMessageHandler myMessageHandler;

    @Override
    protected void onResume() {
        super.onResume();
        if (myMessageHandler == null)
            myMessageHandler = new MyMessageHandler();
        AVIMMessageManager.registerMessageHandler(AVIMMessage.class, myMessageHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVIMMessageManager.unregisterMessageHandler(AVIMMessage.class, myMessageHandler);
    }

    @Override
    public Activity activity() {
        return this;
    }

    @Override
    public void initView(String username) {
        adapter = new ChatAdapter();
        chatList.setLayoutManager(new GridLayoutManager(this, 1));
        chatList.setAdapter(adapter);
        tittle(username);
    }

    @Override
    public void showMessage(List<AVIMMessage> list) {
        adapter.newMessage(list);
        chatList.scrollToPosition(list.size());
    }

    @Override
    public void newMessage(AVIMMessage textMessage) {
        adapter.newMessage(textMessage);
        chatList.scrollToPosition(chatList.getAdapter().getItemCount() - 1);
        chatInput.setText("");
    }


    private class MyMessageHandler extends AVIMMessageHandler {
        @Override
        public void onMessage(AVIMMessage message, AVIMConversation avimConversation, AVIMClient client) {
            presenter.handlerMessage(message, avimConversation, client);
            super.onMessage(message, avimConversation, client);
        }
    }
}