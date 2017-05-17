
package dong.lan.taste.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import dong.lan.avoscloud.bean.AVOFeed;
import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.BaseActivity;
import dong.lan.base.ui.BaseFragment;
import dong.lan.base.ui.base.Config;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.base.ui.customView.PinCircleImageView;
import dong.lan.library.LabelTextView;
import dong.lan.map.service.LocationService;
import dong.lan.map.utils.MapHelper;
import dong.lan.permission.CallBack;
import dong.lan.permission.Permission;
import dong.lan.taste.App;
import dong.lan.taste.R;
import dong.lan.taste.event.ConvEvent;
import dong.lan.taste.event.MarkerEvent;
import dong.lan.taste.helper.FeedMarkerHelper;
import dong.lan.taste.mvp.contract.MainMapContract;
import dong.lan.taste.mvp.presenter.MainMapPresenter;

public class MainActivity extends BaseActivity implements View.OnClickListener, MainMapContract.View, BaiduMap.OnMarkerClickListener, ViewPager.OnPageChangeListener, BaiduMap.OnMapClickListener {


    private static final String TAG = MainActivity.class.getSimpleName();
    private MapView mapView;
    private TextView usernameTv;
    private CircleImageView avatar;
    private LabelTextView shareLocationLtv;
    private BaiduMap baiduMap;
    private SlidingRootNav slidingRootNav;
    private boolean isMenuOpen = false;
    private MainMapContract.Presenter presenter;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Fragment[] tab;
    private MaterialSearchView searchView;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RelativeLayout markerLayout;
    private ImageButton markerLike;
    private ImageButton markerGuide;
    private ImageButton markerDetail;
    private TextView markerInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private boolean isFirstLoc = true;
    private LocationService.LocationCallback locationCallback = new LocationService.LocationCallback() {
        @Override
        public void onLocation(BDLocation location, String error) {
            if (!TextUtils.isEmpty(error)) {
                dialog(error);
            } else {
                if (isFirstLoc) {
                    MapHelper.setLocation(location, baiduMap, true);
                    isFirstLoc = false;
                    ((BaseFragment) tab[0]).start(null);
                    ((BaseFragment) tab[1]).start(null);
                    ((BaseFragment) tab[2]).start(null);
                }
            }
        }
    };


    private MaterialSearchView.OnQueryTextListener queryTextListener = new MaterialSearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            ((NearShopFragment) tab[0]).query(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (TextUtils.isEmpty(newText))
                return false;
//            ((NearShopFragment) tab[0]).query(newText);
            return false;
        }
    };

    private void initView() {

        tab = new Fragment[4];
        tab[0] = NearShopFragment.newInstance("附近店铺", 0, 0);
        tab[1] = NearFeedFragment.newInstance("附近食趣", 0, 0);
        tab[2] = NearUserFragment.newInstance("附近食友", 0, 0);
        tab[3] = ConversationFragment.newInstance("会话");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("");
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
        collapsingToolbarLayout.setScrimsShown(false);
        searchView = (MaterialSearchView) findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(queryTextListener);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager()));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
        presenter = new MainMapPresenter(this);

        View menuView = LayoutInflater.from(this)
                .inflate(R.layout.draw_menu, null);

        menuView.findViewById(R.id.me).setOnClickListener(this);
        menuView.findViewById(R.id.favorite).setOnClickListener(this);
        menuView.findViewById(R.id.setting).setOnClickListener(this);
        menuView.findViewById(R.id.logout).setOnClickListener(this);
        menuView.findViewById(R.id.friends).setOnClickListener(this);
        usernameTv = (TextView) menuView.findViewById(R.id.username);
        avatar = (CircleImageView) menuView.findViewById(R.id.user_avatar);
        avatar.setOnClickListener(this);
        slidingRootNav = new SlidingRootNavBuilder(this)
                .withMenuView(menuView)
                .inject();
        slidingRootNav.closeMenu();
        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setOnMarkerClickListener(this);

        markerLayout = (RelativeLayout) findViewById(R.id.marker_layout);
        markerDetail = (ImageButton) findViewById(R.id.hone_marker_detal);
        markerGuide = (ImageButton) findViewById(R.id.hone_marker_line);
        markerLike = (ImageButton) findViewById(R.id.hone_marker_like);
        markerInfo = (TextView) findViewById(R.id.hone_marker_info);
        markerLike.setOnClickListener(this);
        markerDetail.setOnClickListener(this);
        markerGuide.setOnClickListener(this);

        baiduMap.setOnMapClickListener(this);

        List<String> pers = new ArrayList<>(5);
        pers.add(Manifest.permission.ACCESS_FINE_LOCATION);
        pers.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        pers.add(Manifest.permission.READ_PHONE_STATE);
        pers.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        pers.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Permission.instance().check(new CallBack<List<String>>() {
            @Override
            public void onResult(List<String> result) {
                if (result == null)
                    LocationService.service().registerCallback(MainActivity.this, locationCallback);
            }
        }, this, pers);
        EventBus.getDefault().register(this);
        App.myApp().initIM();
        AVOUser user = AVOUser.getCurrentUser();
        Glide.with(this).load(user.getAvatar() == null ? "" : user.getAvatar().getUrl())
                .error(R.drawable.head)
                .into(avatar);
        usernameTv.setText(user.getDisplayName());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void convEvent(ConvEvent convEvent) {
        ((BaseFragment) tab[3]).start(null);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLabelQuery(AVOLabel label) {
    }

    private Marker curMarker;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMarkerEvent(MarkerEvent event) {
        baiduMap.clear();
        LatLng point = new LatLng(event.latitude, event.longitude);
        curMarker = MapHelper.drawMarker(baiduMap, point, BitmapDescriptorFactory.fromResource(R.drawable.location_flag));
        curMarker.setExtraInfo(event.data);
        MapHelper.setLocation(point, baiduMap, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null)
            mapView.onDestroy();
        baiduMap = null;
        presenter.saveUserLocation();
        EventBus.getDefault().unregister(this);
        viewPager.removeOnPageChangeListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new_feed) {
            startActivity(new Intent(this, CreateFeedActivity.class));
        } else if (id == R.id.action_search) {

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Permission.instance().handleRequestResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.hone_marker_detal:
                Bundle bundle = curMarker.getExtraInfo();
                if (bundle.getInt("type", -1) == Config.MARKER_TYPE_SHOP) {
                    Intent detailIntent = new Intent(this, ShopDetailActivity.class);
                    detailIntent.putExtra("uid", ((PoiInfo) bundle.getParcelable("data")).uid);
                    startActivity(detailIntent);
                }
                markerLayout.setVisibility(View.GONE);
                break;
            case R.id.hone_marker_like:
                Bundle bundle1 = curMarker.getExtraInfo();
                presenter.likeShop(((PoiInfo) bundle1.getParcelable("data")));
                markerLayout.setVisibility(View.GONE);
                break;
            case R.id.hone_marker_line:
                if (curMarker != null) {
                    presenter.queryRoute(curMarker.getPosition(), baiduMap);
                }
                markerLayout.setVisibility(View.GONE);
                break;
            case R.id.bar_left:
                if (isMenuOpen)
                    slidingRootNav.closeMenu(true);
                else
                    slidingRootNav.openMenu(true);
                isMenuOpen = !isMenuOpen;
                break;

            case R.id.me:
                startActivity(new Intent(this, UserCenterActivity.class));
                break;
            case R.id.friends:
                startActivity(new Intent(this, FriendsActivity.class));
                break;
            case R.id.favorite:
                startActivity(new Intent(this, FavoriteActivity.class));
                break;
            case R.id.setting:
                // startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.logout:
                AVOUser.logOut();
                finish();
                break;
            case R.id.user_avatar:
                startActivity(new Intent(this, UserCenterActivity.class));
                break;
        }
    }

    @Override
    public void showNearUser(List<AVOUser> users) {
        if (!users.isEmpty())
            baiduMap.clear();
        for (AVOUser user : users) {
            View view = LayoutInflater.from(this).inflate(R.layout.map_user_head_pin, null);
            PinCircleImageView pin = (PinCircleImageView) view.findViewById(R.id.pin_user_head);
            Glide.with(this)
                    .load(user.getAvatar() == null ? "" : user.getAvatar().getUrl())
                    .error(R.drawable.head)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .placeholder(R.drawable.head)
                    .into(pin);
            Marker marker = MapHelper.drawMarker(baiduMap,
                    new LatLng(user.getLastLocation().getLatitude(),
                            user.getLastLocation().getLongitude()),
                    BitmapDescriptorFactory.fromView(view), 0.5f, 1f);
            marker.setDraggable(false);
            Bundle data = new Bundle();
            data.putInt("type", 0);
            data.putString("user", user.toString());
            marker.setExtraInfo(data);
        }
    }

    @Override
    public void showNearFeed(List<AVOFeed> feeds) {
        if (!feeds.isEmpty())
            baiduMap.clear();
        for (AVOFeed feed : feeds) {

            Marker marker = MapHelper.drawMarker(baiduMap,
                    new LatLng(feed.getLocation().getLatitude(),
                            feed.getLocation().getLongitude()),
                    BitmapDescriptorFactory.fromView(FeedMarkerHelper.instance()
                            .formMarkerView(this, feed)), 0.5f, 1f);
            marker.setDraggable(false);
            Bundle data = new Bundle();
            data.putInt("type", 1);
            data.putString("feed", feed.toString());
            marker.setExtraInfo(data);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        markerLayout.setVisibility(View.VISIBLE);
        markerInfo.setText(marker.getExtraInfo().getString("info"));
        return true;
    }

    @Override
    public Activity activity() {
        return this;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (markerLayout.getVisibility() == View.VISIBLE)
            markerLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tab[position];
        }

        @Override
        public int getCount() {
            return tab.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tab[position].getArguments().getString("tittle");
        }
    }
}
