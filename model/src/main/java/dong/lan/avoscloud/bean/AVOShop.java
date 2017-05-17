package dong.lan.avoscloud.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVRelation;
import com.avos.avoscloud.SaveCallback;

/**
 */

@AVClassName("Shop")
public class AVOShop extends AVObject {

    public void setUid(String uid) {
        put("uid", uid);
    }

    public String getUid() {
        return getString("uid");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getName() {
        return getString("name");
    }

    public void setAddress(String address) {
        put("address", address);
    }

    public String getAddress() {
        return getString("address");
    }

    public void setPhone(String phone) {
        put("phone", phone);
    }

    public String getPhone() {
        return getString("phone");
    }

    public void setLocation(double latitude, double longitude) {
        put("location", new AVGeoPoint(latitude, longitude));
    }

    public AVGeoPoint getLocation() {
        return getAVGeoPoint("location");
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
}
