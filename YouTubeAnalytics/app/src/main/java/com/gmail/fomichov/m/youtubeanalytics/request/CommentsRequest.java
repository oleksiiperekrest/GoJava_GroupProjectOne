package com.gmail.fomichov.m.youtubeanalytics.request;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.json.json_playlist.Playlist;
import com.gmail.fomichov.m.youtubeanalytics.json.json_video.VideoList;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyLog;

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

public class CommentsRequest {
    private final String HTTP_URL_PARSE_PLAYLIST = "https://www.googleapis.com/youtube/v3/playlistItems";
    private final String HTTP_URL_PARSE_VIDEO = "https://www.googleapis.com/youtube/v3/videos";
    private OkHttpClient client = new OkHttpClient();
    private String nextPageToken = "";

    public CommentsRequest() {
    }

    // получаем все айди видео из плейлиста
    private Playlist getListIdVideo(final String nextPageToken, final String idPlayList) throws ExecutionException, InterruptedException {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                String json = null;
                HttpUrl.Builder urlBuilder = HttpUrl.parse(HTTP_URL_PARSE_PLAYLIST).newBuilder();
                urlBuilder.addQueryParameter("part", "contentDetails");
                urlBuilder.addQueryParameter("playlistId", idPlayList);
                urlBuilder.addQueryParameter("maxResults", "50");
                urlBuilder.addQueryParameter("pageToken", nextPageToken);
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
        return JSON.parseObject(futureTask.get(), Playlist.class);
    }

    // получаем все обьекты, которые содержат комментарии к каждому видео
    private List<VideoList> getVideoListId(String idPlayList) throws ExecutionException, InterruptedException {
        final List<String> videoIdArray = getListIdVideo(getNamePlayList(idPlayList));
        List<VideoList> videoLists = new ArrayList<>();
        final List<FutureTask> taskList = new ArrayList<>();
        final ExecutorService threadPool = Executors.newFixedThreadPool(20);
        for (int i = 0; i < videoIdArray.size(); i++) {
            final int finalI = i;
            final FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    String json = null;
                    HttpUrl.Builder urlBuilder = HttpUrl.parse(HTTP_URL_PARSE_VIDEO).newBuilder();
                    urlBuilder.addQueryParameter("part", "statistics");
                    urlBuilder.addQueryParameter("id", videoIdArray.get(finalI));
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
            videoLists.add(JSON.parseObject((String) value.get(), VideoList.class));
        }
        return videoLists;
    }

    // получаем объекты в которых хранится айди видео (все видео из этого плейлиста)
    private List<Playlist> getNamePlayList(String idPlayList) throws ExecutionException, InterruptedException {
        List<Playlist> list = new ArrayList<>();
        int temp = 0;
        do {
            try {
                list.add(getListIdVideo(nextPageToken, idPlayList));
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            nextPageToken = list.get(temp).nextPageToken;
            MyLog.showLog(temp + " - " + nextPageToken);
            temp++;
        } while (nextPageToken != null);
        return list;
    }

    // сводим все айди видео в один массив стрингов
    private List<String> getListIdVideo(List<Playlist> list) throws ExecutionException, InterruptedException {
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < list.get(i).items.size(); j++) {
                stringList.add(list.get(i).items.get(j).contentDetails.videoId);
            }
        }
        return stringList;
    }

    // суммируем комменты
    public int getCountComment(String idPlayList) throws ExecutionException, InterruptedException {
        List<VideoList> listIdVideoComment = getVideoListId(idPlayList);
        int count = 0;
        for (int i = 0; i < listIdVideoComment.size(); i++) {
            count += listIdVideoComment.get(i).items.get(0).statistics.commentCount;
        }
        return count;
    }
}

