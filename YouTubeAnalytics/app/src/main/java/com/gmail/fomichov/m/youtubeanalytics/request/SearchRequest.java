package com.gmail.fomichov.m.youtubeanalytics.request;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.json.json_search.SearchResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchRequest {
    private final String HTTP_URL_PARSE = "https://www.googleapis.com/youtube/v3/search";
    private String request;
    private int maxResults = 7;

    public SearchRequest(String request) {
        this.request = request;
    }

    public SearchResponse getSearchResponse() throws ExecutionException, InterruptedException {

                String json = null;
                HttpUrl.Builder urlBuilder = HttpUrl.parse(HTTP_URL_PARSE).newBuilder();
                urlBuilder.addQueryParameter("part", "snippet");
                urlBuilder.addQueryParameter("maxResults", "" + maxResults);
                urlBuilder.addQueryParameter("q", request);
                urlBuilder.addQueryParameter("key", MainActivity.KEY_YOUTUBE_API);
                Request request = new Request.Builder()
                        .url(urlBuilder.build().toString())
                        .build();
                OkHttpClient client = new OkHttpClient();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }

        return JSON.parseObject(json, SearchResponse.class);

    }
}
