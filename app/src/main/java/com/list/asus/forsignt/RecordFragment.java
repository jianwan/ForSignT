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

import com.list.asus.forsignt.bean.CheckRecord;
import com.list.asus.forsignt.bean.CheckResult;
import com.list.asus.forsignt.bean.Class_stuId;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by ASUS on 2017/5/20.
 *
 */

public class RecordFragment extends Fragment {
    int year,month,day,hour,minute;
    String teachId;                //教师编号
    String teachingClass;
    String stuId;
    String checkId="20170429s1";                //打卡id
    RecyclerView recyclerView;
    TextView isCheckin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.main_fra_record, container, false);


        BmobUser bmobUser = BmobUser.getCurrentUser();
        teachId=bmobUser.getUsername().toString();

        isCheckin=(TextView)recordView.findViewById(R.id.item_record_recycle_IsCheckin) ;
        isCheckin.setText("已打卡");
        //拼凑出查询需要的checkId
//        makeCkeckId();

//        queryCheckResult();

        queryTeachingClass();


        recyclerView=(RecyclerView)recordView.findViewById(R.id.recycler_result);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        return recordView;
    }

    private void queryTeachingClass() {
        BmobQuery<CheckRecord>queryTeachingClass=new BmobQuery<>();
        queryTeachingClass.addWhereEqualTo("checkId",checkId);
        queryTeachingClass.addWhereEqualTo("teaId",teachId);
        queryTeachingClass.findObjects(new FindListener<CheckRecord>() {
            @Override
            public void done(List<CheckRecord> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
                        for (CheckRecord checkRecord:list){
                            teachingClass=checkRecord.getTeachingClass();
//                            Toast.makeText(getContext(),teachingClass,Toast.LENGTH_LONG).show();

                            queryClassStuId();
                        }
                    }else {
                        Toast.makeText(getContext(),"您当前还未发起考勤",Toast.LENGTH_LONG).show();
                    }
                }
            }


        });
    }


    private void queryClassStuId() {
        final BmobQuery<Class_stuId> queryClassStuId=new BmobQuery<>();
        queryClassStuId.addWhereEqualTo("teachingClass",teachingClass);
        queryClassStuId.order("stuId");
        queryClassStuId.findObjects(new FindListener<Class_stuId>() {
            @Override
            public void done(List<Class_stuId> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
                        RecordAdapter recordAdapter=new RecordAdapter(list);
                        recyclerView.setAdapter(recordAdapter);

                        for (Class_stuId class_stuId:list){
                            stuId=class_stuId.getStuId();
                        }

                        queryIsCheckin();

                    }else {
                        Toast.makeText(getContext(),"这节课暂时还没有学生打卡哟~",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(),"fail~",Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private void queryIsCheckin() {
                BmobQuery<CheckResult>queryIsChockin=new BmobQuery<CheckResult>();
                queryIsChockin.addWhereEqualTo("stuId",stuId);
                queryIsChockin.findObjects(new FindListener<CheckResult>() {
                    @Override
                    public void done(List<CheckResult> list, BmobException e) {
                        if (e==null){
                            if (!list.isEmpty()){
                                isCheckin.setText("已打卡");
                            }
                        }else {

                        }
                    }
                });
    }






    //拼凑出checkId来
//    private void makeCkeckId(){
//
//        //判断月份和号数是否小于10，小于的话在它前面加个0保持checkId的位数一样
//
//        if (month>10&&day>10){
//
//            checkId=year+month+day+teachId;
//        }else if (month>10&&day<10){
//
//            checkId=year+month+"0"+day+teachId;
//        }else if (month<10&&day>10){
//
//            checkId=year+"0"+month+day+teachId;
//        }else if (month<10&&day<10){
//
//            checkId=year+"0"+month+"0"+day+teachId;
//        }
//    }



}



//    private void queryCheckResult() {
//        BmobQuery<CheckResult>query=new BmobQuery<>();
//        query.addWhereEqualTo("checkId",checkId);
//        query.order("stuId");
////        query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);
//        query.findObjects(new FindListener<CheckResult>() {
//            @Override
//            public void done(List<CheckResult> list, BmobException e) {
//                if (e==null){
//                    if (list.isEmpty()){
//                        Toast.makeText(getContext(),"这节课暂时还没有学生打卡哟~",Toast.LENGTH_LONG).show();
//                    }else {
//                        RecordAdapter recordAdapter=new RecordAdapter(list);
//                        recyclerView.setAdapter(recordAdapter);
//                    }
//                }else {
//                    Toast.makeText(getContext(),"这节课暂时还没有学生打卡哟~",Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }


