package com.example.xavieralmolda.zahori_android;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Xavier Almolda on 01/04/2015.
 */
public class LevelValuesDialogFragment extends DialogFragment {

    private String mLayer1Date;
    private String mLayer2Date;
    private Double mLayer1Level;
    private Double mLayer2Level;

    static LevelValuesDialogFragment newInstance(String layer1Date, String layer2Date, Double layer1Level, Double layer2Level) {
        LevelValuesDialogFragment f = new LevelValuesDialogFragment();

        Bundle args = new Bundle();
        args.putString("date1", layer1Date);
        args.putString("date2", layer2Date);
        args.putDouble("level1", layer1Level);
        args.putDouble("level2", layer2Level);
        f.setArguments(args);

        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLayer1Date = getArguments().getString("date1");
        mLayer2Date = getArguments().getString("date2");
        mLayer1Level = getArguments().getDouble("level1");
        mLayer2Level = getArguments().getDouble("level2");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle("Water Level");

        View v = inflater.inflate(R.layout.fragment_level_values_dialog,container, false);

        TextView txtDate1 = (TextView) v.findViewById(R.id.txtDate1);
        TextView txtDate2 = (TextView) v.findViewById(R.id.txtDate2);
        TextView txtValue1 = (TextView) v.findViewById(R.id.txtValue1);
        TextView txtValue2 = (TextView) v.findViewById(R.id.txtValue2);


        txtDate1.setText(this.mLayer1Date);
        txtDate2.setText(this.mLayer2Date);
        txtValue1.setText(String.format("%.2f", this.mLayer1Level));
        txtValue2.setText(String.format("%.2f", this.mLayer2Level));

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
