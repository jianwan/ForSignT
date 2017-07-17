package com.list.asus.forsignt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.list.asus.forsignt.bean.Schedule;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by ASUS on 2017/5/20.
 *
 */

public class CurriculumFragment extends Fragment {

    private List<Schedule>mschedules=new ArrayList<>();
//    CurriculumAdapter curriculumAdapter;

    private TextView test;
    String teachId;
    String classTime;
    String classRoom;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View curriculumView = inflater.inflate(R.layout.main_fra_curriculum,container,false);


//        //得到教师编号id
//        Intent intent=getActivity().getIntent();
//        Bundle bundle=intent.getBundleExtra("data");
//        teachId=bundle.getString("teaId");

        BmobUser bmobUser = BmobUser.getCurrentUser();
        teachId=bmobUser.getUsername();


//        test=(TextView)curriculumView.findViewById(R.id.item_curriculum);

        getCourses();


         recyclerView=(RecyclerView)curriculumView.findViewById(R.id.recycler_curriculum);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return curriculumView;
    }

    private void getCourses() {
        BmobQuery<Schedule> querySchedule=new BmobQuery<>();
        querySchedule.addWhereEqualTo("teaId",teachId);
        querySchedule.order("classOrder");
//        querySchedule.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
        querySchedule.setLimit(20);
        querySchedule.findObjects(new FindListener<Schedule>() {
            @Override
            public void done(List<Schedule> list, BmobException e) {
                if (e==null){

                    CurriculumAdapter curriculumAdapter=new CurriculumAdapter(list);
                    recyclerView.setAdapter(curriculumAdapter);

//                    for (Schedule schedule:list){
//                        classTime= schedule.getClassTime();
//                        classRoom=schedule.getClassroom();
//
////                        mschedules.add((Schedule) list);
//                        Toast.makeText(getContext(),"ok",Toast.LENGTH_SHORT).show();
//                        test.setText(classTime+" "+classRoom+"教室");
//
//
//                    }




//                    CurriculumAdapter curriculumAdapter=new CurriculumAdapter(list);
//                    recyclerView.setAdapter(curriculumAdapter);


                }else {
                    Toast.makeText(getContext(),"您当前没有课哟~",Toast.LENGTH_SHORT).show();
                }

            }
        });





//        querySchedule.findObjects(this, new FindListener<Schedule>() {
//
//
//            @Override
//            public void done(List<Schedule> list, BmobException e) {
//
//            }
//        });
    }


}
