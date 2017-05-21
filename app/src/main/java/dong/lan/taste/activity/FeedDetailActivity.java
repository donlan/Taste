package dong.lan.taste.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;

import java.util.Collections;
import java.util.List;

import dong.lan.avoscloud.bean.AVOFeedImage;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.base.ui.customView.TagCloudView;
import dong.lan.taste.R;
import dong.lan.taste.adapter.FeedDetailImagesAdapter;
import dong.lan.taste.mvp.contract.FeedDetailContract;
import dong.lan.taste.mvp.presenter.FeedDetailPresenter;

/**
 * describe ：
 */

public class FeedDetailActivity extends BaseActivity implements FeedDetailContract.View {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);
        initView();
    }

    private TagCloudView labelTags;
    private RecyclerView feedImagesRv;
    private ImageButton likeIb;
    private ImageButton shareIb;
    private TextView content;
    private TextView likeCountTv;
    private CircleImageView avatar;
    private FeedDetailContract.Presenter presenter;

    private void initView() {
        labelTags = (TagCloudView) findViewById(R.id.feed_labels_view);
        feedImagesRv = (RecyclerView) findViewById(R.id.feed_images_view);
        likeIb = (ImageButton) findViewById(R.id.like);
        likeCountTv = (TextView) findViewById(R.id.likes_count);
        shareIb = (ImageButton) findViewById(R.id.feed_share);
        content = (TextView) findViewById(R.id.feed_content);
        avatar = (CircleImageView) findViewById(R.id.user_avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.toUserCenter();
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        feedImagesRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        likeIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.like(likeIb, likeCountTv);
            }
        });

        shareIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.share();
            }
        });
        presenter = new FeedDetailPresenter(this);

        presenter.fetchFeed(getIntent().getStringExtra("feed"));
    }


    @Override
    public void finish() {
        presenter.saveLike();
        super.finish();
    }

    @Override
    public Activity activity() {
        return this;
    }

    @Override
    public void showFeedImages(List<AVOFeedImage> images) {
        feedImagesRv.setAdapter(new FeedDetailImagesAdapter(images));
    }

    @Override
    public void showFeedLikes(int likeCount) {
        likeCountTv.setText(likeCount + " 人喜欢");
    }

    @Override
    public void showLabels(List<AVOLabel> labels) {
        labelTags.setData(labels);
    }

    @Override
    public void showContent(String content) {
        this.content.setText(content);
    }

    @Override
    public void showUserInfo(AVOUser creator) {
        if (creator == null)
            return;
        AVQuery<AVOUser> query = new AVQuery<>("MyUser");
        query.include("avatar");
        query.setCachePolicy(AVQuery.CachePolicy.CACHE_ELSE_NETWORK);
        query.selectKeys(Collections.singleton("avatar"));
        query.getInBackground(creator.getObjectId(), new GetCallback<AVOUser>() {
            @Override
            public void done(AVOUser avoUser, AVException e) {
                if (e == null && avoUser != null) {
                    Glide.with(FeedDetailActivity.this)
                            .load(avoUser.getAvatar().getUrl())
                            .error(R.drawable.head)
                            .into(avatar);
                }
            }
        });

    }
}
