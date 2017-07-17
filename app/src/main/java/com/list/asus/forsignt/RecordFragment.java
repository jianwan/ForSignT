package com.list.asus.forsignt;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.list.asus.forsignt.bean.CheckRecord;
import com.list.asus.forsignt.bean.CheckResult;
import com.list.asus.forsignt.bean.Class_stuId;
import com.list.asus.forsignt.bean.Record;
import com.list.asus.forsignt.bean.Student;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by ASUS on 2017/5/20.
 *
 */

public class RecordFragment extends Fragment  {
    int year,month,day,hour,minute;
    String teachId;                //教师编号
    String teachingClass;
    String stuId;
    String stuName;
    String checkId;                //打卡id
    RecyclerView recyclerView;
    TextView isCheckin;

    List<CheckResult>checkResults=new ArrayList<>();
    List<Class_stuId>class_stuIds=new ArrayList<>();
    List<Record> stuRecord = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.main_fra_record, container, false);


        BmobUser bmobUser = BmobUser.getCurrentUser();
        teachId=bmobUser.getUsername().toString();

//        isCheckin=(TextView)recordView.findViewById(R.id.item_record_recycle_IsCheckin) ;
//        isCheckin.setText("已打卡");
        //拼凑出查询需要的checkId
//        makeCkeckId();

//        queryCheckResult();

        makeCkeckId();

        queryTeachingClass();


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

    //拼凑出checkId来
    private void makeCkeckId(){

//        getTime2();
//        //判断月份和号数是否小于10，小于的话在它前面加个0保持checkId的位数一样
//        String a=null;
//        String b=null;
//        if (month<10){
//            a="0"+month;
//        }else {
//            a=""+month;
//        }
//        if (day<10){
//            b="0"+day;
//        }else {
//            b=""+day;
//        }
//        checkId=year+a+b;
        SimpleDateFormat sDateFormat= new SimpleDateFormat("yyyyMMddHH");
        Date curDate=new Date(System.currentTimeMillis());
        String date = sDateFormat.format(curDate);
        checkId= ""+date+teachId;
        Log.d("that", "makeCkeckId: "+checkId);
//        queryClassStuId();
    }

    //得到当前系统时间  分年月日时分单独获取
    private void getTime2() {
        Calendar c=Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

    }

    private void queryClassStuId() {
        final BmobQuery<Class_stuId> queryClassStuId=new BmobQuery<>();
        queryClassStuId.addWhereEqualTo("teachingClass",teachingClass);
        Log.d("that", "teachingClass: "+teachingClass);
        queryClassStuId.order("stuId");
        queryClassStuId.findObjects(new FindListener<Class_stuId>() {
            @Override
            public void done(List<Class_stuId> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
//                        RecordAdapter recordAdapter=new RecordAdapter(list,checkResults);
//                        recyclerView.setAdapter(recordAdapter);

//                        Class_stuIdlist=list;
//                        Toast.makeText(getContext(),Class_stuIdlist.toString(),Toast.LENGTH_LONG).show();
//                        class_stuIds=list;
                        int count = 0;
                        int size = list.size();
                        for (Class_stuId class_stuId:list){
                            count++;
//                            queryStuNameFromStudent(class_stuId.getStuId());
                            queryIsCheckin(class_stuId.getStuId(), count, size);
//                            Toast.makeText(getContext(),"ok"+stuId,Toast.LENGTH_LONG).show();

                            Log.d("that", "done: "+" "+count+"  "+size+" "+class_stuId.getStuId());
                        }
                    }else {
                        Toast.makeText(getContext(),"这节课暂时还没有学生打卡哟~",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(),"fail~",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void queryStuNameFromStudent(String StuId) {
        BmobQuery<Student>queryStuName=new BmobQuery<Student>();
        queryStuName.addWhereEqualTo("stuId",StuId);
        Log.d("that", "queryStuNameFromStudent: "+StuId);
        queryStuName.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e==null){
                    if (!list.isEmpty()){
                       for (Student student:list){
                           stuName=student.getStuName();
                           Log.d("that", "stuName: "+stuName);
                       }

                    }else {
                        Log.d("that", "queryStuNameFromStudent: "+list.get(0).getStuName());
                    }
                }
            }
        });
    }


    private void queryIsCheckin(final String StuId, final int count, final int size) {
                final Record record= new Record();
                record.setStuId(StuId);
                BmobQuery<CheckResult>queryIsChockin=new BmobQuery<CheckResult>();
                queryIsChockin.addWhereEqualTo("stuId",StuId);
                queryIsChockin.addWhereEqualTo("checkId",checkId);
                queryIsChockin.findObjects(new FindListener<CheckResult>() {
                    @Override
                    public void done(List<CheckResult> list, BmobException e) {
                        if (e==null){
                            if (!list.isEmpty()){
//                                RecordAdapter recordAdapter=new RecordAdapter(class_stuIds,list);
//                                recyclerView.setAdapter(recordAdapter);
//                                isCheckin.setText("已打卡");

//                                Toast.makeText(getContext(),"ok"+stuId,Toast.LENGTH_LONG).show();                                                                     int


//                                if (list.get(0).getStuName().isEmpty()){
//                                    queryStuNameFromStudent(StuId);
//                                    record.setStuName(stuName);
//                                    Log.d("that", "11111 "+stuName);
//                                }else {
//                                    record.setStuName(list.get(0).getStuName());
//                                    Log.d("that", "22222 "+list.get(0).getStuName());
//
//                                }

                                Log.d("that", "list.get(0).getStuName()1 "+list.get(0).getStuName()+list.get(0).getStuId());
                                record.setStuName(list.get(0).getStuName());

                                Log.d("that", "list.get(0).getStuName() "+list.get(0).getStuName()+list.get(0).getStuId());
                                record.setClockStatus(true);
                                record.setRemark(list.get(0).getRemark()+"");

                                stuRecord.add(record);
                            } else {
//                                RecordAdapter recordAdapter=new RecordAdapter(class_stuIds,list);
//                                recyclerView.setAdapter(recordAdapter);
//                                Toast.makeText(getContext(),"fail1"+stuId,Toast.LENGTH_LONG).show();
                                record.setClockStatus(false);
                                stuRecord.add(record);
                            }
                            if (count == size){
                                recyclerView=(RecyclerView)getView().findViewById(R.id.recycler_result);
                                LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
                                recyclerView.setLayoutManager(layoutManager);
                                RecordAdapter recordAdapter=new RecordAdapter(getActivity(),stuRecord);
                                Log.d("TAG", "done: "+stuRecord.size());
                                recyclerView.setAdapter(recordAdapter);
                            }
                        }else {
                            Toast.makeText(getContext(),"fail",Toast.LENGTH_LONG).show();
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


