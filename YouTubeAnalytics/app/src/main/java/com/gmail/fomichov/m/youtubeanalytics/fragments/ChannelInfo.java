package com.gmail.fomichov.m.youtubeanalytics.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.R;
import com.gmail.fomichov.m.youtubeanalytics.json.json_channel.ChannelYouTube;
import com.gmail.fomichov.m.youtubeanalytics.request.ChannelsRequest;
import com.gmail.fomichov.m.youtubeanalytics.request.CommentsRequest;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyDateUtils;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyFileUtils;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyLog;
import com.gmail.fomichov.m.youtubeanalytics.utils.TestInternet;

import java.util.concurrent.ExecutionException;

public class ChannelInfo extends Fragment {
    private ChannelsRequest channelsRequest;
    private Handler handle;
    private ChannelYouTube tube;
    private boolean cache;
    private EditText etChannelId;
    private static final String TYPE_TASK = "typeTask";
    private boolean typeTask;
    private View myView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        typeTask = getArguments().getBoolean("typeTask");
        if (typeTask) {
            myView = inflater.inflate(R.layout.frag_mediaresonance, container, false);
        } else {
            myView = inflater.inflate(R.layout.frag_globalinfo, container, false);
        }
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString((R.string.msgProgressDialog)));
        etChannelId = (EditText) myView.findViewById(R.id.etChannelId);
        final TextView tvChannelNameResult = (TextView) myView.findViewById(R.id.tvChannelNameResult);
        final TextView tvDateCreationChannelResult = (TextView) myView.findViewById(R.id.tvDateCreationChannelResult);
        final TextView tvNumberSubscribersResult = (TextView) myView.findViewById(R.id.tvNumberSubscribersResult);
        final TextView tvNumberVideosResult = (TextView) myView.findViewById(R.id.tvNumberVideosResult);
        final TextView tvNumberViewsResult = (TextView) myView.findViewById(R.id.tvNumberViewsResult);
        final TextView tvNumberCommentsResult = (TextView) myView.findViewById(R.id.tvNumberCommentsResult);
        Button btnGetResult = (Button) myView.findViewById(R.id.btnGetResult);

        btnGetResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLog.showLog(typeTask + "");
                try {
                    boolean typeTaskIf; // принмает разные типы условий в зависимости от задачи
                    if (typeTask) {
                        typeTaskIf = MainActivity.sharedPreferences.getBoolean("setSaveCache", false) && MyFileUtils.getFolder() && !MyFileUtils.findIdChannelFile(etChannelId.getText().toString()) && getCountComment();
                    } else {
                        typeTaskIf = MainActivity.sharedPreferences.getBoolean("setSaveCache", false) && MyFileUtils.getFolder() && !MyFileUtils.findIdChannelFile(etChannelId.getText().toString());
                    }
                    if (typeTaskIf) {
                        progressDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                                    MainActivity.startTime = System.currentTimeMillis();
                                }
                                try {
                                    tube = MyFileUtils.getDataIdChannel(etChannelId.getText().toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                                cache = true; // текст будет красным
                                handle.sendMessage(handle.obtainMessage());
                            }
                        }).start();
                    } else if (TestInternet.isOnline(getContext())) {
                        progressDialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                                    MainActivity.startTime = System.currentTimeMillis();
                                }
                                channelsRequest = new ChannelsRequest(etChannelId.getText().toString());
                                try {
                                    tube = channelsRequest.getSingleObject();
                                    if (typeTask) {
                                        tube.setCountComments(new CommentsRequest().getCountComment(tube.items.get(0).contentDetails.relatedPlaylists.uploads));
                                    }
                                } catch (ExecutionException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                                cache = false; // текст будет красным
                                handle.sendMessage(handle.obtainMessage());
                            }
                        }).start();
                    } else {
                        Toast.makeText(getContext(), getResources().getString((R.string.toastNoInternet)), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handle = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try {
                            if (cache) {
                                tvChannelNameResult.setTextColor(Color.RED);
                                tvDateCreationChannelResult.setTextColor(Color.RED);
                                tvNumberSubscribersResult.setTextColor(Color.RED);
                                tvNumberVideosResult.setTextColor(Color.RED);
                                tvNumberViewsResult.setTextColor(Color.RED);
                                if (typeTask) {
                                    tvNumberCommentsResult.setTextColor(Color.RED);
                                }
                            } else {
                                tvChannelNameResult.setTextColor(Color.BLUE);
                                tvDateCreationChannelResult.setTextColor(Color.BLUE);
                                tvNumberSubscribersResult.setTextColor(Color.BLUE);
                                tvNumberVideosResult.setTextColor(Color.BLUE);
                                tvNumberViewsResult.setTextColor(Color.BLUE);
                                if (typeTask) {
                                    tvNumberCommentsResult.setTextColor(Color.BLUE);
                                }
                            }
                            tvChannelNameResult.setText(tube.items.get(0).snippet.title);
                            tvDateCreationChannelResult.setText(String.valueOf(MyDateUtils.convertStringToDate(tube.items.get(0).snippet.publishedAt)));
                            tvNumberSubscribersResult.setText(String.valueOf(tube.items.get(0).statistics.subscriberCount));
                            tvNumberVideosResult.setText(String.valueOf(tube.items.get(0).statistics.videoCount));
                            tvNumberViewsResult.setText(String.valueOf(tube.items.get(0).statistics.viewCount));
                            if (typeTask) {
                                tvNumberCommentsResult.setText(String.valueOf(tube.countComments));
                            }
                            if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                                MainActivity.endTime = System.currentTimeMillis();
                                Toast.makeText(getContext(), "load data " + (MainActivity.endTime - MainActivity.startTime) + "ms", Toast.LENGTH_LONG).show();
                            }
                            if (MainActivity.sharedPreferences.getBoolean("setSaveCache", false)) {
                                if (!MyFileUtils.getFolder()) { // если папки еще нет то создаем, соответсвенно проверку на наличие такого же файла не проводим
                                    MyFileUtils.writeFileIdChannel(JSON.toJSONString(tube), etChannelId.getText().toString(), getContext(), getActivity());
                                } else if (MyFileUtils.findIdChannelFile(etChannelId.getText().toString())) { // проверяем на наличие файла с таким названием, если нету то пишем файл
                                    MyFileUtils.writeFileIdChannel(JSON.toJSONString(tube), etChannelId.getText().toString(), getContext(), getActivity());
                                }
                                if (typeTask) {
                                    if (!MyFileUtils.findIdChannelFile(etChannelId.getText().toString()) && !getCountComment()) { // если есть файл и в нем количество коментов равно 0
                                        MyFileUtils.writeFileIdChannel(JSON.toJSONString(tube), etChannelId.getText().toString(), getContext(), getActivity());
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

            }
        });

        return myView;
    }

    // проверяем в файле есть ли коменты, если нет то будем делать запрос
    private boolean getCountComment() throws Exception {
        return (MyFileUtils.getDataIdChannel(etChannelId.getText().toString()).countComments > 0);
    }

    public static ChannelInfo newInstance(Boolean typeTask) {
        ChannelInfo fragment = new ChannelInfo();
        Bundle args = new Bundle();
        args.putBoolean(TYPE_TASK, typeTask);
        fragment.setArguments(args);
        return fragment;
    }
}
