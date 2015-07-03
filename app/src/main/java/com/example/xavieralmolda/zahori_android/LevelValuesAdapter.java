package com.example.xavieralmolda.zahori_android;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Xavier Almolda on 01/07/2015.
 */
public class LevelValuesAdapter extends RecyclerView.Adapter<LevelValuesAdapter.ViewHolder> {
    private ArrayList<LevelValuesItem> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public TextView txtLayer;
        public TextView txtLevel;
        public TextView txtDate;

        private Context mContext;


        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            txtLayer = (TextView) v.findViewById(R.id.lblLayer);
            txtLevel = (TextView) v.findViewById(R.id.txtValue);
            txtDate = (TextView) v.findViewById(R.id.txtDate);

            mContext = v.getContext();
        }

        @Override
        public void onClick(View v) {

        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public LevelValuesAdapter(ArrayList<LevelValuesItem> myDataset) {
        mDataset = myDataset;
    }

    public void add(LevelValuesItem task) {
        this.mDataset.add(task);
        notifyItemInserted(this.mDataset.indexOf(task));
    }

    public void add(LevelValuesItem task, int position) {
        this.mDataset.add(position, task);
        notifyItemInserted(position);
    }

    public void updateData(ArrayList<LevelValuesItem> myDataset) {
        mDataset = myDataset;
        notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LevelValuesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layer_row, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final LevelValuesItem name = mDataset.get(position);
        holder.txtLayer.setText(mDataset.get(position).Layer);
        holder.txtLevel.setText(mDataset.get(position).Level.toString());

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        holder.txtDate.setText(formatter.format(mDataset.get(position).Date));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
