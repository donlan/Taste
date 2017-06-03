package dong.lan.taste.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dong.lan.avoscloud.bean.AVOShare;
import dong.lan.base.BaseItemClickListener;
import dong.lan.taste.R;

/**
 * 分享列表的适配器
 */

public class SharesAdapter extends RecyclerView.Adapter<SharesAdapter.ViewHolder> {


    private List<AVOShare> shares;
    private BaseItemClickListener<AVOShare> clickListener;


    public void setClickListener(BaseItemClickListener<AVOShare> clickListener) {
        this.clickListener = clickListener;
    }

    public SharesAdapter(List<AVOShare> shares) {
        this.shares = new ArrayList<>();
        this.shares.addAll(shares);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_share, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVOShare feed = shares.get(position);
        holder.info.setText(feed.getDescribe());
        holder.type.setText(shares.get(position).getType());
        holder.from.setText(feed.getCreator().getDisplayName());
    }

    @Override
    public int getItemCount() {
        return shares == null ? 0 : shares.size();
    }

    public void loadMore(List<AVOShare> list) {
        shares.addAll(list);
    }

    public void reset(List<AVOShare> list) {
        shares.clear();
        shares.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView info;
        TextView from;
        TextView type;

        public ViewHolder(final View itemView) {
            super(itemView);

            info = (TextView) itemView.findViewById(R.id.item_share_info);
            from = (TextView) itemView.findViewById(R.id.item_share_from);
            type = (TextView) itemView.findViewById(R.id.item_share_type);

            if (clickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = getLayoutPosition() - 1;
                        clickListener.onClick(shares.get(p), 0, p + 1);
                    }

                });
            }
        }
    }
}