package com.example.a17990.notificationtest;

import android.app.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by 17990 on 2017/10/31.
 */

public class ApplicationUtil extends Application{
    private Socket socket;

    private static final String HOST = "free.ngrok.cc";
    private static final int PORT = 14123;

    private OutputStream out=null;
    private BufferedReader in = null;

    public void init() throws IOException {
        this.socket=new Socket(HOST,PORT);
        this.out = socket.getOutputStream();
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public OutputStream getOut() {
        return out;
    }

    public void setOut(OutputStream out) {
        this.out = out;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }
}
