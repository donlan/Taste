package dong.lan.taste.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;

import java.util.ArrayList;
import java.util.List;

import dong.lan.base.BaseItemClickListener;
import dong.lan.taste.R;

/**
 * Created by 思远 on 2017/5/16.
 * describe ：
 */

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private List<PoiInfo> shops;
    private List<PoiInfo> cache;


    public void setCache(List<PoiInfo> infos) {
        if (cache == null)
            cache = new ArrayList<>();
        cache.clear();
        cache.addAll(infos);
        showCache(true);
    }

    public ShopAdapter() {
        this.shops = new ArrayList<>();
    }

    public void init(List<PoiInfo> poiInfos) {
        if (showCache) {
            setCache(poiInfos);
        } else {
            shops.addAll(poiInfos);
            showCache(false);
        }
    }

    public void loadMore(List<PoiInfo> infos) {
        int p = getItemCount() - 1;
        if (p < 0)
            p = 0;
        if (showCache) {
            cache.addAll(infos);
        } else {
            shops.addAll(infos);
        }
        notifyItemRangeInserted(p, getItemCount() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_near_shop, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PoiInfo poiInfo = getItem(position);
        holder.name.setText(poiInfo.name);
        holder.address.setText(poiInfo.address);
        holder.phone.setText(poiInfo.phoneNum);
    }

    private PoiInfo getItem(int postion) {
        if (showCache)
            return cache.get(postion);
        return shops.get(postion);
    }

    @Override
    public int getItemCount() {
        if (showCache)
            return cache == null ? 0 : cache.size();
        return shops.size();
    }

    private BaseItemClickListener<PoiInfo> clickListener;

    public void setClickListener(BaseItemClickListener<PoiInfo> clickListener) {
        this.clickListener = clickListener;
    }

    private boolean showCache = false;

    public void showCache(boolean b) {
        if (showCache != b) {
            showCache = b;
            notifyDataSetChanged();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        TextView address;
        TextView phone;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.item_shop_image);
            name = (TextView) itemView.findViewById(R.id.item_shop_name);
            address = (TextView) itemView.findViewById(R.id.item_shop_address);
            phone = (TextView) itemView.findViewById(R.id.item_shop_phone);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = getLayoutPosition() - 1;
                        clickListener.onClick(shops.get(p), 0, p);
                    }
                });
            }
        }
    }
}
