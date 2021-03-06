
package dong.lan.taste.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dong.lan.avoscloud.bean.AVOLabel;
import dong.lan.avoscloud.bean.AVOUser;
import dong.lan.base.ui.BaseBarActivity;
import dong.lan.base.ui.customView.TagCloudView;
import dong.lan.taste.R;
import dong.lan.taste.event.PickLabelEvent;

/**
 * 选择标签页面
 */

public class PickLabelActivity extends BaseBarActivity {

    private EditText labelInput;
    private TagCloudView myLabelsView;
    private TagCloudView hotLabelsView;


    private List<AVOLabel> feedLabels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_label);
        bindView("选择标签");
        initView();
    }

    private void initView() {
        labelInput = (EditText) findViewById(R.id.label_input);
        findViewById(R.id.new_label_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLabel(labelInput.getText().toString());
            }
        });
        myLabelsView = (TagCloudView) findViewById(R.id.my_tags_list);
        hotLabelsView = (TagCloudView) findViewById(R.id.hot_tags_list);

        myLabelsView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int postion) {
                feedLabels.remove(postion);
                myLabelsView.getAdapter().notifyItemRemoved(postion);
            }
        });

        hotLabelsView.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onTagClick(int postion) {
                if(feedLabels.size()>=3){
                    toast("只能添加三个标签");
                }else {
                    AVOLabel avoLabel = (AVOLabel) hotLabelsView.getData(postion);
                    if (!feedLabels.contains(avoLabel)) {
                        feedLabels.add(avoLabel);
                        myLabelsView.getAdapter().notifyDataSetChanged();
                    }
                }
            }
        });


        myLabelsView.setData(feedLabels);

        AVQuery<AVOLabel> nullQuery = new AVQuery<>("Label");
        nullQuery.whereDoesNotExist("creator");

        AVQuery<AVOLabel> meQuery = new AVQuery<>("Label");
        meQuery.whereEqualTo("creator", AVOUser.getCurrentUser());

        AVQuery<AVOLabel> query = AVQuery.or(Arrays.asList(nullQuery, meQuery));
        query.limit(100);
        query.findInBackground(new FindCallback<AVOLabel>() {
            @Override
            public void done(List<AVOLabel> list, AVException e) {
                if (e == null) {
                    if (list != null) {
                        hotLabelsView.setData(list);
                    }
                } else {
                    dialog("获取标签失败，错误码：" + e.getCode());
                }
            }
        });

    }

    @Override
    public void finish() {
        EventBus.getDefault().post(new PickLabelEvent(feedLabels));
        super.finish();
    }

    private boolean isRunning = false;

    private void addLabel(String label) {

        if(isRunning)
            return;
        if(feedLabels.size()>=3){
            dialog("只能添加三个标签");
            return;
        }
        isRunning = true;
        if (TextUtils.isEmpty(label)) {
            dialog("标签不能为空");
        } else {
            final AVOLabel avoLabel = new AVOLabel();
            avoLabel.setCreator(AVOUser.getCurrentUser());
            avoLabel.setLabel(label);
            avoLabel.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    isRunning = false;
                    if (e == null) {
                        feedLabels.add(avoLabel);
                        myLabelsView.getAdapter().notifyDataSetChanged();
                    } else {
                        dialog("无法保存标签，错误码：" + e.getCode());
                    }
                }
            });
        }
    }
}
