package com.example.xavieralmolda.zahori_android;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Xavier Almolda on 31/08/2015.
 */
public class SiteClickDialogFragment extends DialogFragment {

    private String siteCode;

    static SiteClickDialogFragment newInstance(String code) {
        SiteClickDialogFragment f = new SiteClickDialogFragment();
        f.siteCode = code;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().setTitle(siteCode);

        View v = inflater.inflate(R.layout.fragment_site_click_dialog,container, false);

        TextView txtGraph = (TextView)v.findViewById(R.id.txtShowGraph);

        txtGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent plotIntent = new Intent(getActivity(), XYPlotActivity.class);
                plotIntent.putExtra("code", siteCode);
                startActivity(plotIntent);
                getDialog().dismiss();
            }
        });

        TextView txtSendValue = (TextView)v.findViewById(R.id.txtSendValue);

        txtSendValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendValueDialogFragment dialog = SendValueDialogFragment.newInstance(siteCode);
                dialog.show(getFragmentManager(),siteCode);

                getDialog().dismiss();

            }
        });

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
