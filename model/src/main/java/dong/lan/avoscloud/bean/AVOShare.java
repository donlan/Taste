package dong.lan.avoscloud.bean;

import android.content.Context;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.SaveCallback;

import java.util.List;

/**
 */

@AVClassName("Share")
public class AVOShare extends AVObject {

    public AVOUser getCreator() {
        try {
            return getAVObject("creator", AVOUser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setCreator(AVOUser user) {
        super.put("creator", user);
    }

    public void setUid(String uid) {
        put("uid", uid);
    }

    public String getUid() {
        return getString("uid");
    }

    public void setType(String uid) {
        put("type", uid);
    }

    public String getType() {
        return getString("type");
    }

    public void setDescribe(String describe) {
        put("describe", describe);
    }

    public String getDescribe() {
        return getString("describe");
    }

    public void setJsonObject(String jsonObject) {
        put("jsonObj", jsonObject);
    }

    public String getJsonObject() {
        return getString("jsonObj");
    }


    public AVRelation getLikes() {
        return getRelation("likes");
    }

    public void removeLike(AVOUser tourist) {
        getLikes().remove(tourist);
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null)
                    e.printStackTrace();
            }
        });
    }

    public void addLike(AVOUser tourist) {
        getLikes().add(tourist);
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null)
                    e.printStackTrace();
            }
        });
    }

    public void addLike(List<AVOUser> tourist) {
        getLikes().addAll(tourist);
        this.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e != null)
                    e.printStackTrace();
            }
        });
    }

    public void jump(Context context) {

    }
}
