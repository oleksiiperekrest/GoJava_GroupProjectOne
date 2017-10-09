package com.gmail.fomichov.m.youtubeanalytics.fragments;

import android.app.ProgressDialog;
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

import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.R;
import com.gmail.fomichov.m.youtubeanalytics.json.json_channel.ChannelYouTube;
import com.gmail.fomichov.m.youtubeanalytics.request.ChannelsRequest;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyDateUtils;
import com.gmail.fomichov.m.youtubeanalytics.utils.TestInternet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GlobalInfoCompare extends Fragment {
    private Handler handle;
    private List<String> channelIdArray;
    private List<ChannelYouTube> tubeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View myView = inflater.inflate(R.layout.frag_globalinfo_compare, container, false);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString((R.string.msgProgressDialog)));
        final EditText etChannelIdOne = (EditText) myView.findViewById(R.id.etChannelIdOne);
        final EditText etChannelIdTwo = (EditText) myView.findViewById(R.id.etChannelIdTwo);
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
        Button btnGetResult = (Button) myView.findViewById(R.id.btnGetResult);
        channelIdArray = new ArrayList<>();
        tubeList = new ArrayList<>();

        btnGetResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TestInternet.isOnline(getContext())) {
                    progressDialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                                MainActivity.startTime = System.currentTimeMillis();
                            }
                            channelIdArray.add(etChannelIdOne.getText().toString());
                            channelIdArray.add(etChannelIdTwo.getText().toString());
                            ChannelsRequest channelsRequest = new ChannelsRequest();
                            try {
                                tubeList.addAll(channelsRequest.getArrayObject(channelIdArray, false));
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                            handle.sendMessage(handle.obtainMessage());
                        }
                    }).start();
                    handle = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            try {
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
                                if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                                    MainActivity.endTime = System.currentTimeMillis();
                                    Toast.makeText(getContext(), "load data " + (MainActivity.endTime - MainActivity.startTime) + "ms", Toast.LENGTH_LONG).show();
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                } else {
                    Toast.makeText(getContext(), getResources().getString((R.string.toastNoInternet)), Toast.LENGTH_LONG).show();
                }
            }
        });
        return myView;
    }

    public static GlobalInfoCompare newInstance() {
        return new GlobalInfoCompare();
    }
}
