package com.list.asus.forsignt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.list.asus.forsignt.bean.Record;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by wanjian on 2017/6/2.
 */

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {
    private List<Record> recordList = new ArrayList<>();

    public RecordAdapter(List<Record> recordList ){
        this.recordList = recordList;
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
        public ViewHolder(View itemView) {
            super(itemView);
            item_record_recycle_stuId=(TextView)itemView.findViewById(R.id.item_record_recycle_stuId);
            item_record_recycle_IsCheckin=(TextView)itemView.findViewById(R.id.item_record_recycle_IsCheckin);
            item_record_recycle_stuName=(TextView)itemView.findViewById(R.id.item_record_recycle_stuName);
            changeCheckin=(Button)itemView.findViewById(R.id.changeCheckin);
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
