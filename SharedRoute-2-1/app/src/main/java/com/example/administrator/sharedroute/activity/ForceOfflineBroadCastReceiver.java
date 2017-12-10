package com.example.administrator.sharedroute.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
//import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import com.example.administrator.sharedroute.R;

import java.io.IOException;
import java.net.Socket;

public class ForceOfflineBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                .setTitle("强制下线")
                .setMessage("您的账号已经在另一台设备登录，若非本人所为，您的账户可能已不安全，请及时更换密码！")
                .setCancelable(false)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = new Thread() {
                            public void run() {
                                try {
                                    LoginActivity.in.close();
                                    LoginActivity.out.close();
                                    LoginActivity.socket.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.putExtra("from","forceOffline");
                        //在广播中启动活动，需要添加如下代码
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        for (Activity a : MainActivity.activityList) {
                            if (a != null) {
                                a.finish();
                            }
                        }
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();
    }
}
