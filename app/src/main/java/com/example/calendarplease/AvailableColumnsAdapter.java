package com.example.calendarplease;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AvailableColumnsAdapter extends RecyclerView.Adapter<AvailableColumnsAdapter.SyllabusColumnViewHolder> {

    private LayoutInflater mInflater;
    private List<SyllabusDocument> mSyllabusDocumentList;

    public AvailableColumnsAdapter(Context context, List<SyllabusDocument> syllabusDocumentList) {
        mInflater = LayoutInflater.from(context);
        this.mSyllabusDocumentList = syllabusDocumentList;
    }

    @NonNull
    @Override
    public SyllabusColumnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.available_columns_item, parent, false);
        return new SyllabusColumnViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SyllabusColumnViewHolder holder, int position) {
        Log.d("meow", "onBindViewHolder");
        SyllabusDocument currentSyllabus = mSyllabusDocumentList.get(position);


    }

    @Override
    public int getItemCount() {
        return mSyllabusDocumentList.size();
    }

    class SyllabusColumnViewHolder extends RecyclerView.ViewHolder {
        public final TextView columnTitleTextView;
        final AvailableColumnsAdapter mAdapter;

        public SyllabusColumnViewHolder(View itemView, AvailableColumnsAdapter adapter) {
            super(itemView);
            columnTitleTextView = itemView.findViewById(R.id.column_title);
            mAdapter = adapter;
        }
    }
}
