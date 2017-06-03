package com.list.asus.forsignt;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by wanjian on 2017/5/29.
 */

public class MessageIdentification extends AppCompatActivity implements View.OnClickListener {

    private EditText phoneNumber_E;
    private EditText message_identification_E;
    private Button phoneNumber_B;
    private Button message_identification_B;
    private Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_identification);

        initView();


    }

    private void initView() {
        phoneNumber_E=(EditText)findViewById(R.id.phoneNumber_E);
        message_identification_E=(EditText) findViewById(R.id.message_identification_E);
        phoneNumber_B=(Button)findViewById(R.id.phoneNumber_B);
        message_identification_B=(Button)findViewById(R.id.message_identification_B);

        phoneNumber_B.setOnClickListener(this);
        message_identification_B.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.phoneNumber_B:
                getMessageIdentification();
                break;
        }

    }

    private void getMessageIdentification() {
        //讲按钮设置为不可用状态
        phoneNumber_B.setEnabled(false);
        String a= phoneNumber_E.getText().toString();
        Toast.makeText(getBaseContext(),a,Toast.LENGTH_LONG).show();
        BmobSMS.requestSMSCode(mContext,a, "绑定你的手机号", new RequestSMSCodeListener() {
            @Override
            public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                if (e==null){
                    Toast.makeText(getBaseContext(),"请输入验证码",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getBaseContext(),"获取验证码失败",Toast.LENGTH_LONG).show();

                }
            }
        });
    }
}
