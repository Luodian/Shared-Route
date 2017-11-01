package com.example.a17990.notificationtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    public static MyThread thread;
    public Socket socket;
    private BufferedReader in;
    private PrintStream out;
    Button btn;
    TextView mainText;
    TextView secondText;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x11:
                    noti(msg.getData().getString("msg"));
//                    mainText.setText(msg.getData().getString("msg"));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainText = (TextView)findViewById(R.id.main_text);
        secondText = (TextView)findViewById(R.id.second_text);

        //点击启动线程
        btn =(Button)findViewById(R.id.btn_1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                new MyThread(MainActivity.this).start();
                thread=new MyThread(MainActivity.this);
            thread.start();}
        });

        //点击进入另一个界面
        ((Button)findViewById(R.id.gotoanotheractivity)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ScrollingActivity.class));
                finish();
            }
        });
    }

    class MyThread extends  Thread{
        Context  context;
        public MyThread(Context context){
            this.context=context;
        }
        public void run(){

            try {
                //获取socket
                ApplicationUtil appUtil = (ApplicationUtil) MainActivity.this.getApplication();

                //init()只做一次
                appUtil.init();

                socket = appUtil.getSocket();
                in = appUtil.getIn();
                out = appUtil.getOut();


                //向服务器发送UI中按钮上的请求
//                out.println((((Button) findViewById(R.id.android_text)).getText().toString()));
//                out.flush();

                //从服务器获取通知,由handler发送给主线程,之后保持这个线程贯穿程序始终
                String line = null;
                while ((line = in.readLine()) != null) {
                    Log.e("line",line);
                    Message msg = new Message();
                    msg.what = 0x11;
                    Bundle bundle = new Bundle();
                    bundle.putString("msg", line);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void noti(String str){

        Notification notification = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.banner_1))
                .setSmallIcon(R.drawable.banner_2)
                .setTicker("You have a message")
                .setContentTitle("title")
                .setContentText("text:"+str)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_CANCEL_CURRENT))
                .build();
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);
    }

}
