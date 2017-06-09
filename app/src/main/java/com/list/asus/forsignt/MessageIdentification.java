package com.list.asus.forsignt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.list.asus.forsignt.bean.User;

import java.util.List;

import cn.bmob.sms.BmobSMS;
import cn.bmob.sms.listener.RequestSMSCodeListener;
import cn.bmob.sms.listener.VerifySMSCodeListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by wanjian on 2017/5/29.
 */


/*
 *此处为绑定联系电话界面
 * 逻辑为：输入绑定的号码，点击获取验证码就发送验证码到该手机号，填上验证码，检查验证码的正确性，如果正确，就向Teacher表添加号码
 */
public class MessageIdentification extends AppCompatActivity implements View.OnClickListener {

    //phoneNumber_E电话号码输入的TextView，message_identification_E验证码输入的TextView，
    // phoneNumber_B获取验证码的button，message_identification_B发送验证码的button
    private EditText phoneNumber_E;
    private EditText message_identification_E;
    private Button phoneNumber_B;
    private Button message_identification_B;
    private String objectId;                            //该教师在Teacher表中的objectId
    BmobUser bmobUser;                  //用户
    private String phoneNumber;                        //从phoneNumber_E得到的电话号码
    private String verificationCode;                  //从message_identification_E得到的验证码
    boolean isOk=false;                               //是否可以向Teacher表中添加该教师的号码



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_identification);

        //初始化bmob包
        BmobSMS.initialize(this,"a16ec96b707da5d0f2a404b0ce9d755b");

        bmobUser = BmobUser.getCurrentUser();
        Toast.makeText(getBaseContext(),bmobUser.getUsername()+"",Toast.LENGTH_LONG).show();
        Log.d("TAG", "onCreate: "+bmobUser.getUsername());

        //初始化控件
        initView();

    }


    //初始化控件
    private void initView() {
        phoneNumber_E=(EditText)findViewById(R.id.phoneNumber_E);
        message_identification_E=(EditText) findViewById(R.id.message_identification_E);
        phoneNumber_B=(Button)findViewById(R.id.phoneNumber_B);
        message_identification_B=(Button)findViewById(R.id.message_identification_B);

        phoneNumber_B.setOnClickListener(this);
        message_identification_B.setOnClickListener(this);
    }


    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.phoneNumber_B:
                getMessageIdentification();
                break;
            case R.id.message_identification_B:
                VerificationCode();
                //判断验证码是否对，对才去查教师的teaId并添加号码
                if (isOk=true){
                    queryObjectIdFromUser();
                }else {
                    Toast.makeText(getBaseContext(),"验证码不正确哦~请重新输入",Toast.LENGTH_LONG).show();
                }

                break;
        }

    }


    //向输入的手机号发送验证码
    private void getMessageIdentification() {
        //讲按钮设置为不可用状态
//        phoneNumber_B.setEnabled(false);
        final String number= phoneNumber_E.getText().toString();
        if (!number.isEmpty()){
            BmobSMS.requestSMSCode(this,number, "绑定你的手机号", new RequestSMSCodeListener() {
                @Override
                public void done(Integer integer, cn.bmob.sms.exception.BmobException e) {
                    if (e==null){
                        Log.i("TAG1","短信发送成功，短信id："+integer);//用于查询本次短信发送详情
                        Toast.makeText(getBaseContext(),"已发送验证短信到"+number+",请尽快填好验证码",Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getBaseContext(),"获取验证码失败",Toast.LENGTH_LONG).show();
                        Log.i("TAG2","errorCode = "+e.getErrorCode()+",errorMsg = "+e.getLocalizedMessage());
                    }
                }
            });
        }else {
            Toast.makeText(getBaseContext(),"输入的号码不能为空",Toast.LENGTH_LONG).show();
        }
    }


    //验证输入的验证码是否正确
    private void VerificationCode() {
        phoneNumber=phoneNumber_E.getText().toString();
        verificationCode=message_identification_E.getText().toString();
         if (!verificationCode.isEmpty()){
             Toast.makeText(getBaseContext(),phoneNumber+" "+verificationCode,Toast.LENGTH_LONG).show();
             Log.d("TAG0",phoneNumber+" "+verificationCode);
             BmobSMS.verifySmsCode(this, phoneNumber, verificationCode, new VerifySMSCodeListener() {
                 @Override
                 public void done(cn.bmob.sms.exception.BmobException e) {
                     if (e==null){
                         isOk=true;
                         Log.i("TAG3", "验证通过");

                     }else{
                         Log.i("TAG4", "验证失败：code ="+e.getErrorCode()+",msg = "+e.getLocalizedMessage());
                         Toast.makeText(getBaseContext(),"验证失败",Toast.LENGTH_LONG).show();
                     }
                 }
             });
         }else {
             Toast.makeText(getBaseContext(),"输入的验证码不能为空",Toast.LENGTH_LONG).show();
         }
    }


    //从Teacher表中查询老师的teaId，查到了就添加其绑定的号码
   private void queryObjectIdFromUser() {
        BmobQuery<User> queryObjectId=new BmobQuery<User>();
        queryObjectId.addWhereEqualTo("username",bmobUser.getUsername());
        Log.d("TAG", "11111: "+bmobUser.getUsername());
        queryObjectId.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
                        for (User user:list){
                            objectId=user.getObjectId();
                            Log.d("TAG5", "22222: "+objectId);
                        }
                        addPhoneNumberToUser();
                    }else {
                        Log.i("TAG6", "验证失败：code ="+e.getErrorCode()+",msg = "+e.getLocalizedMessage());
                    }
                }
            }
        });
    }


    //在查到了老师的teaId的情况下添加改老师的号码
    private void addPhoneNumberToUser() {
        final BmobUser user=BmobUser.getCurrentUser();
        phoneNumber=phoneNumber_E.getText().toString();
        user.setMobilePhoneNumber(phoneNumber);
        Log.d("TAG", "333333: "+phoneNumber);
        user.update(objectId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    Toast.makeText(getBaseContext(),"已绑定手机号"+user.getMobilePhoneNumber(),Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getBaseContext(),"fail",Toast.LENGTH_LONG).show();
                    Log.i("TAG7","更新失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


}
