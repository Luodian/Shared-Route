package com.example.administrator.sharedroute.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.sharedroute.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText  mName;
    private EditText  mPhone;
    private EditText  mStuNum;
    private EditText  mInviteCode;
    private EditText  mPassWord;
    private EditText  mVerifyPassword;
    private Button mRegisterBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mName = (EditText)findViewById(R.id.regi_name);
        mPhone = (EditText)findViewById(R.id.regi_phone);
        mStuNum = (EditText)findViewById(R.id.regi_stu_num);
        mInviteCode = (EditText)findViewById(R.id.regi_invite_code);
        mPassWord = (EditText)findViewById(R.id.regi_password);
        mVerifyPassword = (EditText)findViewById(R.id.verify_password);

        mRegisterBtn = (Button)findViewById(R.id.regi_ensure_btn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = mPassWord.getText().toString();
                String verify = mVerifyPassword.getText().toString();
                if (!(password.equals(verify))){
                    Toast.makeText(RegisterActivity.this,"buyizhia",Toast.LENGTH_SHORT).show();
                    mVerifyPassword.setError("密码不一致");
                    mVerifyPassword.requestFocus();
                }else {
                    Intent data = new Intent();
                    data.putExtra("name", mName.getText().toString());
                    data.putExtra("phone", mPhone.getText().toString());
                    data.putExtra("stuNum", mStuNum.getText().toString());
                    data.putExtra("inviteCode", mInviteCode.getText().toString());
                    data.putExtra("password", mPassWord.getText().toString());
                    setResult(RESULT_OK, data);
                    finish();
                }
            }
        });

    }
}
