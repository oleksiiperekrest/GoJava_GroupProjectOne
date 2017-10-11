package com.gmail.fomichov.m.youtubeanalytics.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gmail.fomichov.m.youtubeanalytics.R;
import com.gmail.fomichov.m.youtubeanalytics.utils.CheckForChannel;
import com.gmail.fomichov.m.youtubeanalytics.utils.TestInternet;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class DialogArray extends DialogFragment {
    private EditText etArrayIdChannel;
    private Intent intent;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_array, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        etArrayIdChannel = (EditText) view.findViewById(R.id.etArrayIdChannel);
        builder.setTitle("Массив idChannels YouTube");
        builder.setView(view);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getArguments().getBoolean("comments")) {
                    intent = new Intent("pressButtonOkInDialogMedia");
                } else {
                    intent = new Intent("pressButtonOkInDialogGlobal");
                }

                try {
                    intent.putExtra("array", checkForChannels(etArrayIdChannel.getText().toString()));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }

    private String checkForChannels(String channelsString) throws ExecutionException, InterruptedException {
        final String channels = channelsString;
        final FutureTask<String> checkId = new FutureTask<>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                StringBuilder stringBuilder = new StringBuilder();
                String[] channelsArray = channels.split(" ");
                String checkingChannel;
                for (String channel : channelsArray) {
                    checkingChannel = CheckForChannel.getChannelId(channel);
                    if (!checkingChannel.equals(CheckForChannel.BADCHANNEL)) {
                        stringBuilder.append(checkingChannel + " ");
                    }
                }
                return stringBuilder.toString();
            }
        });

        new Thread(checkId).start();
        String checkedId = checkId.get();
            return checkedId;

    }

    public static DialogArray newInstance(Boolean comments) {
        DialogArray dialogArray = new DialogArray();
        dialogArray.setCancelable(false);
        Bundle args = new Bundle();
        args.putBoolean("comments", comments);
        dialogArray.setArguments(args);
        return dialogArray;
    }
}

