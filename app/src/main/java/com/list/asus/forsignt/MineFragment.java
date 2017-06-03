package com.list.asus.forsignt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.list.asus.forsignt.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by ASUS on 2017/5/20.
 *
 */

public class MineFragment extends Fragment implements View.OnClickListener {

    private TextView teachId;
    private TextView email;
    private TextView settings;
    private LinearLayout phoneNumberL;
    private LinearLayout emailL;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View mineView = inflater.inflate(R.layout.main_fra_mine,container,false);

        teachId=(TextView)mineView.findViewById(R.id.teaId);
        phoneNumberL=(LinearLayout)mineView.findViewById(R.id.phoneNumberL);
        emailL=(LinearLayout)mineView.findViewById(R.id.emailL);
        email=(TextView)mineView.findViewById(R.id.email);
        settings=(TextView)mineView.findViewById(R.id.settings);

//        //得到教师编号id
//        Intent intent=getActivity().getIntent();
//        Bundle bundle=intent.getBundleExtra("data");
//        String teaId=bundle.getString("teaId");
//        teachId.setText(teaId);


        BmobUser bmobUser = BmobUser.getCurrentUser();
        teachId.setText(bmobUser.getUsername());

        queryEmail();



        phoneNumberL.setOnClickListener(this);
        emailL.setOnClickListener(this);
        settings.setOnClickListener(this);




        return mineView;


    }

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
                           email.setText("点击绑定邮箱");
                       }
                    }

                }else {
                    email.setText("点击绑定邮箱");
//                    Toast.makeText(getContext(),"fail",Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.phoneNumberL:
//                showDialog();
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

    private void setttings() {
        Intent intent=new Intent(getActivity(),MineSettings.class);
        startActivity(intent);
    }

    private void lockEmail() {
        Intent intent=new Intent(getActivity(),MineEmail.class);
        startActivity(intent);
    }

    private void messageIdentification() {
        Intent intent=new Intent(getActivity(),MessageIdentification.class);
        startActivity(intent);
    }

    private void showDialog() {

//        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getContext());

        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("请在下面输入你要绑定号码");

         EditText phoneNumber=new EditText(getContext());
        builder.setView(phoneNumber);
        final String a=phoneNumber.getText().toString();
        Log.d("zheli",a);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getContext(),a,Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

}
