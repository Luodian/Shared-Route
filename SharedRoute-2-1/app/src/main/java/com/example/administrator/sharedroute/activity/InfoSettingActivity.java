package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.administrator.sharedroute.R;
import com.unstoppable.submitbuttonview.SubmitButton;

public class InfoSettingActivity extends AppCompatActivity {

    private int requestCodeFromHistory = 123;
    private int resultCodeToHistory = 124;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_setting);
        final Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        final int index=Integer.valueOf(bundle.getString("index"));
        final EditText name = (EditText)findViewById(R.id.name);
        final EditText phone = (EditText)findViewById(R.id.phone);
        final EditText place = (EditText)findViewById(R.id.commonplace);
        name.setText(bundle.getString("nameInfo"));
        phone.setText(bundle.getString("phoneInfo"));
        place.setText(bundle.getString("delieverplaceInfo"));

        final SubmitButton submitBtn = (SubmitButton)findViewById(R.id.info_ensure);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitBtn.doResult(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Intent intent1=new Intent();
                intent1.putExtra("index",String.valueOf(index));
                intent1.putExtra("name",name.getText().toString());
                intent1.putExtra("phone",phone.getText().toString());
                intent1.putExtra("place",place.getText().toString());
                setResult(resultCodeToHistory,intent1);
                finish();
            }
        });
    }
}
