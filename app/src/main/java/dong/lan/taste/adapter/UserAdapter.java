package dong.lan.taste.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.bumptech.glide.Glide;

import java.util.List;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.taste.R;

/**
 * Created by 思远 on 2017/5/16.
 * describe ：
 */

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private List<AVOUser> users;
    private BaseItemClickListener<AVOUser> clickListener;

    public UserAdapter(List<AVOUser> users, BaseItemClickListener<AVOUser> itemClickListener) {
        this.users = users;
        clickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fridens, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVFile avFile = users.get(position).getAvatar();
        Glide.with(holder.itemView.getContext())
                .load(avFile == null ? "" : avFile.getUrl())
                .error(R.drawable.head)
                .into(holder.avatar);
        holder.username.setText(users.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return users == null ? 0 : users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView username;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = (CircleImageView) itemView.findViewById(R.id.item_friend_avatar);
            username = (TextView) itemView.findViewById(R.id.item_friend_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int p = getLayoutPosition()-1;
                    if (clickListener != null)
                        clickListener.onClick(users.get(p), 0, p);
                }
            });

        }
    }
}
