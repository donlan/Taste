package dong.lan.taste;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMConversationEventHandler;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.blankj.ALog;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import dong.lan.avoscloud.ModelConfig;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.utils.SPHelper;
import dong.lan.map.service.LocationService;
import dong.lan.taste.event.ConvInitEvent;
import dong.lan.taste.event.ConvMemberEvent;
import dong.lan.taste.im.IMMessageHandler;

/**
 */

public class App extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        LocationService.service().init(this);
        ModelConfig.init(this);
        SPHelper.instance().init(this,"taste");
        new ALog.Builder(this).setGlobalTag("DOOZE");
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private static App app;
    private AVIMClient avimClient;

    public static App myApp() {
        return app;
    }

    public LocationService getLocationService() {
        return LocationService.service();
    }

    public AVIMClient getAvimClient() {
        return avimClient;
    }



    public void initIM(){
        AVIMMessageManager.registerDefaultMessageHandler(new IMMessageHandler());
        AVIMClient.getInstance(AVOUser.getCurrentUser().getObjectId())
                .open(new AVIMClientCallback() {
                    @Override
                    public void done(AVIMClient avimClient, final AVIMException e) {
                        if (e == null) {
                            App.this.avimClient = avimClient;
                            EventBus.getDefault().post(new ConvInitEvent());
                            AVIMMessageManager.setConversationEventHandler(new AVIMConversationEventHandler() {
                                @Override
                                public void onMemberLeft(AVIMClient avimClient, AVIMConversation avimConversation, List<String> list, String s) {

                                }

                                @Override
                                public void onMemberJoined(AVIMClient avimClient, AVIMConversation avimConversation, List<String> list, String s) {
                                    ConvMemberEvent event = new ConvMemberEvent();
                                    event.conversation = avimConversation;
                                    EventBus.getDefault().post(event);
                                }

                                @Override
                                public void onKicked(AVIMClient avimClient, AVIMConversation avimConversation, String s) {

                                }

                                @Override
                                public void onInvited(AVIMClient avimClient, AVIMConversation avimConversation, String s) {

                                }
                            });
                        } else {

                        }
                    }
                });
    }
}
