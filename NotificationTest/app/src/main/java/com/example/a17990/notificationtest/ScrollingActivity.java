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
import java.io.OutputStream;
import java.net.Socket;

public class ScrollingActivity extends AppCompatActivity {

    private Socket socket;
    private BufferedReader in;
    private OutputStream out;
    Button btn;
    TextView scrollText;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0x11:
                    scrollText.setText(msg.getData().getString("msg"));
                    break;
                default:
                    break;
            }
        }
    };

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

        new MyThread(ScrollingActivity.this).start();

}
    class MyThread extends  Thread{
        Context context;
        public MyThread(Context context){
            this.context=context;
        }
        public void run(){

            //测试子线程获取UI内的信息并发送给handler
            Message message = new Message();
            message.what=0x11;
            Bundle bundle1 = new Bundle();
            bundle1.putString("msg",scrollText.getText().toString()+"改变后");
            message.setData(bundle1);
            handler.sendMessage(message);



            try {
                //获取socket
                ApplicationUtil appUtil = (ApplicationUtil) ScrollingActivity.this.getApplication();

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
    }}

