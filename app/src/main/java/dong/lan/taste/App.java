package dong.lan.taste;

import android.app.Application;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.AVIMMessageManager;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.blankj.ALog;

import dong.lan.avoscloud.ModelConfig;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.utils.SPHelper;
import dong.lan.map.service.LocationService;
import dong.lan.taste.im.IMMessageHandler;

/**
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        LocationService.service().init(this);
        ModelConfig.init(this);
        SPHelper.instance().init(this,"taste");
        new ALog.Builder(this).setGlobalTag("DOOZE");
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
                    public void done(AVIMClient avimClient, AVIMException e) {
                        if (e == null) {
                            App.this.avimClient = avimClient;
                        } else {
                        }
                    }
                });
    }
}
