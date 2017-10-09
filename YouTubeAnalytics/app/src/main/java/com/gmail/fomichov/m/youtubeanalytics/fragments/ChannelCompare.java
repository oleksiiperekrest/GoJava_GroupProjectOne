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
import com.gmail.fomichov.m.youtubeanalytics.utils.TestInternet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChannelCompare extends Fragment {
    private Handler handle;
    private List<String> channelIdArray;
    private List<ChannelYouTube> tubeList;
    private boolean cacheOne;
    private boolean cacheTwo;
    private EditText etChannelIdOne;
    private EditText etChannelIdTwo;
    private static final String TYPE_TASK = "typeTask";
    private View myView;
    private boolean typeTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        typeTask = getArguments().getBoolean("typeTask");
        if (typeTask) {
            myView = inflater.inflate(R.layout.frag_mediaresonance_compare, container, false);
        } else {
            myView = inflater.inflate(R.layout.frag_globalinfo_compare, container, false);
        }
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString((R.string.msgProgressDialog)));
        etChannelIdOne = (EditText) myView.findViewById(R.id.etChannelIdOne);
        etChannelIdTwo = (EditText) myView.findViewById(R.id.etChannelIdTwo);
        final TextView tvChannelNameResultOne = (TextView) myView.findViewById(R.id.tvChannelNameResultOne);
        final TextView tvChannelNameResultTwo = (TextView) myView.findViewById(R.id.tvChannelNameResultTwo);
        final TextView tvDateCreationChannelResultOne = (TextView) myView.findViewById(R.id.tvDateCreationChannelResultOne);
        final TextView tvDateCreationChannelResultTwo = (TextView) myView.findViewById(R.id.tvDateCreationChannelResultTwo);
        final TextView tvNumberSubscribersResultOne = (TextView) myView.findViewById(R.id.tvNumberSubscribersResultOne);
        final TextView tvNumberSubscribersResultTwo = (TextView) myView.findViewById(R.id.tvNumberSubscribersResultTwo);
        final TextView tvNumberVideosResultOne = (TextView) myView.findViewById(R.id.tvNumberVideosResultOne);
        final TextView tvNumberVideosResultTwo = (TextView) myView.findViewById(R.id.tvNumberVideosResultTwo);
        final TextView tvNumberViewsResultOne = (TextView) myView.findViewById(R.id.tvNumberViewsResultOne);
        final TextView tvNumberViewsResultTwo = (TextView) myView.findViewById(R.id.tvNumberViewsResultTwo);
        final TextView tvNumberCommentsResultOne = (TextView) myView.findViewById(R.id.tvNumberCommentsResultOne);
        final TextView tvNumberCommentsResultTwo = (TextView) myView.findViewById(R.id.tvNumberCommentsResultTwo);
        Button btnGetResult = (Button) myView.findViewById(R.id.btnGetResult);
        channelIdArray = new ArrayList<>();
        tubeList = new ArrayList<>();
        channelIdArray.add(etChannelIdOne.getText().toString());
        channelIdArray.add(etChannelIdTwo.getText().toString());

        btnGetResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MainActivity.sharedPreferences.getBoolean("setSaveCache", false) && MyFileUtils.getFolder()) {
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                boolean typeTaskIf;
                                if (typeTask) {
                                    typeTaskIf = !MainActivity.sharedPreferences.getBoolean("setSaveTime", false) && getCountCommentOne();
                                } else {
                                    typeTaskIf = !MainActivity.sharedPreferences.getBoolean("setSaveTime", false);
                                }
                                if (typeTaskIf) {
                                    MainActivity.startTime = System.currentTimeMillis();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            if (!MyFileUtils.findIdChannelFile(channelIdArray.get(0))) {
                                try {
                                    tubeList.add(0, MyFileUtils.getDataIdChannel(channelIdArray.get(0)));
                                    cacheOne = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                ChannelsRequest channelsRequestOne = new ChannelsRequest(channelIdArray.get(0));
                                try {
                                    tubeList.add(0, channelsRequestOne.getSingleObject());
                                    if (typeTask) {
                                        tubeList.get(0).setCountComments(new CommentsRequest().getCountComment(tubeList.get(0).items.get(0).contentDetails.relatedPlaylists.uploads));
                                    }
                                    cacheOne = false;
                                } catch (ExecutionException | InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            try {
                                boolean typeTaskIf;
                                if (typeTask) {
                                    typeTaskIf = !MyFileUtils.findIdChannelFile(channelIdArray.get(1)) && getCountCommentTwo();
                                } else {
                                    typeTaskIf = !MyFileUtils.findIdChannelFile(channelIdArray.get(1));
                                }
                                if (typeTaskIf) {
                                    try {
                                        tubeList.add(1, MyFileUtils.getDataIdChannel(channelIdArray.get(1)));
                                        cacheTwo = true;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ChannelsRequest channelsRequestTwo = new ChannelsRequest(channelIdArray.get(1));
                                    try {
                                        tubeList.add(1, channelsRequestTwo.getSingleObject());
                                        if (typeTask) {
                                            tubeList.get(1).setCountComments(new CommentsRequest().getCountComment(tubeList.get(1).items.get(0).contentDetails.relatedPlaylists.uploads));
                                        }
                                        cacheTwo = false;
                                    } catch (ExecutionException | InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
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
                            ChannelsRequest channelsRequest = new ChannelsRequest();

                            try {
                                if(typeTask){
                                    tubeList.addAll(channelsRequest.getArrayObject(channelIdArray, true));
                                } else {
                                    tubeList.addAll(channelsRequest.getArrayObject(channelIdArray, false));
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            cacheOne = false;
                            cacheTwo = false;
                            handle.sendMessage(handle.obtainMessage());
                        }
                    }).start();
                } else {
                    Toast.makeText(getContext(), getResources().getString((R.string.toastNoInternet)), Toast.LENGTH_LONG).show();
                }
                handle = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        try {
                            if (cacheOne) {
                                tvChannelNameResultOne.setTextColor(Color.RED);
                                tvDateCreationChannelResultOne.setTextColor(Color.RED);
                                tvNumberSubscribersResultOne.setTextColor(Color.RED);
                                tvNumberVideosResultOne.setTextColor(Color.RED);
                                tvNumberViewsResultOne.setTextColor(Color.RED);
                                if (typeTask) {
                                    tvNumberCommentsResultOne.setTextColor(Color.RED);
                                }
                            } else {
                                tvChannelNameResultOne.setTextColor(Color.BLUE);
                                tvDateCreationChannelResultOne.setTextColor(Color.BLUE);
                                tvNumberSubscribersResultOne.setTextColor(Color.BLUE);
                                tvNumberVideosResultOne.setTextColor(Color.BLUE);
                                tvNumberViewsResultOne.setTextColor(Color.BLUE);
                                if (typeTask) {
                                    tvNumberCommentsResultOne.setTextColor(Color.BLUE);
                                }
                            }
                            if (cacheTwo) {
                                tvChannelNameResultTwo.setTextColor(Color.RED);
                                tvDateCreationChannelResultTwo.setTextColor(Color.RED);
                                tvNumberSubscribersResultTwo.setTextColor(Color.RED);
                                tvNumberVideosResultTwo.setTextColor(Color.RED);
                                tvNumberViewsResultTwo.setTextColor(Color.RED);
                                if (typeTask) {
                                    tvNumberCommentsResultTwo.setTextColor(Color.RED);
                                }
                            } else {
                                tvChannelNameResultTwo.setTextColor(Color.BLUE);
                                tvDateCreationChannelResultTwo.setTextColor(Color.BLUE);
                                tvNumberSubscribersResultTwo.setTextColor(Color.BLUE);
                                tvNumberVideosResultTwo.setTextColor(Color.BLUE);
                                tvNumberViewsResultTwo.setTextColor(Color.BLUE);
                                if (typeTask) {
                                    tvNumberCommentsResultTwo.setTextColor(Color.BLUE);
                                }
                            }
                            tvChannelNameResultOne.setText(tubeList.get(0).items.get(0).snippet.title);
                            tvChannelNameResultTwo.setText(tubeList.get(1).items.get(0).snippet.title);
                            tvDateCreationChannelResultOne.setText(String.valueOf(MyDateUtils.convertStringToDate(tubeList.get(0).items.get(0).snippet.publishedAt)));
                            tvDateCreationChannelResultTwo.setText(String.valueOf(MyDateUtils.convertStringToDate(tubeList.get(1).items.get(0).snippet.publishedAt)));
                            tvNumberSubscribersResultOne.setText(String.valueOf(tubeList.get(0).items.get(0).statistics.subscriberCount));
                            tvNumberSubscribersResultTwo.setText(String.valueOf(tubeList.get(1).items.get(0).statistics.subscriberCount));
                            tvNumberVideosResultOne.setText(String.valueOf(tubeList.get(0).items.get(0).statistics.videoCount));
                            tvNumberVideosResultTwo.setText(String.valueOf(tubeList.get(1).items.get(0).statistics.videoCount));
                            tvNumberViewsResultOne.setText(String.valueOf(tubeList.get(0).items.get(0).statistics.viewCount));
                            tvNumberViewsResultTwo.setText(String.valueOf(tubeList.get(1).items.get(0).statistics.viewCount));
                            if (typeTask) {
                                tvNumberCommentsResultOne.setText(String.valueOf(tubeList.get(0).countComments));
                                tvNumberCommentsResultTwo.setText(String.valueOf(tubeList.get(1).countComments));
                            }
                            if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                                MainActivity.endTime = System.currentTimeMillis();
                                Toast.makeText(getContext(), "load data " + (MainActivity.endTime - MainActivity.startTime) + "ms", Toast.LENGTH_LONG).show();
                            }
                            if (MainActivity.sharedPreferences.getBoolean("setSaveCache", false)) {
                                if (!MyFileUtils.getFolder()) { // если папки еще нет то создаем, соответсвенно проверку на наличие такого же файла не проводим
                                    MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(0)), channelIdArray.get(0), getContext(), getActivity());
                                    MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(1)), channelIdArray.get(1), getContext(), getActivity());
                                } else {
                                    if (MyFileUtils.findIdChannelFile(channelIdArray.get(0))) { // проверяем на наличие файла с таким названием, если нету то пишем файл
                                        MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(0)), channelIdArray.get(0), getContext(), getActivity());
                                        if (typeTask) {
                                            if (!MyFileUtils.findIdChannelFile(channelIdArray.get(0)) && !getCountCommentOne()) {
                                                MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(0)), channelIdArray.get(0), getContext(), getActivity());
                                            }
                                        }
                                    }
                                    if (MyFileUtils.findIdChannelFile(channelIdArray.get(1))) { // проверяем на наличие файла с таким названием, если нету то пишем файл
                                        MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(1)), channelIdArray.get(1), getContext(), getActivity());
                                    }
                                    if (typeTask) {
                                        if (!MyFileUtils.findIdChannelFile(channelIdArray.get(1)) && !getCountCommentTwo()) {
                                            MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(1)), channelIdArray.get(1), getContext(), getActivity());
                                        }
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

    private boolean getCountCommentOne() throws Exception {
        return (MyFileUtils.getDataIdChannel(etChannelIdOne.getText().toString()).countComments > 0);
    }

    private boolean getCountCommentTwo() throws Exception {
        return (MyFileUtils.getDataIdChannel(etChannelIdTwo.getText().toString()).countComments > 0);
    }

    public static ChannelCompare newInstance(Boolean typeTask) {
        ChannelCompare fragment = new ChannelCompare();
        Bundle args = new Bundle();
        args.putBoolean(TYPE_TASK, typeTask);
        fragment.setArguments(args);
        return fragment;
    }
}
