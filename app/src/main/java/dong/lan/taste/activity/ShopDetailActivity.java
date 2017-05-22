package dong.lan.taste.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.List;

import dong.lan.avoscloud.bean.AVOShop;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.BaseActivity;
import dong.lan.taste.R;
import dong.lan.taste.adapter.UserAdapter;

/**
 * 店铺详情页
 */

public class ShopDetailActivity extends BaseActivity implements OnGetPoiSearchResultListener, BaseItemClickListener<AVOUser> {


    private ImageButton likeIb;
    private ImageButton shareIb;
    private TextView likeCountTv;
    private TextView shopName;
    private TextView shopAddress;
    private TextView shopPhone;
    private LRecyclerView userlist;
    private TextView otherInfo;
    private AVOShop avoShop;
    private boolean isLike;
    private int likeCount;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_detail);

        initView();
    }

    private void initView() {

        likeIb = (ImageButton) findViewById(R.id.like);
        likeCountTv = (TextView) findViewById(R.id.likes_count);
        shareIb = (ImageButton) findViewById(R.id.share_shop);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        shopName = (TextView) findViewById(R.id.shop_name);
        shopAddress = (TextView) findViewById(R.id.shop_address);
        shopPhone = (TextView) findViewById(R.id.shop_phone);
        userlist = (LRecyclerView) findViewById(R.id.users_list);
        otherInfo = (TextView) findViewById(R.id.shop_other_info);
        userlist.setLayoutManager(new GridLayoutManager(this, 1));
        shareIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        likeIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                like(likeIb, likeCountTv);
            }
        });

        String uid = getIntent().getStringExtra("uid");
        if (TextUtils.isEmpty(uid))
            finish();
        else {
            //根据uid从百度搜索店铺详细信息
            PoiSearch poiSearch = PoiSearch.newInstance();
            PoiDetailSearchOption option = new PoiDetailSearchOption();
            option.poiUid(uid);
            poiSearch.setOnGetPoiSearchResultListener(this);
            poiSearch.searchPoiDetail(option);


        }
    }

    private void share() {
        if (avoShop == null) {
            toast("无法分享没有同步过的店铺");
            return;
        }
        Intent intent = new Intent(this, ShareActivity.class);
        intent.putExtra("type", "Feed");
        intent.putExtra("uid", avoShop.getObjectId());
        intent.putExtra("desc", avoShop.getName());
        intent.putExtra("json", avoShop.toString());
        startActivity(intent);
    }

    @Override
    public void onGetPoiResult(PoiResult poiResult) {

    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        showShopDetail(poiDetailResult);
    }

    /**
     * 显示店铺详情
     *
     * @param poiDetailResult
     */
    private void showShopDetail(final PoiDetailResult poiDetailResult) {
        shopName.setText(poiDetailResult.getName());
        shopAddress.setText(poiDetailResult.getAddress());
        shopPhone.setText(poiDetailResult.getTelephone());

        StringBuilder sb = new StringBuilder();
        sb.append("<p>* 营业时间：");
        sb.append(poiDetailResult.getShopHours());
        sb.append("</p><p>* 环境评价：");
        sb.append(poiDetailResult.getEnvironmentRating());
        sb.append("</p><p>* 服务评价：");
        sb.append(poiDetailResult.getServiceRating());
        sb.append("</p><p>* 设施评价：");
        sb.append(poiDetailResult.getFacilityRating());
        sb.append("</p><p>* 收藏量：");
        sb.append(poiDetailResult.getFavoriteNum());
        sb.append("</p><p>* 详情：<a>");
        sb.append(poiDetailResult.getDetailUrl());
        sb.append("</a></p>");
        otherInfo.setText(Html.fromHtml(sb.toString()));


        //获取收藏这间店铺的所有用户
        final AVQuery<AVOShop> query = new AVQuery<>("Shop");
        query.whereEqualTo("uid", poiDetailResult.getUid());
        query.findInBackground(new FindCallback<AVOShop>() {
            @Override
            public void done(List<AVOShop> list, AVException e) {
                if (e == null) {
                    if (list == null || list.isEmpty()) {
                        final AVOShop avoShop = new AVOShop();
                        avoShop.setAddress(poiDetailResult.address);
                        avoShop.setName(poiDetailResult.name);
                        avoShop.setPhone(poiDetailResult.getTelephone());
                        avoShop.setUid(poiDetailResult.getUid());
                        avoShop.setLocation(poiDetailResult.location.latitude, poiDetailResult.location.longitude);
                        avoShop.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(AVException e) {
                                if (e == null) {
                                    avoShop.addLike(AVOUser.getCurrentUser());
                                    ShopDetailActivity.this.avoShop = avoShop;
                                }
                            }
                        });
                    } else {
                        avoShop = list.get(0);
                        AVQuery<AVOUser> query1 = avoShop.getLikes().getQuery();
                        query1.include("user");
                        query1.include("avatar");
                        query1.findInBackground(new FindCallback<AVOUser>() {
                            @Override
                            public void done(List<AVOUser> list, AVException e) {

                                if (e == null && list != null) {
                                    likeCountTv.setText(String.valueOf(list.size()));
                                    userlist.setAdapter(new LRecyclerViewAdapter(new UserAdapter(list, ShopDetailActivity.this)));
                                    userlist.setLoadMoreEnabled(false);
                                    userlist.setPullRefreshEnabled(false);
                                }
                            }
                        });
                    }
                } else {
                    toast("获取店铺用户信息失败，错误码：" + e.getCode());
                }
            }
        });
    }

    @Override
    public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {

    }

    @Override
    public void onClick(AVOUser data, int action, int position) {
        Intent intent = new Intent(this, UserCenterActivity.class);
        intent.putExtra("userSeq", data.toString());
        intent.putExtra("id", data.getObjectId());
        startActivity(intent);
    }

    //收藏该店铺
    public void like(ImageButton likeIcon, TextView likText) {
        if (isLike) {
            likeCount--;
            likeIcon.setImageResource(R.drawable.ic_favorite_border);
        } else {
            likeCount++;
            likeIcon.setImageResource(R.drawable.ic_favorite);
        }
        likText.setText(String.valueOf(likeCount));
        isLike = !isLike;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (avoShop != null && isLike)
            avoShop.addLike(AVOUser.getCurrentUser());
    }
}
