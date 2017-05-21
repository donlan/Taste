package dong.lan.taste.event;

import com.avos.avoscloud.im.v2.AVIMConversation;

/**
 *会话初始化完成的通知事件
 */

public class ConvMemberEvent {

    public int type;
    public AVIMConversation conversation;
}
