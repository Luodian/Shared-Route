package com.example.a17990.notificationtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class ScrollingActivity extends AppCompatActivity {

    private Socket socket;
    private BufferedReader in;
    private PrintStream out;
    Button btn;
    TextView scrollText;

//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg){
//            switch (msg.what){
//                case 0x11:
////                    noti(msg.getData().getString("msg"));
//                    scrollText.setText(msg.getData().getString("msg"));
//                    break;
//                default:
//                    break;
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        scrollText = (TextView)findViewById(R.id.scroll_text);
        btn =(Button)findViewById(R.id.btn_2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScrollingActivity.this,MainActivity.class));
                finish();
            }
        });


}
//    public void noti(String str){
//
//        Notification notification = new NotificationCompat.Builder(this)
//                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.banner_1))
//                .setSmallIcon(R.drawable.banner_2)
//                .setTicker("You have a message")
//                .setContentTitle("title")
//                .setContentText("text:"+str)
//                .setWhen(System.currentTimeMillis())
//                .setPriority(Notification.PRIORITY_DEFAULT)
//                .setAutoCancel(true)
//                .setOngoing(false)
//                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
//                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
//                .build();
//        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
//        notificationManager.notify(1,notification);
//    }
}

