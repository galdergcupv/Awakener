package com.example.awakener;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {

    private List<Alarm> alarmList;
    private Context context;
    private OnAlarmLongClickListener onAlarmLongClickListener;

    public AlarmAdapter(Context context, List<Alarm> alarmList, OnAlarmLongClickListener onAlarmLongClickListener) {
        this.context = context;
        this.alarmList = alarmList;
        this.onAlarmLongClickListener = onAlarmLongClickListener;
    }
    public void setAlarmList(List<Alarm> alarmList) {
        this.alarmList = alarmList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        holder.bind(alarm);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onAlarmLongClickListener != null) {
                    onAlarmLongClickListener.onAlarmLongClicked(alarm);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (alarmList != null) {
            return alarmList.size();
        } else {
            return 0;
        }
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {

        private TextView timeTextView;
        private TextView nameTextView;

        public AlarmViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.time_text_view);
            nameTextView = itemView.findViewById(R.id.name_text_view);
        }

        public void bind(Alarm alarm) {
            timeTextView.setText(alarm.getTime());
            nameTextView.setText(alarm.getName());
        }
    }


    public interface OnAlarmLongClickListener {
        void onAlarmLongClicked(Alarm alarm);
    }


    public void setOnAlarmLongClickListener(OnAlarmLongClickListener listener) {
        this.onAlarmLongClickListener = listener;
    }



}
