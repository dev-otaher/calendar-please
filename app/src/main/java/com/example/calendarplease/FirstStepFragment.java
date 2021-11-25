package com.example.calendarplease;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstStepFragment extends Fragment {
    public static final int BROWSE_DOC_REQUEST = 100;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String UPLOADED_FILES_PATH_LIST = "UPLOADED_FILES_PATH_LIST";
    private static final String FILE_DESCRIPTORS = "FILE_DESCRIPTORS";

    private EditText mCalendarName;
    private EditText mStartDate;
    private RecyclerView mRecyclerView;
    // TODO: Rename and change types of parameters

    private SchoolCalendar mSchoolCalendar;

    public FirstStepFragment() {
        // Required empty public constructor
    }

    public static FirstStepFragment newInstance(SchoolCalendar schoolCalendar) {
        FirstStepFragment fragment = new FirstStepFragment();
        fragment.mSchoolCalendar = schoolCalendar;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSchoolCalendar.setSchoolStartDate(Calendar.getInstance());
        mSchoolCalendar.setTitle(getResources().getString(R.string.cal_name_placeholder, mSchoolCalendar.getSchoolStartDate().getTimeInMillis()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_first_step, container, false);

        mCalendarName = root.findViewById(R.id.editText_calendar_name);
        mCalendarName.setText(mSchoolCalendar.getTitle());

        mStartDate = root.findViewById(R.id.editText_start_date);
        mStartDate.setText(new SimpleDateFormat("dd/MM/y", Locale.getDefault()).format(mSchoolCalendar.getSchoolStartDate().getTime()));

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            mSchoolCalendar.setSchoolStartDate(new Calendar.Builder().setDate(year, month, dayOfMonth).build());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/y", Locale.getDefault());
            mStartDate.setText(simpleDateFormat.format(mSchoolCalendar.getSchoolStartDate().getTime()));
        }, mSchoolCalendar.getSchoolStartDate().get(Calendar.YEAR), mSchoolCalendar.getSchoolStartDate().get(Calendar.MONTH), mSchoolCalendar.getSchoolStartDate().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle("School Start Date");


        ImageButton buttonPickDate = root.findViewById(R.id.button_show_date_dialog);
        buttonPickDate.setOnClickListener(v -> datePickerDialog.show());

        ImageButton buttonAddFile = root.findViewById(R.id.button_add_file_path);
        buttonAddFile.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            getActivity().startActivityForResult(intent, BROWSE_DOC_REQUEST);
        });

        mRecyclerView = root.findViewById(R.id.recyclerview);
        FileListAdapter fileListAdapter = new FileListAdapter(getContext(), mSchoolCalendar.getSyllabusDocumentList());
        mRecyclerView.setAdapter(fileListAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }
}