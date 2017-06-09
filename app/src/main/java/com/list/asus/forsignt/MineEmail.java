package com.list.asus.forsignt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wanjian on 2017/5/30.
 */


/*
 *邮箱绑定版块
 * 点击发送button就会向指定邮箱发送一封验证邮件
 */
public class MineEmail extends AppCompatActivity implements View.OnClickListener {

    private EditText email_EditText;
    private Button email_Button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_email);

        //初始化控件
        initView();


    }

    //初始化控件
    private void initView() {

        email_EditText=(EditText)findViewById(R.id.email_EditText);
        email_Button=(Button)findViewById(R.id.email_Button);

        email_EditText.setOnClickListener(this);
        email_Button.setOnClickListener(this);

    }


    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.email_Button:
//                  sendEmail();

//                Intent intent=new Intent(MineEmail.this,MineFragment.class);
//                startActivity(intent);

//                final String mine_email=email_EditText.getText().toString();
//                Intent intent=new Intent(MineEmail.this,MainActivity.class);
//                //使用bundle传递数据
//                Bundle bundle=new Bundle();
//                bundle.putString("mine_email",mine_email);
//                intent.putExtra("data",bundle);
//                startActivity(intent);


//                onBackPressed();

                sendEmail();
                break;


        }
    }



    //发送绑定邮件到指定邮箱，如果点击链接就绑定该邮箱
    private void sendEmail() {

//        final String mine_email=email_EditText.getText().toString();
//
//        BmobUser.requestEmailVerify("1763138044@qq.com", new UpdateListener() {
//            @Override
//            public void done(BmobException e) {
//                if (e==null){
//                    Toast.makeText(getBaseContext(),"已发送邮件到"+mine_email,Toast.LENGTH_LONG).show();
//                }else {
//                    Toast.makeText(getBaseContext(),"发送失败",Toast.LENGTH_LONG).show();
//                }
//            }
//        });
        final String email=email_EditText.getText().toString();
        BmobUser bu=BmobUser.getCurrentUser();
        bu.setEmail(email);
        bu.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                      if (!email.isEmpty()){
                          Toast.makeText(getBaseContext(),"已发送邮件到"+email,Toast.LENGTH_LONG).show();
                      }else {
                          Toast.makeText(getBaseContext(),"您输入的邮箱为空"+email,Toast.LENGTH_LONG).show();
                      }
//                      onBackPressed();

//                    Intent intent=new Intent(MineEmail.this,MineFragment.class);
//                    //使用bundle传递数据
//                    Bundle bundle=new Bundle();
//                    bundle.putString("mine_email",mine_email);
//                    intent.putExtra("data",bundle);
//                    startActivity(intent);


                }else {
                    Toast.makeText(getBaseContext(),"发送邮件失败" ,Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
