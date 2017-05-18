
package dong.lan.avoscloud;

import android.content.Context;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;

import dong.lan.avoscloud.bean.AVOFavorite;
import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOGuide;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOShare;
import dong.lan.avoscloud.bean.AVOShop;
import dong.lan.avoscloud.bean.AVOUser;


public final class ModelConfig {
    private static final String API_ID = "vtLil59BTSwVFGrFddyDhfI5-9Nh9j0Va";
    private static final String API_KEY = "t7YnOqmL3D60ThObsdulYCf0";

    public static void init(Context appContext) {
        AVObject.registerSubclass(AVOLabel.class);
        AVObject.registerSubclass(AVOFeed.class);
        AVObject.registerSubclass(AVOFavorite.class);
        AVObject.registerSubclass(AVOFeedImage.class);
        AVObject.registerSubclass(AVOGuide.class);
        AVObject.registerSubclass(AVOUser.class);
        AVObject.registerSubclass(AVOShop.class);
        AVObject.registerSubclass(AVOShare.class);
        AVOSCloud.initialize(appContext, API_ID, API_KEY);
        AVOSCloud.setDebugLogEnabled(true);
    }
}
