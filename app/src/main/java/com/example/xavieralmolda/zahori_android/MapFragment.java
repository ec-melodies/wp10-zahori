package com.example.xavieralmolda.zahori_android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.HashMap;

/**
 * Created by Xavier Almolda on 30/03/2015.
 */
public class MapFragment extends Fragment {

    MapView mMapView;
    private GoogleMap googleMap;

//    private double _latitude = 41.325157;
//    private double _longitude = 2.09799;

    private HashMap<String, Site> _sites;

    private MainActivity mainActivity;

    private Marker userPositionMarker;

    public MapFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map, container,
                false);

        mainActivity = (MainActivity) getActivity();

        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        _sites = new HashMap<>();

        googleMap = mMapView.getMap();
        // latitude and longitude

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(!marker.getTitle().equals("Your position")) {

                    Toast toast = Toast.makeText(getActivity(), _sites.get(marker.getTitle()).Code, Toast.LENGTH_LONG);
                    toast.show();

                    Intent plotIntent = new Intent(getActivity(), XYPlotActivity.class);
                    plotIntent.putExtra("code", _sites.get(marker.getTitle()).Code);
                    startActivity(plotIntent);

                }else{


                    new GetDepthValuesTask().execute();

                }



                return true;
            }
        });


        // create marker
        MarkerOptions markerOptions = new MarkerOptions().position(
                new LatLng(mainActivity._latitude, mainActivity._longitude)).title("Your position");

        // Changing marker icon
        markerOptions.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

        // adding marker
        userPositionMarker =  googleMap.addMarker(markerOptions);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(mainActivity._latitude, mainActivity._longitude)).zoom(12).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        //new GetODMSitesTask().execute();


        Button btnMap = (Button) v.findViewById(R.id.map_button);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GetODMSitesTask().execute();
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void AddSitesToMap() {

        if(googleMap != null) {

            for(Site s : _sites.values())
            {
                googleMap.addMarker(new MarkerOptions().position(new LatLng(s.Latitude, s.Longitude)).title(s.Name));
            }

        }

    }

    private class GetODMSitesTask extends AsyncTask<String, Void, ArrayList<Site>> {


        public GetODMSitesTask(){

        }

        @Override
        protected ArrayList<Site> doInBackground(String... params) {


            ArrayList<Site> result = new ArrayList<>();

            //String urlString="http://192.168.1.37:15003/TTMS_Service/TTMS_Service/GetActivities/aaa"; // URL to call
            //String urlString=params[0]; // URL to call

            String urlString="http://h2vmservice.cloudapp.net/GetODMSites?longitude=" + mainActivity._longitude + "&latitude=" + mainActivity._latitude + "&meters=10000&aquifer=2&clientid=aca"; // URL to call

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

            } catch (XmlPullParserException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            }


            return result;

        }

        protected void onPostExecute(ArrayList<Site> result) {

            for(Site s : result) {

                _sites.put(s.Name, s);

            }
            AddSitesToMap();

        }



        private ArrayList<Site> parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {

            ArrayList<Site> result = new ArrayList<>();

            Site currentSite = null;

            int eventType = parser.getEventType();

            while( eventType!= XmlPullParser.END_DOCUMENT ) {

                String name;

                switch(eventType)
                {
                    case XmlPullParser.START_TAG:

                        name = parser.getName();

                        if ( name.equals("site")) {
                            currentSite = new Site();
                        }else if(name.equals("siteName"))
                        {
                            currentSite.Name = parser.nextText();
                        }else if(name.equals("latitude"))
                        {
                            currentSite.Latitude = Double.parseDouble(parser.nextText());
                        }else if(name.equals("longitude"))
                        {
                            currentSite.Longitude = Double.parseDouble(parser.nextText());
                        }else if(name.equals("siteCode"))
                        {
                            if(parser.getAttributeValue("","network").equals("ACA_ODM")) {

                                currentSite.Code = parser.nextText();
                            }
                        }

                        break;


                    case XmlPullParser.END_TAG:

                        name = parser.getName();

                        if(name.equals("site")) {
                            result.add(currentSite);
                        }
                        break;
                } // end switch

                eventType = parser.next();
            } // end while

            return result;

        }


    }



    private class GetDepthValuesTask extends AsyncTask<String, Void, ArrayList<SeriesValue>> {


        public GetDepthValuesTask(){

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

                LevelValuesDialogFragment dialog = LevelValuesDialogFragment.newInstance(sdf.format(result.get(0).Date), sdf.format(result.get(1).Date), result.get(0).Value, result.get(1).Value);
                dialog.show(getFragmentManager(), "dialog");

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


    public void updateUI() {

        userPositionMarker.setPosition(new LatLng(mainActivity._latitude, mainActivity._longitude));

    }


}
