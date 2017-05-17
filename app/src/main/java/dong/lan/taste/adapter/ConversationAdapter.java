package dong.lan.taste.adapter;

/**
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.im.v2.AVIMConversation;
import com.avos.avoscloud.im.v2.AVIMMessage;

import java.util.ArrayList;
import java.util.List;

import dong.lan.base.BaseItemClickListener;
import dong.lan.base.ui.customView.CircleImageView;
import dong.lan.taste.R;

/**
 * 内容列表的适配器
 */

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {


    private List<AVIMConversation> conversations;
    private BaseItemClickListener<AVIMConversation> clickListener;

    public ConversationAdapter(List<AVIMConversation> feeds, BaseItemClickListener<AVIMConversation> itemClickListener) {
        this.conversations = new ArrayList<>();
        this.conversations.addAll(feeds);
        clickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversation, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVIMConversation conversation = conversations.get(position);
        holder.name.setText(conversation.getName());
        AVIMMessage message = conversation.getLastMessage();
        if(message!=null)
            holder.msg.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return conversations == null ? 0 : conversations.size();
    }

    public void loadMore(List<AVIMConversation> list) {
        conversations.addAll(list);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView avatar;
        TextView name;
        TextView msg;

        public ViewHolder(View itemView) {
            super(itemView);

            avatar = (CircleImageView) itemView.findViewById(R.id.item_conversation_avatar);
            name = (TextView) itemView.findViewById(R.id.item_conversation_name);
            msg = (TextView) itemView.findViewById(R.id.item_conversation_msg);

            if(clickListener!=null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int p = getLayoutPosition() - 1;
                        if (clickListener != null)
                            clickListener.onClick(conversations.get(p), 0, p);
                    }
                });
            }

        }
    }
}