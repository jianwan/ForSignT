package com.list.asus.forsignt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.list.asus.forsignt.bean.Teacher;
import com.list.asus.forsignt.bean.User;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by ASUS on 2017/5/19.
 *
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    public EditText login_id;
    public EditText login_password;
    public Button login_button;
    public Button login_toRegister;
    String login_id_content;
    String login_password_content;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //初始化 SDK信息
        Bmob.initialize(getApplicationContext(),"a16ec96b707da5d0f2a404b0ce9d755b");

        initView();



        login_toRegister.setOnClickListener(this);
        login_button.setOnClickListener(this);


    }

    //实例化控件
    private void initView() {
        login_id=(EditText) findViewById(R.id.login_id);
        login_password=(EditText) findViewById(R.id.login_password);
        login_button=(Button)findViewById(R.id.login_button);
        login_toRegister=(Button)findViewById(R.id.login_toRegister);

        login_id_content=login_id.getText().toString();
        login_password_content=login_password.getText().toString();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login_toRegister:
                if (TextUtils.isEmpty(login_id.getText().toString())&&TextUtils.isEmpty(login_password.getText().toString())){
                    Toast.makeText(getBaseContext(),"账号和密码不能为空",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(login_id.getText().toString())){
                    Toast.makeText(getBaseContext(),"账号不能为空",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(login_password.getText().toString())){
                    Toast.makeText(getBaseContext(),"密码不能为空",Toast.LENGTH_LONG).show();
                }else {
                    register();
                }

                break;
            case R.id.login_button:
                if (TextUtils.isEmpty(login_id.getText().toString())&&TextUtils.isEmpty(login_password.getText().toString())){
                    Toast.makeText(getBaseContext(),"账号和密码不能为空",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(login_id.getText().toString())){
                    Toast.makeText(getBaseContext(),"账号不能为空",Toast.LENGTH_LONG).show();
                }else if (TextUtils.isEmpty(login_password.getText().toString())){
                    Toast.makeText(getBaseContext(),"密码不能为空",Toast.LENGTH_LONG).show();
                }else {
                    queryUser();
                }
                break;
            default:
                break;
        }
    }


    //注册功能
    private void register() {

        String username=login_id.getText().toString();
        String userpassword=login_password.getText().toString();

        BmobUser buser=new BmobUser();
        buser.setUsername(username);
        buser.setPassword(userpassword);
        buser.signUp( new SaveListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    Toast.makeText(LoginActivity.this,"注册成功，现在您可以登陆了",Toast.LENGTH_SHORT).show();
                    addResultToTeacher();
                }else {
                    Toast.makeText(LoginActivity.this,"该账号已注册",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    //查询用户名，查到了才让它登陆
    private void queryUser() {

        final String username=login_id.getText().toString();

        BmobQuery<User> queryUser=new BmobQuery<>();
        queryUser.addWhereEqualTo("username",username);
        Log.d("cichu ",username);
        queryUser.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
//                   for (User user:list){
//                       Toast.makeText(getBaseContext(),user.getUsername(),Toast.LENGTH_LONG).show();
//                       //查到了用户名才让它登陆
//                       login();
////                       Toast.makeText(getBaseContext(),"OK",Toast.LENGTH_LONG).show();
//                   }
                    Toast.makeText(getBaseContext(),"ok",Toast.LENGTH_LONG).show();
                    //查到了用户名才让它登陆
                    login();

                }else {
                    //else有点问题，弹不出来
                    Toast.makeText(getBaseContext(),"该用户名未注册，请注册后再登陆",Toast.LENGTH_LONG).show();

                }
            }
        });



    }

    //登陆
    private void login() {

//        //缓存账户
//        BmobUser bmobUser=BmobUser.getCurrentUser();
//        if (bmobUser!=null){
//            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);
//        }else {
//            Toast.makeText(getBaseContext(),"缓存失败",Toast.LENGTH_LONG).show();
//        }

        String username=login_id.getText().toString();
        String userpassword=login_password.getText().toString();

        BmobUser buser=new BmobUser();
        buser.setUsername(username);
        buser.setPassword(userpassword);

        BmobUser.loginByAccount(username, userpassword, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e==null){
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);

//                    //使用bundle传递数据
//                    Bundle bundle=new Bundle();
//                    String teaId=login_id.getText().toString();
//                    bundle.putString("teaId",teaId);
//                    intent.putExtra("data",bundle);

                    startActivity(intent);
                }else {
                    Toast.makeText(LoginActivity.this,"账号或密码不对，请检查后再登陆",Toast.LENGTH_SHORT).show();
                }
            }
        });





    }

    //如果注册成功就给teacher表添加一行数据
    private void addResultToTeacher() {
        String username=login_id.getText().toString();

        Teacher teacher=new Teacher();
        teacher.setTeaId(username);
        teacher.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    Toast.makeText(getBaseContext(),s,Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getBaseContext(),"fail",Toast.LENGTH_LONG).show();
                }
            }
        });
    }


}
