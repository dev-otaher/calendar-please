package com.example.calendarplease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class AvailableColumnsAdapter extends RecyclerView.Adapter<AvailableColumnsAdapter.AvailableColumnsViewHolder> {

    private final LinkedList<String> mColumnList;
    private LayoutInflater mInflater;

    public AvailableColumnsAdapter(Context context, LinkedList<String> columnList) {
        mInflater = LayoutInflater.from(context);
        this.mColumnList = columnList;
    }

    @NonNull
    @Override
    public AvailableColumnsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.available_columns_item, parent, false);
        return new AvailableColumnsViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableColumnsViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mColumnList.size();
    }

    class AvailableColumnsViewHolder extends RecyclerView.ViewHolder {
        private final TextView columnTitleTextView;
        private final AvailableColumnsAdapter mAdapter;

        public AvailableColumnsViewHolder(View itemView, AvailableColumnsAdapter adapter) {
            super(itemView);
            columnTitleTextView = itemView.findViewById(R.id.column_title);
            mAdapter = adapter;
        }
    }
}
