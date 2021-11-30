package com.example.calendarplease;

import android.content.Context;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalendarListAdapter extends RecyclerView.Adapter<CalendarListAdapter.CalendarViewHolder> {
    Context context;
    List<String> mCalendarNameList;

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

    class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public final TextView textViewCalendarName;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCalendarName = itemView.findViewById(R.id.textView_calendar_name);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, 10, getAdapterPosition(), "Copy to Downloads...");
            new MenuInflater(context).inflate(R.menu.menu_context, menu);
        }
    }
}
