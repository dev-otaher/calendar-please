package com.example.calendarplease;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        SyllabusDocument currentSyllabus = mSyllabusDocumentList.get(position);
        holder.textViewFilePath.setText(currentSyllabus.getFileName());
        holder.editTextPrefix.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSyllabus.setEventPrefix(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
        public final TextView textViewFilePath;
        public final ImageButton buttonDelete;
        public final EditText editTextPrefix;
        final FileListAdapter mAdapter;

        public FileViewHolder(View itemView, FileListAdapter adapter) {
            super(itemView);
            textViewFilePath = itemView.findViewById(R.id.textView_file_path);
            buttonDelete = itemView.findViewById(R.id.button_delete);
            editTextPrefix = itemView.findViewById(R.id.editText_events_prefix);
            this.mAdapter = adapter;
        }

    }
}
