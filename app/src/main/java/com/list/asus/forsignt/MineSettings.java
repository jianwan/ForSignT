package com.list.asus.forsignt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import cn.bmob.v3.BmobUser;

/**
 * Created by wanjian on 2017/6/2.
 */

public class MineSettings extends AppCompatActivity implements View.OnClickListener {

    private Button exit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_settings);

        initView();

    }

    private void initView() {
        exit=(Button)findViewById(R.id.exit);

        exit.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        BmobUser.logOut();
//        BmobUser currentUser=BmobUser.getCurrentUser();
        Intent intent=new Intent(MineSettings.this,LoginActivity.class);
        startActivity(intent);
//        MainActivity.MainActivity.finish();
//        System.exit(0);
    }
}
