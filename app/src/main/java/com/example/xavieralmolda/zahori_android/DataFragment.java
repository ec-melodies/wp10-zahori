package com.example.xavieralmolda.zahori_android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class DataFragment extends Fragment {

    private MainActivity mainActivity;

    private TextView txtLatitude;
    private TextView txtLongitude;
    private TextView txtElevation;
    private TextView txtLevel;
    private TextView txtDepth;

    private Double mElevation = 0.0;
    private Double mLevel = 0.0;
    private Double mDepth = 0.0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;


//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment DataFragment.
//     */
    // TODO: Rename and change types and number of parameters
//    public static DataFragment newInstance(String param1, String param2) {
//        DataFragment fragment = new DataFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static DataFragment newInstance() {
        DataFragment fragment = new DataFragment();
        return fragment;
    }

    public DataFragment() {
        // Required empty public constructor
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
////        if (getArguments() != null) {
////            mParam1 = getArguments().getString(ARG_PARAM1);
////            mParam2 = getArguments().getString(ARG_PARAM2);
////        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_data, container, false);
        mainActivity = (MainActivity) getActivity();

        txtLatitude = (TextView)v.findViewById(R.id.txtLatitude);
        txtLongitude = (TextView)v.findViewById(R.id.txtLongitude);
        txtElevation = (TextView)v.findViewById(R.id.txtElevation);
        txtLevel = (TextView) v.findViewById(R.id.txtWaterLevel);
        txtDepth = (TextView) v.findViewById(R.id.txtWateDepth);

        txtLatitude.setText(String.format("%.4f", mainActivity._latitude));
        txtLongitude.setText(String.format("%.4f", mainActivity._longitude));
        txtElevation.setText("0.0");
        txtLevel.setText("0.0");
        txtDepth.setText("0.0");

//        ImageButton btnElevation = (ImageButton) v.findViewById(R.id.btnUpdateElevation);
//
//        btnElevation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new GetElevationTask().execute();
//            }
//        });

        Button btnCota = (Button) v.findViewById(R.id.btnNewLevel);

        btnCota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetElevationTask().execute();
            }
        });

        ImageButton btnSend = (ImageButton) v.findViewById(R.id.btnUpdateDepth);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendValueDialogFragment dialog = SendValueDialogFragment.newInstance(Double.parseDouble(txtLatitude.getText().toString()),Double.parseDouble(txtLongitude.getText().toString()));
                dialog.show(getFragmentManager(),"position");

            }
        });

        return v;
    }


    public void updateUI() {

        txtLatitude.setText(String.format("%.4f", mainActivity._latitude));
        txtLongitude.setText(String.format("%.4f", mainActivity._longitude));
        txtElevation.setText(String.format("%.2f", mElevation));
        txtLevel.setText(String.format("%.2f", mLevel));
        txtDepth.setText(String.format("%.2f", mDepth));

    }


    private class GetElevationTask extends AsyncTask<String, Void, Double> {


        public GetElevationTask(){

        }

        @Override
        protected Double doInBackground(String... params) {


            Double result = 0.0;

            String urlString="https://maps.googleapis.com/maps/api/elevation/xml?locations=" + mainActivity._latitude + "," + mainActivity._longitude + "&sensor=true&key=" + getString(R.string.google_api_server_key);
            Log.d("URL", urlString);
            InputStream in = null;

            // HTTP Get
            try {

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());


            } catch (Exception e ) {

                System.out.println(e.getMessage());

            }

            // Parse XML
            XmlPullParserFactory pullParserFactory;

            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = pullParserFactory.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);

                result = parseXML(parser);

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();

            }

            return result;

        }

        protected void onPostExecute(Double result) {

            mElevation = result;

            new GetWaterLevelTask().execute();

            updateUI();

        }



        private Double parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {

            Double result = 0.0;

            int eventType = parser.getEventType();

            while( eventType!= XmlPullParser.END_DOCUMENT ) {

                String name;

                switch(eventType)
                {
                    case XmlPullParser.START_TAG:

                        name = parser.getName();

                        if ( name.equals("elevation")) {
                            result = Double.parseDouble(parser.nextText());
                        }

                        break;


                    case XmlPullParser.END_TAG:

                        name = parser.getName();

                        break;
                } // end switch

                eventType = parser.next();
            } // end while

            return result;

        }


    }


    private class GetWaterLevelTask extends AsyncTask<String, Void, ArrayList<SeriesValue>> {


        public GetWaterLevelTask(){

        }

        @Override
        protected ArrayList<SeriesValue> doInBackground(String... params) {

            ArrayList<SeriesValue> result = new ArrayList<>();

            String urlString = "http://h2vmservice.cloudapp.net/GetDepth?longitude=" + mainActivity._longitude + "&latitude=" + mainActivity._latitude + "&modelid=3&clientid=tahal";

            InputStream in = null;

            // HTTP Get
            try {

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());


            } catch (Exception e ) {

                System.out.println(e.getMessage());

            }

            // Parse XML
            XmlPullParserFactory pullParserFactory;

            try {
                pullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = pullParserFactory.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);

                result = parseXML(parser);

                //resultToDisplay = parser.getText();

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();

            }

            return result;

        }

        protected void onPostExecute(ArrayList<SeriesValue> result) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            if(result.size() == 2) {

                //DepthValuesDialogFragment dialog = DepthValuesDialogFragment.newInstance(sdf.format(result.get(0).Date), sdf.format(result.get(1).Date), result.get(0).Value, result.get(1).Value);
                //dialog.show(getFragmentManager(), "dialog");

                mLevel = result.get(0).Value;
                mDepth = mElevation - mLevel;
                updateUI();


            }

        }



        private ArrayList<SeriesValue> parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {

            ArrayList<SeriesValue> result = new ArrayList<>();

            int eventType = parser.getEventType();

            SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");

            while( eventType!= XmlPullParser.END_DOCUMENT ) {

                String name;

                switch(eventType)
                {
                    case XmlPullParser.START_TAG:

                        name = parser.getName();

                        if ( name.equals("value")) {

                            try {
                                SeriesValue val = new SeriesValue(sdf.parse(parser.getAttributeValue("", "dateTime")), Double.parseDouble(parser.nextText()));
                                result.add(val);
                            }
                            catch(ParseException pe) {
                                Log.d("PARSE", "Parse exception in depth value");
                            }
                        }

                        break;


                    case XmlPullParser.END_TAG:

                        name = parser.getName();

                        if(name.equals("site")) {

                        }
                        break;
                } // end switch

                eventType = parser.next();
            } // end while

            return result;

        }


    }


}
