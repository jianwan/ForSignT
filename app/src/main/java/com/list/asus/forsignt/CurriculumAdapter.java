package com.list.asus.forsignt;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.list.asus.forsignt.bean.Schedule;

import java.util.List;

/**
 * Created by wanjian on 2017/5/25.
 */

public class CurriculumAdapter extends RecyclerView.Adapter<CurriculumAdapter.ViewHolder> {

    private List<Schedule> mScheduleList;




    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView classTime;
        TextView classRoom;

        public ViewHolder(View itemView) {
            super(itemView);
            classTime=(TextView)itemView.findViewById(R.id.item_curriculum_classTime);
//            classRoom=(TextView)itemView.findViewById(R.id.item_curriculum_classRoom);
        }
    }

    public CurriculumAdapter(List<Schedule>scheduleList){
        mScheduleList=scheduleList;
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
    public CurriculumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.main_fra_curriculum_recycle,parent,false);
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
    @Override
    public void onBindViewHolder(CurriculumAdapter.ViewHolder holder, int position) {
        Schedule schedule=mScheduleList.get(position);
        holder.classTime.setText(schedule.getClassTime()+"    "+schedule.getCourseName()+"    "+schedule.getClassroom());
//        holder.classRoom.setText(schedule.getClassroom());
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return mScheduleList.size();
    }


}
