package com.example.calendarplease;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AvailableColumnsAdapter extends RecyclerView.Adapter<AvailableColumnsAdapter.SyllabusColumnViewHolder> {

    private LayoutInflater mInflater;
    private List<SyllabusDocument> mSyllabusDocumentList;
    private Context mContext;

    public AvailableColumnsAdapter(Context context, List<SyllabusDocument> syllabusDocumentList) {
        mInflater = LayoutInflater.from(context);
        this.mSyllabusDocumentList = syllabusDocumentList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public SyllabusColumnViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.available_columns_item, parent, false);
        return new SyllabusColumnViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull SyllabusColumnViewHolder holder, int position) {
        SyllabusDocument currentSyllabus = mSyllabusDocumentList.get(position);
        if (currentSyllabus.getAvailableColumnsIndex() == null)
            new FetchAvailableColumns(mContext, currentSyllabus, holder).execute(currentSyllabus.createParcelFileDescriptor());
        holder.columnTitleTextView.setText(currentSyllabus.getFileName());

    }



    @Override
    public int getItemCount() {
        return mSyllabusDocumentList.size();
    }

    class SyllabusColumnViewHolder extends RecyclerView.ViewHolder {
        final AvailableColumnsAdapter mAdapter;
        public final LinearLayoutCompat linearLayout;
        public final TextView columnTitleTextView;
        public final ProgressBar progressBar;

        public SyllabusColumnViewHolder(View itemView, AvailableColumnsAdapter adapter) {
            super(itemView);
            mAdapter = adapter;
            linearLayout = (LinearLayoutCompat) itemView;
            columnTitleTextView = linearLayout.findViewById(R.id.column_title);
            progressBar = linearLayout.findViewById(R.id.progress_bar);
        }
    }
}
