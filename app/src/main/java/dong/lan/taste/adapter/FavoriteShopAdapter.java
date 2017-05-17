package dong.lan.taste.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dong.lan.avoscloud.bean.AVOShop;
import dong.lan.base.BaseItemClickListener;
import dong.lan.taste.R;

/**
 * Created by 思远 on 2017/5/16.
 * describe ：
 */

public class FavoriteShopAdapter extends RecyclerView.Adapter<FavoriteShopAdapter.ViewHolder> {

    private List<AVOShop> shops;


    public FavoriteShopAdapter() {
        this.shops = new ArrayList<>();
    }


    public void loadMore(List<AVOShop> infos) {
        int p = getItemCount() - 1;
        if (p < 0)
            p = 0;
        shops.addAll(infos);
        notifyItemRangeInserted(p, getItemCount() - 1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_near_shop, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVOShop shop = shops.get(position);
        holder.name.setText(shop.getName());
        holder.address.setText(shop.getAddress());
        holder.phone.setText(shop.getPhone());
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    private BaseItemClickListener<AVOShop> clickListener;

    public void setClickListener(BaseItemClickListener<AVOShop> clickListener) {
        this.clickListener = clickListener;
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
