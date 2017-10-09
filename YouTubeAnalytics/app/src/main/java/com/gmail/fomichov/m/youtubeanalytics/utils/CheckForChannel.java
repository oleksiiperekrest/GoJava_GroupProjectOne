package com.gmail.fomichov.m.youtubeanalytics.utils;

import com.gmail.fomichov.m.youtubeanalytics.json.json_search.SearchResponse;
import com.gmail.fomichov.m.youtubeanalytics.request.SearchRequest;

public class CheckForChannel {
    public static final String BADCHANNEL = "#ChannelNotFound";

    static String lookupChannel(SearchResponse searchResponse) {

        String channelId = BADCHANNEL;
        if (searchResponse.pageInfo.totalResults == 0) return channelId;
        for (int i = 0; i < searchResponse.items.size(); i++) {
            if (searchResponse.items.get(i).id.kind.equals("youtube#channel")) {
                channelId = searchResponse.items.get(i).id.channelId;
                break;
            }
        }
        return channelId;
    }

    public static String getChannelId(String id) {
        String channelId = BADCHANNEL;
        try {
            channelId = CheckForChannel.lookupChannel(new SearchRequest(id).getSearchResponse());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelId;
    }
}

