package com.gmail.fomichov.m.youtubeanalytics.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.fomichov.m.youtubeanalytics.R;
import com.gmail.fomichov.m.youtubeanalytics.json.json_channel.ChannelYouTube;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyDateUtils;

import java.text.ParseException;
import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {
    private List<ChannelYouTube> listItems;
    private Context context;
    private Boolean typeTask;

    public MyRecyclerAdapter(List<ChannelYouTube> listItems, Context context, Boolean typeTask) {
        this.listItems = listItems;
        this.context = context;
        this.typeTask = typeTask;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (typeTask) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_media_sort, parent, false); // создаем новый вид
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_global_sort, parent, false); // создаем новый вид
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        ChannelYouTube itemList = listItems.get(i);
        viewHolder.tvChannelNameResult.setText(context.getResources().getString(R.string.chName) + " " + itemList.items.get(0).snippet.title);
        try {
            viewHolder.tvDateCreationChannelResult.setText(context.getResources().getString(R.string.chDate) + " " + (MyDateUtils.convertStringToDate(itemList.items.get(0).snippet.publishedAt)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (itemList.cache) {
            viewHolder.tvChannelNameResult.setTextColor(Color.RED);
            viewHolder.tvDateCreationChannelResult.setTextColor(Color.RED);
            viewHolder.tvNumberSubscribersResult.setTextColor(Color.RED);
            viewHolder.tvNumberVideosResult.setTextColor(Color.RED);
            viewHolder.tvNumberViewsResult.setTextColor(Color.RED);
            if (typeTask) {
                viewHolder.tvNumberCommentsResult.setTextColor(Color.RED);
            }
        } else {
            viewHolder.tvChannelNameResult.setTextColor(Color.BLUE);
            viewHolder.tvDateCreationChannelResult.setTextColor(Color.BLUE);
            viewHolder.tvNumberSubscribersResult.setTextColor(Color.BLUE);
            viewHolder.tvNumberVideosResult.setTextColor(Color.BLUE);
            viewHolder.tvNumberViewsResult.setTextColor(Color.BLUE);
            if (typeTask) {
                viewHolder.tvNumberCommentsResult.setTextColor(Color.BLUE);
            }
        }
        viewHolder.tvNumberSubscribersResult.setText(context.getResources().getString(R.string.chSubs) + " " + (itemList.items.get(0).statistics.subscriberCount));
        viewHolder.tvNumberVideosResult.setText(context.getResources().getString(R.string.chVideo) + " " + (itemList.items.get(0).statistics.videoCount));
        viewHolder.tvNumberViewsResult.setText(context.getResources().getString(R.string.chView) + " " + (itemList.items.get(0).statistics.viewCount));
        if (typeTask) {
            viewHolder.tvNumberCommentsResult.setText(context.getResources().getString(R.string.chComment) + " " + (itemList.countComments));
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChannelNameResult;
        private TextView tvDateCreationChannelResult;
        private TextView tvNumberSubscribersResult;
        private TextView tvNumberVideosResult;
        private TextView tvNumberViewsResult;
        private TextView tvNumberCommentsResult;

        private ViewHolder(final View itemView) {
            super(itemView);
            tvChannelNameResult = (TextView) itemView.findViewById(R.id.tvChannelNameResult);
            tvDateCreationChannelResult = (TextView) itemView.findViewById(R.id.tvDateCreationChannelResult);
            tvNumberSubscribersResult = (TextView) itemView.findViewById(R.id.tvNumberSubscribersResult);
            tvNumberVideosResult = (TextView) itemView.findViewById(R.id.tvNumberVideosResult);
            tvNumberViewsResult = (TextView) itemView.findViewById(R.id.tvNumberViewsResult);
            tvNumberCommentsResult = (TextView) itemView.findViewById(R.id.tvNumberCommentsResult);
        }
    }
}
