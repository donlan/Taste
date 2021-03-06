
package dong.lan.taste.mvp.contract;

import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.IActivityFunc;
import dong.lan.base.ui.ProgressView;

/**
 */

public interface FeedDetailContract {
    public interface View extends ProgressView,IActivityFunc {
        void showFeedImages(List<AVOFeedImage> images);

        void showFeedLikes(int likeCount);

        void showLabels(List<AVOLabel> labels);

        void showContent(String content);

        void showUserInfo(AVOUser creator);
    }

    public interface Presenter {
        void like(ImageButton likeIcon, TextView likText);

        void fetchFeed(String feedSeq);

        void saveLike();

        void share();

        void toUserCenter();
    }

    public interface Model {
    }
}
