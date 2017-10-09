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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.R;
import com.gmail.fomichov.m.youtubeanalytics.json.json_channel.ChannelYouTube;
import com.gmail.fomichov.m.youtubeanalytics.request.ChannelsRequest;
import com.gmail.fomichov.m.youtubeanalytics.utils.CheckForChannel;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyDateUtils;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyFileUtils;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyLog;
import com.gmail.fomichov.m.youtubeanalytics.utils.TestInternet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class GlobalInfo extends Fragment {
    private ChannelsRequest channelsRequest;
    private Handler handle;
    private ChannelYouTube tube;
    boolean checkOk = true;
    private String channelId;
    private boolean cache;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.frag_globalinfo, container, false);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString((R.string.msgProgressDialog)));
        final EditText etChannelId = (EditText) myView.findViewById(R.id.etChannelId);
        final TextView tvChannelNameResult = (TextView) myView.findViewById(R.id.tvChannelNameResult);
        final TextView tvDateCreationChannelResult = (TextView) myView.findViewById(R.id.tvDateCreationChannelResult);
        final TextView tvNumberSubscribersResult = (TextView) myView.findViewById(R.id.tvNumberSubscribersResult);
        final TextView tvNumberVideosResult = (TextView) myView.findViewById(R.id.tvNumberVideosResult);
        final TextView tvNumberViewsResult = (TextView) myView.findViewById(R.id.tvNumberViewsResult);
        Button btnGetResult = (Button) myView.findViewById(R.id.btnGetResult);

        btnGetResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // проверяем включена ли настройка о чтении из файла, существует ли папка, есть ли файл с которого будем считывать информацию
                if (MainActivity.sharedPreferences.getBoolean("setSaveCache", false) && MyFileUtils.getFolder() && !MyFileUtils.findIdChannelFile(etChannelId.getText().toString())) {
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
                    Toast.makeText(getContext(), getResources().getString((R.string.dataCache)), Toast.LENGTH_SHORT).show();
                } else if (TestInternet.isOnline(getContext())) { // сработатет если есть интернет
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                                MainActivity.startTime = System.currentTimeMillis();
                            }
                            channelId = CheckForChannel.getChannelId(etChannelId.getText().toString());

                            if (channelId.equals(CheckForChannel.BADCHANNEL)) {
                                checkOk = false;
                                progressDialog.dismiss();
                                handle.sendMessage(handle.obtainMessage());
                            } else {
                                checkOk = true;
                                channelsRequest = new ChannelsRequest(etChannelId.getText().toString());
                                try {
                                    tube = channelsRequest.getSingleObject();
                                } catch (ExecutionException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                                cache = false; // текст будет синий
                                handle.sendMessage(handle.obtainMessage());
                            }
                        }
                    }).start();
                } else { // если нет интернета вывыедет тост
                    Toast.makeText(getContext(), getResources().getString((R.string.toastNoInternet)), Toast.LENGTH_SHORT).show();
                }

                handle = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        if (checkOk) {
                            try {
                                if (cache) {
                                    tvChannelNameResult.setTextColor(Color.RED);
                                    tvDateCreationChannelResult.setTextColor(Color.RED);
                                    tvNumberSubscribersResult.setTextColor(Color.RED);
                                    tvNumberVideosResult.setTextColor(Color.RED);
                                    tvNumberViewsResult.setTextColor(Color.RED);
                                } else {
                                    tvChannelNameResult.setTextColor(Color.BLUE);
                                    tvDateCreationChannelResult.setTextColor(Color.BLUE);
                                    tvNumberSubscribersResult.setTextColor(Color.BLUE);
                                    tvNumberVideosResult.setTextColor(Color.BLUE);
                                    tvNumberViewsResult.setTextColor(Color.BLUE);
                                }
                                tvChannelNameResult.setText(tube.items.get(0).snippet.title);
                                tvDateCreationChannelResult.setText(String.valueOf(MyDateUtils.convertStringToDate(tube.items.get(0).snippet.publishedAt)));
                                tvNumberSubscribersResult.setText(String.valueOf(tube.items.get(0).statistics.subscriberCount));
                                tvNumberVideosResult.setText(String.valueOf(tube.items.get(0).statistics.videoCount));
                                tvNumberViewsResult.setText(String.valueOf(tube.items.get(0).statistics.viewCount));
                                if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                                    MainActivity.endTime = System.currentTimeMillis();
                                    Toast.makeText(getContext(), "load data " + (MainActivity.endTime - MainActivity.startTime) + "ms", Toast.LENGTH_SHORT).show();
                                }
                                if (MainActivity.sharedPreferences.getBoolean("setSaveCache", false)) {
                                    if (!MyFileUtils.getFolder()) { // если папки еще нет то создаем, соответсвенно проверку на наличие такого же файла не проводим
                                        MyFileUtils.writeFileIdChannel(JSON.toJSONString(tube), etChannelId.getText().toString(), getContext(), getActivity());
                                    } else if (MyFileUtils.findIdChannelFile(etChannelId.getText().toString())) { // проверяем на наличие файла с таким названием, если нету то пишем файл
                                        MyFileUtils.writeFileIdChannel(JSON.toJSONString(tube), etChannelId.getText().toString(), getContext(), getActivity());
                                    }
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), getResources().getString((R.string.toastChannelNotFound)), Toast.LENGTH_SHORT).show();
                            tvChannelNameResult.setText("");
                            tvDateCreationChannelResult.setText("");
                            tvNumberSubscribersResult.setText("");
                            tvNumberVideosResult.setText("");
                            tvNumberViewsResult.setText("");
                        }
                    }
                };
            }
        });
        return myView;
    }

    public static GlobalInfo newInstance() {
        return new GlobalInfo();
    }
}
