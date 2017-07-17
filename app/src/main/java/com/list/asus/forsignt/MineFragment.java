package com.list.asus.forsignt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.list.asus.forsignt.bean.Teacher;
import com.list.asus.forsignt.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.ValueEventListener;

import static com.list.asus.forsignt.R.id.teaId;

/**
 * Created by ASUS on 2017/5/20.
 *
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    private TextView teachId;
    private TextView email;
    private TextView phoneNumber;
    private TextView mine_name;
    private TextView settings;
    private LinearLayout phoneNumberL;
    private LinearLayout emailL;

    private String objectIdFromUser;
    private String objectIdFromTeacher;
    private String mobilePhoneNumber;
    private String emails;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mineView = inflater.inflate(R.layout.main_fra_mine,container,false);

        //初始化控件
        initView(mineView);

//        //得到教师编号id
//        Intent intent=getActivity().getIntent();
//        Bundle bundle=intent.getBundleExtra("data");
//        String teaId=bundle.getString("teaId");
//        teachId.setText(teaId);

        //得到当前用户，并把它显示在teachId这个TextView中
        BmobUser bmobUser = BmobUser.getCurrentUser();
        teachId.setText(bmobUser.getUsername());


        queryTeacherName();

        //在User表中查询手机号和邮箱，如果有就显示出来
        queryPhoneNumber();
        queryEmail();

        //同步User表中的数据到Teacher表中来
        SynchronousData();

//        if (isOk=true){
//            //更新老师的数据，包括邮箱和电话号码
//            upDataTeacher();
//        }else {
//            Toast.makeText(getContext(),"isOK是FALSE，更新失败",Toast.LENGTH_LONG).show();
//        }

        return mineView;


    }

    private void queryTeacherName() {
        BmobQuery<Teacher>queryteacherName=new BmobQuery<>();
        queryteacherName.addWhereEqualTo("teaId",teachId.getText().toString());
//        queryteacherName.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        queryteacherName.setLimit(1);
        queryteacherName.findObjects(new FindListener<Teacher>() {
            @Override
            public void done(List<Teacher> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
                        for (Teacher teacher:list){
                            mine_name.setText(teacher.getTeaName());

                            Log.d("111", "done: "+teacher.getTeaName());
                        }
                    }
                }else {

                }
            }
        });
    }


    //初始化控件
    private void initView(View mineView) {
        teachId=(TextView)mineView.findViewById(teaId);
        mine_name=(TextView)mineView.findViewById(R.id.mine_name);
        phoneNumberL=(LinearLayout)mineView.findViewById(R.id.phoneNumberL);
        emailL=(LinearLayout)mineView.findViewById(R.id.emailL);
        email=(TextView)mineView.findViewById(R.id.email);
        phoneNumber=(TextView)mineView.findViewById(R.id.phoneNumber);
        settings=(TextView)mineView.findViewById(R.id.settings);

        //点击监听
        phoneNumberL.setOnClickListener(this);
        emailL.setOnClickListener(this);
        settings.setOnClickListener(this);
    }


    //下面四个方法是用来监听User表中的数据变化来改变Teacher中的数据的

    //查询User表中该老师的ObjectId
    private void queryObjectIdFromUser() {
        BmobUser bmobUser=BmobUser.getCurrentUser();
        BmobQuery<User> queryObjectId=new BmobQuery<User>();
        queryObjectId.addWhereEqualTo("username",bmobUser.getUsername());
        Log.d("TAG", "11111: "+bmobUser.getUsername());
        queryObjectId.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
                        for (User user:list){
                            objectIdFromUser=user.getObjectId();
                            Log.d("bmob", "objectIdFromUser: "+objectIdFromUser);
                        }

                    }else {
                        Log.i("bmob", "失败：code ="+e.getErrorCode()+",msg = "+e.getLocalizedMessage());
                    }
                }
            }
        });
    }


    //监听User表中的电话号码和email的变化，并将变化传递到Teacher表中
    private void SynchronousData() {

        //查询User表中的objectId,用以找出监听User表中该objectId的行
        queryObjectIdFromUser();

        final BmobRealTimeData rtd=new BmobRealTimeData();
        rtd.start(new ValueEventListener() {
            @Override
            public void onConnectCompleted(Exception e) {

                Log.d("bmob", "连接成功:"+rtd.isConnected());

                    //queryObjectIdFromUser查询到的objectIdFromUser
                    rtd.subRowUpdate("_User",objectIdFromUser);
                    Log.d("bmob", "连接成功3:"+rtd.isConnected()+objectIdFromUser);

                queryObjectIdFromTeacher();

            }

            @Override
            public void onDataChange(JSONObject data) {
               Log.d("bmob", "数据："+data);
                try {
                    mobilePhoneNumber=data.getJSONObject("data").getString("mobilePhoneNumber");
                    emails=data.getJSONObject("data").getString("email");


                    Log.d("bmob", "mobilePhoneNumber: "+mobilePhoneNumber);
                    Log.d("bmob", "emails: "+emails);

                    //从Teacher表中查询到objectIdFromTeacher，在该方法中有一个upDataTeacher()方法，
                    // 如果查到了就运行该方法同步该老师的mobilePhoneNumber和emails
                    queryObjectIdFromTeacher();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    //查询Teacher表中该老师的ObjectId
    private void queryObjectIdFromTeacher() {
        BmobUser bmobUser=BmobUser.getCurrentUser();
        BmobQuery<Teacher> queryObjectId=new BmobQuery<Teacher>();
        queryObjectId.addWhereEqualTo("teaId",bmobUser.getUsername());
        Log.d("bmob", "11111: "+bmobUser.getUsername());
        queryObjectId.findObjects(new FindListener<Teacher>() {
            @Override
            public void done(List<Teacher> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
                        for (Teacher teacher:list){
                            objectIdFromTeacher=teacher.getObjectId();
                            Log.d("bmob", "22222: "+objectIdFromTeacher);
                            //upDataTeacher()方法，如果User表中的电话或者邮箱改变了，就改变Teacher表中的电话或者邮箱
                            upDataTeacher();
                        }

                    }else {
                        Log.i("bmob", "失败：code ="+e.getErrorCode()+",msg = "+e.getLocalizedMessage());
                    }
                }
            }
        });
    }


    //如果User表中的电话或者邮箱改变了，就改变Teacher表中的电话或者邮箱
    private void upDataTeacher() {
        Teacher teacher=new Teacher();
        teacher.setMobilePhoneNumber(mobilePhoneNumber);
        teacher.setEmail(emails);
        Log.i("bmob","objectIdFromTeacher:"+objectIdFromTeacher);
        teacher.update(objectIdFromTeacher, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.i("bmob","已更新Teacher中的表");
                    Log.i("bmob","23333");
                }else{
                    Log.i("bmob","更新Teacher中的表失败"+e.getMessage()+","+e.getErrorCode());
                    Log.i("bmob","55555");
                }
            }
        });
    }




    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.phoneNumberL:
                messageIdentification();
                break;
            case R.id.emailL:
                lockEmail();
//                Intent intent=getActivity().getIntent();
//                Bundle bundle=intent.getBundleExtra("data");
//                String e=bundle.getString("mine_email");
//                mine_email.setText(e);

//                Intent intent=new Intent(getActivity(),MineEmail.class);

//                MineEmail emailL= (MineEmail) getActivity();
//                String a= emailL.getEmail();
//                mine_email.setText(a);
                break;
            case R.id.settings:
                setttings();
                break;
        }
    }


    //查询电话号码
    private void queryPhoneNumber() {
        final BmobQuery<User> queryPhoneNumber=new BmobQuery<>();
        queryPhoneNumber.addWhereEqualTo("username",teachId.getText().toString());
//        queryPhoneNumber.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        queryPhoneNumber.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
                    for (User user:list){
                        if (!user.getMobilePhoneNumber().isEmpty()){
                            phoneNumber.setText(user.getMobilePhoneNumber());
                        }else {
                            phoneNumber.setText("请绑定手机号");
                        }
                    }
                }else {
                    phoneNumber.setText("请绑定手机号");
                }
            }
        });
    }


    //查询邮箱
    private void queryEmail() {
        final BmobQuery<User> queryEmail=new BmobQuery<>();
        queryEmail.addWhereEqualTo("username",teachId.getText().toString());
//        queryEmail.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        queryEmail.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e==null){
                    for (User user:list){
                       if (!user.getEmail().isEmpty()&&user.getEmailVerified()==true){
                           email.setText(user.getEmail());
                       }else {
                           email.setText("请绑定邮箱");
                       }
                    }

                }else {
                    email.setText("请绑定邮箱");
//                    Toast.makeText(getContext(),"fail",Toast.LENGTH_LONG).show();
                }
            }
        });


    }



    //点击事件中的三个intent跳转
    private void messageIdentification() {
        Intent intent=new Intent(getActivity(),MessageIdentification.class);
        startActivity(intent);
    }

    private void lockEmail() {
        Intent intent=new Intent(getActivity(),MineEmail.class);
        startActivity(intent);
    }

    private void setttings() {
        Intent intent=new Intent(getActivity(),MineSettings.class);
        startActivity(intent);

    }


//    private void showDialog() {
//
////        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());
//
//        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//        builder.setTitle("请在下面输入你要绑定号码");
//
//         EditText phoneNumber=new EditText(getContext());
//        builder.setView(phoneNumber);
//        final String a=phoneNumber.getText().toString();
//        Log.d("zheli",a);
//        builder.setNegativeButton("取消", null);
//        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                Toast.makeText(getContext(),a,Toast.LENGTH_LONG).show();
//            }
//        });
//        builder.show();
//    }

}
