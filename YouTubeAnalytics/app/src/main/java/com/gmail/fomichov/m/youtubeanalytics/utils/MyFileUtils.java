package com.gmail.fomichov.m.youtubeanalytics.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.json.json_channel.ChannelYouTube;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MyFileUtils {
    private static int REQUEST_WRITE_EXTERNAL_STORAGE = 2;

    // проверяем разрешения на запись файла
    public static void writeFileIdChannel(final String json, final String idChannel, final Context context, final Activity activity) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
        } else {
            if (isExternalStorageWritable()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ChannelYouTube temp = JSON.parseObject(json, ChannelYouTube.class);
                        temp.setCache(true);
                        String tempJSON = JSON.toJSONString(temp);
                        saveData(tempJSON, idChannel, context, activity);
                    }
                }).start();
            }
        }
    }

    // Проверяет, доступно ли внешнее хранилище для чтения и записи
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // создаем каталог и файл
    private static void saveData(String json, final String idChannel, final Context context, Activity activity) {
        final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YouTube Analytics/" + MainActivity.sharedPreferences.getString("setNameFolder", ""));
        if (!file.mkdirs()) {
            MyLog.showLog("Directory not created");
        }

        File patch = new File(file, idChannel + ".txt");
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(patch));
            bw.write(json);
            bw.close();
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(context, "Файл " + idChannel + " создан в Documents/YouTube Analytics/" + MainActivity.sharedPreferences.getString("setNameFolder", ""), Toast.LENGTH_LONG).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // получаем список файлов в папке и возращаем булеан если файл найден или нет
    public static boolean findIdChannelFile(String nameFile) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YouTube Analytics/" + MainActivity.sharedPreferences.getString("setNameFolder", ""));
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.getName().equals(nameFile + ".txt")) {
                return false;
            }
        }
        return true;
    }

    // по айди канала считываем данные и возвращаем обьект
    public static ChannelYouTube getDataIdChannel(String channelId) throws Exception {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YouTube Analytics/" + MainActivity.sharedPreferences.getString("setNameFolder", ""));
        File file = new File(path, channelId + ".txt");
        FileInputStream stream = new FileInputStream(file);
        String json = convertStreamToString(stream);
        MyLog.showLog(json);
        MyLog.showLog("" + file);
        stream.close();
        return JSON.parseObject(json, ChannelYouTube.class);
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static boolean getFolder(){
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "YouTube Analytics/" + MainActivity.sharedPreferences.getString("setNameFolder", ""));
        return directory.exists();
    }
}
