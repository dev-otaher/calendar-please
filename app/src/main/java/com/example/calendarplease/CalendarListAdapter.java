package com.example.calendarplease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.CalendarViewHolder> {

    Context context;
    private List<String> mCalendarNameList;

    public CalendarListAdapter(Context context, List<String> calendarNameList) {
        this.context = context;
        this.mCalendarNameList = calendarNameList;
    }

    @NonNull
    @Override
    public CalendarListAdapter.CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.calendarlist_item, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarListAdapter.CalendarViewHolder holder, int position) {
        holder.textViewCalendarName.setText(mCalendarNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return this.mCalendarNameList.size();
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewCalendarName;
        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCalendarName = itemView.findViewById(R.id.textView_calendar_name);
        }
    }
}
