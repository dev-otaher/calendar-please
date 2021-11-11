package com.example.calendarplease;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FileListAdapter extends RecyclerView.Adapter<FileListAdapter.FileViewHolder> {
    private LayoutInflater mInflater;

    class FileViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewCounter;
        public final EditText editTextFilePath;
        public final Button buttonBrowse;
        public final Button buttonDelete;

        final FileListAdapter mAdapter;

        public FileViewHolder(View itemView, FileListAdapter adapter) {
            super(itemView);
            textViewCounter = itemView.findViewById(R.id.textView_counter);
            editTextFilePath = itemView.findViewById(R.id.editText_file_path);
            buttonBrowse = itemView.findViewById(R.id.button_browse);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            this.mAdapter = adapter;
        }

    }

    public FileListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public FileListAdapter.FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.filelist_item, parent, false);
        return new FileViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FileListAdapter.FileViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
