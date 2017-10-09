package com.gmail.fomichov.m.youtubeanalytics.json.json_channel;

import java.util.List;

public class ChannelYouTube {
    public List<ChannelItems> items;
    public int countComments;
    public boolean cache;



    public void setCountComments(int countComments) {
        this.countComments = countComments;
    }

    public void setCache(boolean cache) {
        this.cache = cache;
    }
}
