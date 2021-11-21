package com.example.calendarplease;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {

    private final LayoutInflater mInflater;
    private final List<SyllabusDocument> mSyllabusDocumentList;

    public FileListAdapter(Context context, List<SyllabusDocument> syllabusDocumentList) {
        mInflater = LayoutInflater.from(context);
        this.mSyllabusDocumentList = syllabusDocumentList;
    }

    @NonNull
    @Override
    public FileListAdapter.FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.filelist_item, parent, false);
        return new FileViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FileListAdapter.FileViewHolder holder, int position) {
        holder.editTextFilePath.setText(mSyllabusDocumentList.get(position).getFileName());
        holder.buttonDelete.setOnClickListener(v -> {
            int index = holder.getAdapterPosition();
            mSyllabusDocumentList.remove(index);
            this.notifyItemRemoved(index);
        });
    }

    @Override
    public int getItemCount() {
        return mSyllabusDocumentList.size();
    }

    class FileViewHolder extends RecyclerView.ViewHolder {
        public final EditText editTextFilePath;
        public final ImageButton buttonDelete;
        final FileListAdapter mAdapter;

        public FileViewHolder(View itemView, FileListAdapter adapter) {
            super(itemView);
            editTextFilePath = itemView.findViewById(R.id.editText_file_path);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            this.mAdapter = adapter;
        }

    }
}
