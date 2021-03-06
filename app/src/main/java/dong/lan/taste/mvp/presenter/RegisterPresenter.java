
package dong.lan.taste.mvp.presenter;

import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;

import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.taste.feature.presenter.IRegisterPresenter;
import dong.lan.taste.feature.view.IRegisterView;

/**
 */

public class RegisterPresenter implements IRegisterPresenter {

    private IRegisterView view;

    public RegisterPresenter(IRegisterView view) {
        this.view = view;
    }

    @Override
    public void register(final String username, final String password) {
        if (TextUtils.isEmpty(username)) {
            view.toast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(username)) {
            view.toast("密码长度为6到16为数字字母组合");
            return;
        }
        final AVUser avUser = new AVUser();
        avUser.setUsername(username);
        avUser.setPassword(password);
        avUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(AVException e) {
                if(e == null){
                    final AVOUser user = new AVOUser();
                    user.setCreator(avUser);
                    user.setSex(-1);
                    user.setLastLocation(0, 0);
                    user.setNickname("");
                    user.setShareLocation(false);
                    user.setAvatar(null);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                view.toast("即将为你自动登录");
                                Intent intent = new Intent();
                                intent.putExtra("password", password);
                                intent.putExtra("username", username);
                                AVOUser.setCurrentUser(user);
                                view.activity().setResult(1, intent);
                                view.activity().finish();
                            } else {
                                e.printStackTrace();
                                view.dialog("注册失败，错误码：" + e.getCode());
                            }
                        }
                    });
                }else {
                    e.printStackTrace();
                }
            }
        });
    }
}
