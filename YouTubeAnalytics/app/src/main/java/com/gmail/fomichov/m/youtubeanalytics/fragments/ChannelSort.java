package com.gmail.fomichov.m.youtubeanalytics.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.gmail.fomichov.m.youtubeanalytics.MainActivity;
import com.gmail.fomichov.m.youtubeanalytics.R;
import com.gmail.fomichov.m.youtubeanalytics.adapters.MyRecyclerAdapter;
import com.gmail.fomichov.m.youtubeanalytics.dialogs.DialogArray;
import com.gmail.fomichov.m.youtubeanalytics.json.json_channel.ChannelYouTube;
import com.gmail.fomichov.m.youtubeanalytics.request.ChannelsRequest;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyFileUtils;
import com.gmail.fomichov.m.youtubeanalytics.utils.TestInternet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ChannelSort extends Fragment {
    private List<String> channelIdArray;
    private List<ChannelYouTube> tubeList;
    private MyRecyclerAdapter adapter;
    private ProgressDialog progressDialog;
    private Handler handle;
    private Spinner spinner;
    private LinearLayout llSpinnerChoice;
    private RecyclerView recyclerView;
    private static final String TYPE_TASK = "typeTask";
    private View myView;
    private boolean typeTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        typeTask = getArguments().getBoolean("typeTask");
        if (typeTask) {
            myView = inflater.inflate(R.layout.frag_mediaresonance_sort, container, false);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("pressButtonOkInDialogMedia"));
        } else {
            myView = inflater.inflate(R.layout.frag_globalinfo_sort, container, false);
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, new IntentFilter("pressButtonOkInDialogGlobal"));
        }
        channelIdArray = new ArrayList<>();
        tubeList = new ArrayList<>();
        spinner = (Spinner) myView.findViewById(R.id.spTypeSort);
        Button btnAddArray = (Button) myView.findViewById(R.id.btnAddArray);
        Button btnLoadExample = (Button) myView.findViewById(R.id.btnLoadExample);
        llSpinnerChoice = (LinearLayout) myView.findViewById(R.id.llSpinnerChoice);

        btnAddArray.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogArray();
            }
        });

        btnLoadExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                channelIdArray.clear();
                channelIdArray.add("UCt7sv-NKh44rHAEb-qCCxvA");
                channelIdArray.add("UCZzCY2vYd-B2mtk9ZYgbGug");
                channelIdArray.add("UCDbsY8C1eQJ5t6KBv9ds-ag");
                channelIdArray.add("UCnAmkiIpUXkVOY1A1r-zE6w");
                channelIdArray.add("UCmbzthMYaX8FAe_cZSrGMrA");
                channelIdArray.add("UC5s8uAm1UgUetX3exGGDZCw");
                channelIdArray.add("UCkp0Tc7ll67bChomTyB1ezQ");
                channelIdArray.add("UCHFNDph3zTYh8-hwofOVXsg");
                channelIdArray.add("UCSpU8Y1aoqBSAwh8DBpiM9A");
                channelIdArray.add("UCRP4EhX1Op-jL7D87PB3qhQ");
                channelIdArray.add("UCBa659QWEk1AI4Tg--mrJ2A");
                try {
                    startLoadData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        recyclerView = (RecyclerView) myView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyRecyclerAdapter(tubeList, getContext(), typeTask);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] choose = getResources().getStringArray(R.array.sort_array);
                switch (position) {
                    case 0:
                        Collections.sort(tubeList, new Comparator<ChannelYouTube>() {
                            public int compare(ChannelYouTube obj1, ChannelYouTube obj2) {
                                return obj1.items.get(0).snippet.title.compareToIgnoreCase(obj2.items.get(0).snippet.title);
                            }
                        });
                        break;
                    case 1:
                        Collections.sort(tubeList, new Comparator<ChannelYouTube>() {
                            public int compare(ChannelYouTube obj1, ChannelYouTube obj2) {
                                return obj1.items.get(0).snippet.publishedAt.compareToIgnoreCase(obj2.items.get(0).snippet.publishedAt);
                            }
                        });
                        break;
                    case 2:
                        Collections.sort(tubeList, new Comparator<ChannelYouTube>() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            public int compare(ChannelYouTube obj1, ChannelYouTube obj2) {
                                return Integer.compare(obj1.items.get(0).statistics.subscriberCount, obj2.items.get(0).statistics.subscriberCount);
                            }
                        });
                        break;
                    case 3:
                        Collections.sort(tubeList, new Comparator<ChannelYouTube>() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            public int compare(ChannelYouTube obj1, ChannelYouTube obj2) {
                                return Integer.compare(obj1.items.get(0).statistics.videoCount, obj2.items.get(0).statistics.videoCount);
                            }
                        });
                        break;
                    case 4:
                        Collections.sort(tubeList, new Comparator<ChannelYouTube>() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            public int compare(ChannelYouTube obj1, ChannelYouTube obj2) {
                                return Integer.compare(obj1.items.get(0).statistics.viewCount, obj2.items.get(0).statistics.viewCount);
                            }
                        });
                        break;
                    case 5:
                        Collections.sort(tubeList, new Comparator<ChannelYouTube>() {
                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                            public int compare(ChannelYouTube obj1, ChannelYouTube obj2) {
                                return Integer.compare(obj1.countComments, obj2.countComments);
                            }
                        });
                        break;
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        return myView;
    }

    private void startLoadData() throws InterruptedException {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString((R.string.msgProgressDialog)));
        progressDialog.show();
        tubeList.clear();
        if (MainActivity.sharedPreferences.getBoolean("setSaveCache", false) && MyFileUtils.getFolder()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                        MainActivity.startTime = System.currentTimeMillis();
                    }
                    List<String> tempChannelIdArray = new ArrayList<>();
                    boolean typeTaskIf = false;
                    for (int i = 0; i < channelIdArray.size(); i++) {                        
                        if(typeTask){
                            try {
                                typeTaskIf = !MyFileUtils.findIdChannelFile(channelIdArray.get(i)) && getCountComment(channelIdArray.get(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            typeTaskIf = !MyFileUtils.findIdChannelFile(channelIdArray.get(i));
                        }
                        if (typeTaskIf) {
                            try {
                                tubeList.add(MyFileUtils.getDataIdChannel(channelIdArray.get(i)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            tempChannelIdArray.add(channelIdArray.get(i)); // добавляем те айди, которых нет в сохраненных файлах
                        }
                    }
                    ChannelsRequest channelsRequest = new ChannelsRequest();
                    try {
                        if (typeTask) {
                            tubeList.addAll(channelsRequest.getArrayObject(tempChannelIdArray, true)); // получаем по запросу данные и добавляем их в массив с данными из файлов
                        } else {
                            tubeList.addAll(channelsRequest.getArrayObject(tempChannelIdArray, false)); // получаем по запросу данные и добавляем их в массив с данными из файлов
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    Collections.sort(tubeList, new Comparator<ChannelYouTube>() {
                        public int compare(ChannelYouTube obj1, ChannelYouTube obj2) {
                            return obj1.items.get(0).snippet.title.compareToIgnoreCase(obj2.items.get(0).snippet.title);
                        }
                    });
                    progressDialog.dismiss();
                    handle.sendMessage(handle.obtainMessage());
                }
            }).start();
        } else if (TestInternet.isOnline(getContext())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                        MainActivity.startTime = System.currentTimeMillis();
                    }
                    ChannelsRequest channelsRequest = new ChannelsRequest();
                    try {
                        if (typeTask) {
                            tubeList.addAll(channelsRequest.getArrayObject(channelIdArray, true));
                        } else {
                            tubeList.addAll(channelsRequest.getArrayObject(channelIdArray, false));
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                    Collections.sort(tubeList, new Comparator<ChannelYouTube>() {
                        public int compare(ChannelYouTube obj1, ChannelYouTube obj2) {
                            return obj1.items.get(0).snippet.title.compareToIgnoreCase(obj2.items.get(0).snippet.title);
                        }
                    });
                    progressDialog.dismiss();
                    handle.sendMessage(handle.obtainMessage());
                }
            }).start();
        } else {
            Toast.makeText(getContext(), getResources().getString((R.string.toastNoInternet)), Toast.LENGTH_LONG).show();
        }

        handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                adapter.notifyDataSetChanged();
                llSpinnerChoice.setVisibility(View.VISIBLE);
                spinner.setSelection(0);
                if (MainActivity.sharedPreferences.getBoolean("setSaveTime", false)) {
                    MainActivity.endTime = System.currentTimeMillis();
                    Toast.makeText(getContext(), "load data " + (MainActivity.endTime - MainActivity.startTime) + "ms", Toast.LENGTH_LONG).show();
                }
                try {
                    saveFile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void saveFile() throws Exception {
        if (MainActivity.sharedPreferences.getBoolean("setSaveCache", false)) {
            if (!MyFileUtils.getFolder()) { // если папки еще нет то создаем, соответсвенно проверку на наличие такого же файла не проводим
                for (int i = 0; i < tubeList.size(); i++) {
                    MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(i)), tubeList.get(i).items.get(0).id, getContext(), getActivity());
                }
            } else {
                for (int i = 0; i < tubeList.size(); i++) {
                    if (MyFileUtils.findIdChannelFile(tubeList.get(i).items.get(0).id)) {// проверяем на наличие файла с таким названием, если нету то пишем файл
                        MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(i)), tubeList.get(i).items.get(0).id, getContext(), getActivity());
                    }
                    if (typeTask) {
                        if (!MyFileUtils.findIdChannelFile(tubeList.get(i).items.get(0).id) && !getCountComment(tubeList.get(i).items.get(0).id)) {
                            MyFileUtils.writeFileIdChannel(JSON.toJSONString(tubeList.get(i)), tubeList.get(i).items.get(0).id, getContext(), getActivity());
                        }
                    }
                }
            }
        }
    }

    private boolean getCountComment(String idChannel) throws Exception {
        return (MyFileUtils.getDataIdChannel(idChannel).countComments > 0);
    }

    // запускаем диалог для ввода айди каналов
    private void showDialogArray() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        DialogFragment newFragment = DialogArray.newInstance(typeTask);
        newFragment.show(fragmentTransaction, "dialog");
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            channelIdArray.clear();
            channelIdArray = new ArrayList(Arrays.asList(intent.getStringExtra("array").split(" ")));
            if (channelIdArray.size() == 1 && channelIdArray.get(0).equals("")) {
                Toast.makeText(getContext(), getResources().getString((R.string.toastChannelNotFound)), Toast.LENGTH_LONG).show();
                // Здесь нужно очистить recyclerView
                tubeList.clear();
                adapter.notifyDataSetChanged();
            } else {
                try {
                    startLoadData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public static ChannelSort newInstance(Boolean typeTask) {
        ChannelSort fragment = new ChannelSort();
        Bundle args = new Bundle();
        args.putBoolean(TYPE_TASK, typeTask);
        fragment.setArguments(args);
        return fragment;
    }
}