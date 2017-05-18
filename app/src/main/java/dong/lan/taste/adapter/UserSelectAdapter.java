package dong.lan.taste.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.taste.R;

/**
 * Created by 思远 on 2017/5/16.
 * describe ：
 */

public class UserSelectAdapter extends RecyclerView.Adapter<UserSelectAdapter.ViewHolder> {
    private List<AVOUser> users;
    private Map<Integer, AVOUser> map;

    public UserSelectAdapter(List<AVOUser> users) {
        this.users = users;
        map = new HashMap<>();
    }

    public List<AVOUser> getSelectUsers() {
        List<AVOUser> res = new ArrayList<>();
        for (Integer key : map.keySet()) {
            res.add(map.get(key));
        }
        return res;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_users_select, null);
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
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = (CircleImageView) itemView.findViewById(R.id.item_user_avatar);
            username = (TextView) itemView.findViewById(R.id.item_user_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_users_check);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int p = getLayoutPosition();
                    if (isChecked) {
                        AVOUser user = users.get(p);
                        map.put(getLayoutPosition(), user);
                    } else {
                        map.remove(p);
                    }
                }
            });

        }
    }
}
