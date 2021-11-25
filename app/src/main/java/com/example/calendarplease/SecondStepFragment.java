package com.example.calendarplease;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SecondStepFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SecondStepFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private AvailableColumnsAdapter mAdapter;

    private List<SyllabusDocument> mSyllabusDocumentList;

    public SecondStepFragment() {
        // Required empty public constructor
    }

    public static SecondStepFragment newInstance(List<SyllabusDocument> syllabusDocumentList) {
        SecondStepFragment fragment = new SecondStepFragment();
        fragment.mSyllabusDocumentList = syllabusDocumentList;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("meow2", "1111111111111111" + getActivity().getContentResolver().toString());
        mSyllabusDocumentList.get(0).setContentResolver(getActivity().getContentResolver());
        Hashtable<Integer, String> prefColumns = mSyllabusDocumentList.get(0).fetchWcoHeaderColumns();
        prefColumns.forEach((integer, s) -> {
            Log.d("meow", integer + ". " + s);
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_second_step, container, false);
        mRecyclerView = root.findViewById(R.id.column_recyclerview);
        mAdapter = new AvailableColumnsAdapter(getContext(), this.mSyllabusDocumentList);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }
}