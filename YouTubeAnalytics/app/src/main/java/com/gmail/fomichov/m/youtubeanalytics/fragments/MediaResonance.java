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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.R;
import com.gmail.fomichov.m.youtubeanalytics.json.json_channel.ChannelYouTube;
import com.gmail.fomichov.m.youtubeanalytics.request.ChannelsRequest;
import com.gmail.fomichov.m.youtubeanalytics.request.CommentsRequest;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyDateUtils;
import com.gmail.fomichov.m.youtubeanalytics.utils.TestInternet;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

public class MediaResonance extends Fragment {
    private ChannelsRequest channelsRequest;
    private Handler handle;
    private ChannelYouTube tube;
    private int countComments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View myView = inflater.inflate(R.layout.frag_mediaresonance, container, false);
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString((R.string.msgProgressDialog)));
        final EditText etChannelId = (EditText) myView.findViewById(R.id.etChannelId);
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
                if (TestInternet.isOnline(getContext())) {
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
                                countComments = new CommentsRequest().getCountComment(tube.items.get(0).contentDetails.relatedPlaylists.uploads);
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
                                tvChannelNameResult.setText(tube.items.get(0).snippet.title);
                                tvDateCreationChannelResult.setText(String.valueOf(MyDateUtils.convertStringToDate(tube.items.get(0).snippet.publishedAt)));
                                tvNumberSubscribersResult.setText(String.valueOf(tube.items.get(0).statistics.subscriberCount));
                                tvNumberVideosResult.setText(String.valueOf(tube.items.get(0).statistics.videoCount));
                                tvNumberViewsResult.setText(String.valueOf(tube.items.get(0).statistics.viewCount));
                                tvNumberCommentsResult.setText(String.valueOf(countComments));
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

    public static MediaResonance newInstance() {
        MediaResonance fragment = new MediaResonance();
        return fragment;
    }
}
