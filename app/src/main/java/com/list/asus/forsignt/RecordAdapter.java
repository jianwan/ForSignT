package com.list.asus.forsignt;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.list.asus.forsignt.bean.CheckResult;
import com.list.asus.forsignt.bean.Record;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/*
 * Created by wanjian on 2017/6/2.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private List<Record> recordList = new ArrayList<>();
    private Context context;
    String stuId;

    int year,month,day,hour,minute;

    public RecordAdapter(Context context,List<Record> recordList ){
        this.recordList = recordList;
        this.context=context;
//        Log.d("TAG", "done:33333333 "+recordList.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fra_record_recycle,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Record record = recordList.get(position);
        holder.item_record_recycle_stuId.setText(record.getStuId());
        holder.item_record_recycle_stuName.setText(record.getStuName());
        if (record.getClockStatus()){
            holder.item_record_recycle_IsCheckin.setText("已打卡");
        }else {
            holder.item_record_recycle_IsCheckin.setText("未打卡");
        }
    }


    @Override
    public int getItemCount() {
        return recordList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_record_recycle_stuId;
        TextView item_record_recycle_IsCheckin;
        TextView item_record_recycle_stuName;
        Button changeCheckin;
        String checkId="20170429s1";
        public ViewHolder(View itemView) {
            super(itemView);
            item_record_recycle_stuId=(TextView)itemView.findViewById(R.id.item_record_recycle_stuId);
            item_record_recycle_IsCheckin=(TextView)itemView.findViewById(R.id.item_record_recycle_IsCheckin);
            item_record_recycle_stuName=(TextView)itemView.findViewById(R.id.item_record_recycle_stuName);
            changeCheckin=(Button)itemView.findViewById(R.id.changeCheckin);
            changeCheckin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    makeCheckId();
                    stuId=item_record_recycle_stuId.getText().toString();
                    showDialog();
                    Log.d("bmob", "onClick: hhh"+item_record_recycle_stuId.getText().toString());
                }

                //拼凑出checkId来
                private void makeCheckId(){

                   BmobUser a=BmobUser.getCurrentUser();
                    String teachId=a.getUsername();
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

                //显示框并改变打卡结果
                private void showDialog() {
                    AlertDialog.Builder builder=new AlertDialog.Builder(context);
                    builder.setTitle("确定改变该生打卡结果？");

                    builder.setNeutralButton("取消", null);
                    builder.setPositiveButton("改变打卡", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(context,"您已改变学号为"+stuId+"的打卡结果",Toast.LENGTH_LONG).show();
                            queryFromCheckResult();
                            Log.d("bmob", "onClick: 222");

                        }

                        private void queryFromCheckResult() {
                            BmobQuery<CheckResult> queryFromCheckResult=new BmobQuery<>();
                            queryFromCheckResult.addWhereEqualTo("stuId",stuId);
                            queryFromCheckResult.findObjects(new FindListener<CheckResult>() {
                                @Override
                                public void done(List<CheckResult> list, BmobException e) {
                                    if (e==null){
                                        if (list.isEmpty()){
                                            addResultToCheckResult();
                                        }else {
                                            Toast.makeText(context,"学号为"+stuId+"的该生已打卡，请勿重复改变",Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                private void addResultToCheckResult() {
                                    CheckResult checkResult=new CheckResult();
                                    checkResult.setStuId(stuId);
                                    checkResult.setCheckID(checkId);
                                    Log.d("bmob", "addResultToCheckResult: "+checkId);
//                                    checkResult.setCheckStatus(true);
                                    checkResult.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e==null){
                                                Log.i("bmob","添加数据成功："+e.getMessage()+","+e.getErrorCode());
                                                item_record_recycle_IsCheckin.setText("已改变打卡");
                                            }else{
                                                Log.i("bmob","添加数据失败："+e.getMessage()+","+e.getErrorCode());
//                                                item_record_recycle_IsCheckin.setText("已改变打卡");
                                            }
                                        }
                                    });
                                }

                            });
                        }



                    });
                    builder.setNegativeButton("请假", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            Toast.makeText(context,"您已改变学号为"+stuId+"的打卡结果:请假",Toast.LENGTH_LONG).show();
                            Log.d("bmob", "onClick: 333");

                        }
                    });
                    builder.show();
                }


            });
        }
    }








//    private List<Class_stuId> mClass_stuId;
//    private List<CheckResult> mCheckResult;
//
//    boolean misCheckin=false;
//
//    String a;
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView item_record_recycle_stuId;
//        TextView item_record_recycle_IsCheckin;
//        Button changeCheckin;
//
//
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            item_record_recycle_stuId=(TextView)itemView.findViewById(R.id.item_record_recycle_stuId);
//            item_record_recycle_IsCheckin=(TextView)itemView.findViewById(R.id.item_record_recycle_IsCheckin);
//            changeCheckin=(Button)itemView.findViewById(R.id.changeCheckin);
//
//        }
//    }

//    public RecordAdapter(List<Class_stuId> class_stuIds,List<CheckResult>checkResults){
//        mClass_stuId=class_stuIds;
//        mCheckResult=checkResults;
////        misCheckin=isCheckin;
//    }
//
//
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fra_record_recycle,parent,false);
//        ViewHolder holder=new ViewHolder(view);
//        return holder;
//    }
//
//
//    public void onBindViewHolder(RecordAdapter.ViewHolder holder, int position) {
//
//        Class_stuId class_stuId=mClass_stuId.get(position);
//        holder.item_record_recycle_stuId.setText(class_stuId.getStuId());
//
////        CheckResult checkResult=mCheckResult.get(position);
////        String a=checkResult.getStuId();
////        holder.item_record_recycle_IsCheckin.setText(checkResult.getClockStatus().toString());
//
//         a=class_stuId.getStuId();
//
////        queryIsCheckIn();
//
////        if (checkResult.getClockStatus()==true){
////            holder.item_record_recycle_IsCheckin.setText("已打卡");
////        }else {
////            holder.item_record_recycle_IsCheckin.setText("未打卡");
////        }
//
//
//
//
//    }
//
//    private void queryIsCheckIn() {
//        BmobQuery<CheckResult>queryIsChockin=new BmobQuery<CheckResult>();
//        queryIsChockin.addWhereEqualTo("stuId",a);
//        queryIsChockin.findObjects(new FindListener<CheckResult>() {
//            @Override
//            public void done(List<CheckResult> list, BmobException e) {
//                if (e==null){
//                    if (!list.isEmpty()){
//                       misCheckin=true;
//
//                    }
//                }
//            }
//        });
//
//    }
//
//
//    /**
//     * Returns the total number of items in the data set held by the adapter.
//     *
//     * @return The total number of items in this adapter.
//     */
//    @Override
//    public int getItemCount() {
//        return mClass_stuId.size();
//
//    }
}
