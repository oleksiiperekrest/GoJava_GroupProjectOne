package com.gmail.fomichov.m.youtubeanalytics.request;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.json.json_channel.ChannelYouTube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChannelsRequest {
    private final String HTTP_URL_PARSE = "https://www.googleapis.com/youtube/v3/channels";
    private String idChannel;
    private OkHttpClient client = new OkHttpClient();

    public ChannelsRequest() {
    }

    public ChannelsRequest(String idChannel) {
        this.idChannel = idChannel;
    }

    public ChannelYouTube getSingleObject() throws ExecutionException, InterruptedException {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String json = null;
                HttpUrl.Builder urlBuilder = HttpUrl.parse(HTTP_URL_PARSE).newBuilder();
                urlBuilder.addQueryParameter("part", "snippet,contentDetails,statistics");
                urlBuilder.addQueryParameter("id", idChannel);
                urlBuilder.addQueryParameter("key", MainActivity.KEY_YOUTUBE_API);
                Request request = new Request.Builder()
                        .url(urlBuilder.build().toString())
                        .build();
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
                return json;
            }
        });
        new Thread(futureTask).start();
        return JSON.parseObject(futureTask.get(), ChannelYouTube.class);
    }

    public List<ChannelYouTube> getArrayObject(final List<String> channelIdArray, Boolean comments) throws ExecutionException, InterruptedException {
        final List<ChannelYouTube> tubeList = new ArrayList<>();
        List<FutureTask> taskList = new ArrayList<>();
        final ExecutorService threadPool = Executors.newFixedThreadPool(20);
        for (int i = 0; i < channelIdArray.size(); i++) {
            final int finalI = i;
            FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String json = null;
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(HTTP_URL_PARSE).newBuilder();
                    urlBuilder.addQueryParameter("part", "snippet,contentDetails,statistics");
                    urlBuilder.addQueryParameter("id", channelIdArray.get(finalI));
                    urlBuilder.addQueryParameter("key", MainActivity.KEY_YOUTUBE_API);
                    Request request = new Request.Builder()
                            .url(urlBuilder.build().toString())
                            .build();
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
                    return json;
                }
            });
            taskList.add(futureTask);
            threadPool.submit(futureTask);
        }
        threadPool.shutdown();
        for (FutureTask value : taskList) {
            tubeList.add(JSON.parseObject((String) value.get(), ChannelYouTube.class));
        }

        // записываем в лист обьектов еще и количество коментов по каждому афди каналу, для последующего вывода через рециклер
        if (comments) {
            List<Integer> list = getListComments(tubeList);
            for (int i = 0; i < list.size(); i++) {
                tubeList.get(i).setCountComments(list.get(i));
            }
        }

        return tubeList;
    }

    private List<Integer> getListComments(final List<ChannelYouTube> tubeList) throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(20);
        List<FutureTask> taskList = new ArrayList<>();
        List<Integer> countComments = new ArrayList<>();
        for (int i = 0; i < tubeList.size(); i++) {
            final int finalI = i;
            FutureTask<Integer> integerFutureTask = new FutureTask<Integer>(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    return new CommentsRequest().getCountComment(tubeList.get(finalI).items.get(0).contentDetails.relatedPlaylists.uploads);
                }
            });
            taskList.add(integerFutureTask);
            threadPool.submit(integerFutureTask);
        }
        threadPool.shutdown();
        for (FutureTask value : taskList) {
            countComments.add((Integer) value.get());
        }
        return countComments;
    }
}

