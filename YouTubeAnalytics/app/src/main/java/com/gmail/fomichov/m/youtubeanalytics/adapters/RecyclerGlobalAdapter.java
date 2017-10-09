package com.gmail.fomichov.m.youtubeanalytics.adapters;

import android.content.Context;
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

public class RecyclerGlobalAdapter extends RecyclerView.Adapter<RecyclerGlobalAdapter.ViewHolder> {
    private List<ChannelYouTube> listItems;
    private Context context;

    public RecyclerGlobalAdapter(List<ChannelYouTube> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void setListItems(List<ChannelYouTube> listItems) {
        this.listItems = listItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_global_sort, parent, false); // создаем новый вид
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
        viewHolder.tvNumberSubscribersResult.setText(context.getResources().getString(R.string.chSubs) + " " + (itemList.items.get(0).statistics.subscriberCount));
        viewHolder.tvNumberVideosResult.setText(context.getResources().getString(R.string.chVideo) + " " + (itemList.items.get(0).statistics.videoCount));
        viewHolder. tvNumberViewsResult.setText(context.getResources().getString(R.string.chView) + " " + (itemList.items.get(0).statistics.viewCount));
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

        private ViewHolder(final View itemView) {
            super(itemView);
            tvChannelNameResult = (TextView) itemView.findViewById(R.id.tvChannelNameResult);
            tvDateCreationChannelResult = (TextView) itemView.findViewById(R.id.tvDateCreationChannelResult);
            tvNumberSubscribersResult = (TextView) itemView.findViewById(R.id.tvNumberSubscribersResult);
            tvNumberVideosResult = (TextView) itemView.findViewById(R.id.tvNumberVideosResult);
            tvNumberViewsResult = (TextView) itemView.findViewById(R.id.tvNumberViewsResult);
        }
    }
}
