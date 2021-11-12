package com.example.calendarplease;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

    private List<String> mUploadedFilesPathList;

    public static int DOC_REQUEST = 100;

    private final LayoutInflater mInflater;

    private Context mContext;

    class FileViewHolder extends RecyclerView.ViewHolder {
        public final TextView textViewCounter;
        public final EditText editTextFilePath;
        public final ImageButton buttonBrowse;
        public final ImageButton buttonDelete;

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

    public FileListAdapter(Context context, List<String> uploadedFilesPathList) {
        mInflater = LayoutInflater.from(context);
        this.mUploadedFilesPathList = uploadedFilesPathList;
        mContext = context;
    }

    @NonNull
    @Override
    public FileListAdapter.FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.filelist_item, parent, false);
        return new FileViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FileListAdapter.FileViewHolder holder, int position) {
        holder.textViewCounter.setText((position + 1) + ")");
        holder.editTextFilePath.setText(this.mUploadedFilesPathList.get(position));
        holder.buttonBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                ((Activity) mContext).startActivityForResult(intent, DOC_REQUEST);
            }
        });
//        holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mUploadedFilesPathList.remove(holder.getAdapterPosition());
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mUploadedFilesPathList.size();
    }


}
