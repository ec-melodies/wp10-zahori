package com.example.xavieralmolda.zahori_android;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Xavier Almolda on 01/04/2015.
 */
public class LevelValuesDialogFragment extends DialogFragment {

    private ArrayList<LevelValuesItem> mDataset;

    private RecyclerView mRecyclerView;
    private LevelValuesAdapter mAdapter;
    //private RecyclerView.LayoutManager mLayoutManager;
    private MyLinearLayoutManager mLayoutManager;

    static LevelValuesDialogFragment newInstance() {
        LevelValuesDialogFragment f = new LevelValuesDialogFragment();
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mDataset = ((MainActivity)getActivity()).myDataset;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Water Level");

        View v = inflater.inflate(R.layout.level_values_list_dialog,container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.level_values_recycler_list);

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        //mLayoutManager = new LinearLayoutManager(getActivity());
        //mRecyclerView.setLayoutManager(mLayoutManager);

        mLayoutManager = new MyLinearLayoutManager(getActivity(), OrientationHelper.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new LevelValuesAdapter(mDataset);
        mRecyclerView.setAdapter(mAdapter);

        return v;

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
