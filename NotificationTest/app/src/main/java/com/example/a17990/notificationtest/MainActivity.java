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
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Socket socket;
    private BufferedReader in;
    private OutputStream out;
    Button btn;
    TextView mainText;
    TextView secondText;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x11:
                    mainText.setText(msg.getData().getString("msg"));
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
                new MyThread(MainActivity.this).start();
            }
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

            //测试子线程获取UI内的信息并发送给handler
//            Message message = new Message();
//            message.what=0x11;
//            Bundle bundle = new Bundle();
//            bundle.putString("msg",secondText.getText().toString());
//            message.setData(bundle);
//            handler.sendMessage(message);



            try {
                //获取socket
                ApplicationUtil appUtil = (ApplicationUtil) MainActivity.this.getApplication();

                //init()只做一次
                appUtil.init();

                socket = appUtil.getSocket();
                in = appUtil.getIn();
                out = appUtil.getOut();

                //从服务器获取通知,由handler发送给主线程
                String line = null;
                StringBuilder buffer = new StringBuilder();
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }
                Message msg = new Message();
                msg.what = 0x11;
                Bundle bundle = new Bundle();
                bundle.putString("msg", buffer.toString());
                msg.setData(bundle);
                handler.sendMessage(msg);

                //向服务器发送通知
//                out.write("这里是安卓客户端".getBytes("gbk"));

                //向服务器发送UI中按钮上的请求
                out.write(((Button) findViewById(R.id.android_text)).getText().toString().getBytes("gbk"));
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
//
//    public void myThread(){
//        Thread thread = new Thread(new Runnable() {
//            public boolean isRunning = true;
//            @Override
//            public void run() {
//                while(isRunning){
//                    try {
//                        Thread.sleep(1000);
//                        int r=in.available();
//                        while (r==0){
//                            r=in.available();
//                        }
//                        byte[] b=new byte[r];
//                        in.read(b);
//                        String content = new String(b,"utf-8");
//                        //每当读到来自服务器的数据后，发送消息通知程序页面显示数据
//                        Message msg = new Message();
//                        msg.what = 0x123;
//                        msg.obj = content;
//                        Log.v("ho", content);
//                        handler.sendMessage(msg);
//
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });
//        thread.start();
//    }
//

    public void noti(){

        Notification notification = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),R.drawable.banner_1))
                .setSmallIcon(R.drawable.banner_2)
                .setTicker("You have a message")
                .setContentTitle("title")
                .setContentText("text:"+mainText.getText().toString())
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
