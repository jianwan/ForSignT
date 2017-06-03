package com.list.asus.forsignt;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.list.asus.forsignt.bean.CheckRecord;
import com.list.asus.forsignt.bean.Course;
import com.list.asus.forsignt.bean.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by ASUS on 2017/5/20.
 *
 */

public class CheckFragment extends Fragment{


    public TextView range;          //range、isCheck是button下的textView
    public TextView isCheck;
    public TextView id;             //id、courseN是教师ID以及课程的名字，两个textView
    public TextView courseN;
    public ImageView image_range;
    public ImageView image_isCheck;
    public Button check;           //buttton check
    int year,month,day,hour,minute;
    String teachingClass;          //教学班
    String courseId;                //课程id
    String courseName;             //用String储存课程名称
    String teachId;                //教师编号
    String checkId;                //打卡id
    String classTime;              //课的节次
    Boolean isHasClass=false;
    Boolean isCheckin=true;
    int count=0;                   //记录button的次数

    Boolean isRightPlace=true;
//    Boolean isCheck;




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View checkView = inflater.inflate(R.layout.main_fra_check, container, false);



        initView(checkView);

        BmobUser bmobUser = BmobUser.getCurrentUser();
        id.setText(bmobUser.getUsername());
        teachId=id.getText().toString();







        //得到系统时间为拼凑CkeckId
        getTime2();
        //得到课的节次classTime，提供给查询Schedule表
        makeClassTime();
        //拼凑出checkId来
        makeCkeckId();



        //查询授课表里的该教师的教学班、课程名称，查询到才能发起打卡
        querySchedule();

        //在checkRecord中查询是否有打卡记录，没有才能打卡成功
        queryCheckRecord();

        //点击事件，即点击成功就是向checkRecord表中添加一行数据
        check.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

 //防止多次点击，控制十分钟内点的第二次无效
//                if (!ButtonUtils.isFastDoubleClick(R.id.check)) {
//
//                    addToCheckResult();
//
//                }

                if (isRightPlace==true&&isHasClass==true&&isCheckin==false){
                    //向考勤记录表里添加一行发起打卡数据
                    addRecordToCheckRecord();
                }
                //提醒用户请勿多次点击打卡
                    count++;
                if (count>=2){
                    Toast.makeText(getContext(),"请勿多次点击打卡",Toast.LENGTH_LONG).show();
                }

            }
        });

        return checkView;

    }

    //初始化控件
    private void initView(View checkView) {
        //上边两个TextView
        id=(TextView)checkView.findViewById(R.id.name) ;
        courseN=(TextView)checkView.findViewById(R.id.course);
        //button下边的两个TextView
        range=(TextView) checkView.findViewById(R.id.range);
        isCheck=(TextView)checkView.findViewById(R.id.isCheck);
        image_isCheck=(ImageView)checkView.findViewById(R.id.image_isCheck);
        check=(Button) checkView.findViewById(R.id.check);
    }




    //根据教师的Id和课程的节次 在授课表里 查询教学班
    private void querySchedule2() {

        final BmobQuery<Schedule>queryTeachingClass=new BmobQuery<>();
        queryTeachingClass.addWhereEqualTo("teaId",teachId);
        queryTeachingClass.addWhereEqualTo("classTime",classTime);
//        queryTeachingClass.setLimit(1);
        queryTeachingClass.findObjects(new FindListener<Schedule>() {
            @Override
            public void done(List<Schedule> list, BmobException e) {
                if (list!=null){
                    for (Schedule schedule:list){
                        //teachingClass为教学班号
                        teachingClass= schedule.getTeachingClass();
                        courseId=schedule.getCourseId();

                        courseName=schedule.getCourseName();
                        courseN.setText("当前课程："+courseName);

                        isHasClass=true;


                    }
                }else{
                    //此处好像有点问题
//                    Toast.makeText(getContext(),"当前你没有课哦",Toast.LENGTH_SHORT).show();
                    courseN.setText("当前你没有课哦");
                    Toast.makeText(getContext(),"4",Toast.LENGTH_SHORT).show();
                    Log.d("","44444444444");

                }

            }



        });

    }




    private void querySchedule() {

       BmobQuery<Schedule>queryTeaId=new BmobQuery<>();
        queryTeaId.addWhereEqualTo("teaId",teachId);
        BmobQuery<Schedule>queryClassTime=new BmobQuery<>();
        queryClassTime.addWhereEqualTo("classTime",classTime);
        List<BmobQuery<Schedule>>querySchedule=new ArrayList<BmobQuery<Schedule>>();
        querySchedule.add(queryTeaId);
        querySchedule.add(queryClassTime);

        BmobQuery<Schedule>query=new BmobQuery<>();
        query.and(querySchedule);
        query.findObjects(new FindListener<Schedule>() {
            @Override
            public void done(List<Schedule> list, BmobException e) {


                if (e==null){
                    if (!list.isEmpty()){
                        for (Schedule schedule:list){
                            //teachingClass为教学班号
                            teachingClass= schedule.getTeachingClass();
                            courseId=schedule.getCourseId();

                            courseName=schedule.getCourseName();
                            courseN.setText("当前课程："+courseName);

                            isHasClass=true;


                        }

                    }else {
                        courseN.setText("当前你没有课哦");

                    }
                }else{
                    //此处好像有点问题

                    Toast.makeText(getContext(), "失败"+e.getErrorCode(),Toast.LENGTH_LONG).show();

                }


            }
        });
    }

    private void queryCheckRecord() {
        BmobQuery<CheckRecord>queryTeaId=new BmobQuery<>();
        queryTeaId.addWhereEqualTo("teaId",teachId);
        BmobQuery<CheckRecord>queryCheckId=new BmobQuery<>();
        queryCheckId.addWhereEqualTo("checkId",checkId);
        List<BmobQuery<CheckRecord>>querySchedule=new ArrayList<>();
        querySchedule.add(queryTeaId);
        querySchedule.add(queryCheckId);

        BmobQuery<CheckRecord>query=new BmobQuery<>();
        query.and(querySchedule);
        query.findObjects(new FindListener<CheckRecord>() {
            @Override
            public void done(List<CheckRecord> list, BmobException e) {
               if (e==null){
                   if (list.isEmpty()){
                       isCheckin=false;
                   }else {
                       check.setText("已发起考勤");
                       isCheckin=true;

                      for (CheckRecord checkRecord:list){
                          image_isCheck.setImageResource(R.drawable.yes);
                          isCheck.setText("上次发起考勤时间是："+checkRecord.getUpdatedAt());

                      }
                   }
               }else {
                   Toast.makeText(getContext(), "失败"+e.getErrorCode(),Toast.LENGTH_LONG).show();

               }
            }
        });

    }



    //向考勤记录表里添加一行发起打卡数据
    private void addRecordToCheckRecord() {

        //点击butto向后台check_record表中添加一条记录
        CheckRecord check_record=new CheckRecord();
        check_record.setCheckId(checkId);
        check_record.setTeachingClass(teachingClass);
        check_record.setTeaId(teachId);
        check_record.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    //考勤成功就把button文字改变
                    check.setText("发起考勤成功");
                    image_isCheck.setImageResource(R.drawable.yes);

                    isCheckin=true;

                    getTime1();


                }else {
                    Toast.makeText(getContext(),"发起考勤失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //根据手机系统时间来判断现在是第几节课
    private void makeClassTime(){

//          此处为实际函数方法
//        if (hour==8||hour==9){
//            classTime="星期三一二节课";
//
//        }else if (hour==10||hour==11){
//            classTime="星期三三四节课";
//
//        }else if (hour==14||hour==15){
//            classTime="星期一五六节课";
//        }else if (hour==16||hour==17){
//            classTime="星期一七八节课";
//        }else if (hour==19||hour==20){
//           classTime="星期一九十节课";
//        }else if (hour==21||hour==22){
//            classTime="星期一十一十二节课";
//        }
        //下面一行为测试代码

        classTime="星期一三四节课";
    }


    //拼凑出checkId来
    private void makeCkeckId(){

        //判断月份和号数是否小于10，小于的话在它前面加个0保持checkId的位数一样

        if (month>10&&day>10){

            checkId=year+month+day+teachId;
        }else if (month>10&&day<10){

            checkId=year+month+"0"+day+teachId;
        }else if (month<10&&day>10){

            checkId=year+"0"+month+day+teachId;
        }else if (month<10&&day<10){

            checkId=year+"0"+month+"0"+day+teachId;
        }
    }

    //得到当前系统时间  直接获取总时间
    private void getTime1() {
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy年MM月dd日  HH:mm:ss     ");
        Date curDate=new Date(System.currentTimeMillis());
        String date=sDateFormat.format(curDate);
        isCheck.setText("发起打卡时间是："+date);
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


}
