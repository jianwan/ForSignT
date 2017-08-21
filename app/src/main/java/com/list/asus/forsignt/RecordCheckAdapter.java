package com.list.asus.forsignt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.list.asus.forsignt.bean.CheckResult;
import com.list.asus.forsignt.bean.Class_stuId;

import java.util.List;

/**
 * Created by wanjian on 2017/6/2.
 */

public class RecordCheckAdapter extends RecyclerView.Adapter<RecordCheckAdapter.ViewHolder> {

    private List<CheckResult> mCheckResult;
//    boolean a=false;


    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView item_record_recycle_stuId;
        TextView item_record_recycle_IsCheckin;
        Button changeCheckin;



        public ViewHolder(View itemView) {
            super(itemView);
//            item_record_recycle_stuId=(TextView)itemView.findViewById(R.id.item_record_recycle_stuId);
            item_record_recycle_IsCheckin=(TextView)itemView.findViewById(R.id.item_record_recycle_IsCheckin);
            changeCheckin=(Button)itemView.findViewById(R.id.changeCheckin);

        }
    }

    public RecordCheckAdapter(List<CheckResult> checkResults){
        mCheckResult=checkResults;
    }



    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int, List)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fra_record_recycle,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override {@link #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    public void onBindViewHolder(RecordCheckAdapter.ViewHolder holder, int position) {

        CheckResult checkResult=mCheckResult.get(position);
//        if (a==true){
//            holder.item_record_recycle_IsCheckin.setText("已打卡");
//        }else {
//            holder.item_record_recycle_IsCheckin.setText("未打卡");
//        }

        holder.item_record_recycle_IsCheckin.setText("已打卡");
    }



    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mCheckResult.size();
    }
}
