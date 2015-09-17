package com.example.xavieralmolda.zahori_android;


import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.shinobicontrols.charts.ChartFragment;
import com.shinobicontrols.charts.DataPoint;
import com.shinobicontrols.charts.DateTimeAxis;
import com.shinobicontrols.charts.LineSeries;
import com.shinobicontrols.charts.LineSeriesStyle;
import com.shinobicontrols.charts.NumberAxis;
import com.shinobicontrols.charts.NumberRange;
import com.shinobicontrols.charts.PointStyle;
import com.shinobicontrols.charts.SeriesStyle;
import com.shinobicontrols.charts.ShinobiChart;
import com.shinobicontrols.charts.SimpleDataAdapter;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.FieldPosition;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class XYPlotActivity extends ActionBarActivity {

    private ShinobiChart shinobiChart;

    private ArrayList<Series> _series = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xyplot);

        if (savedInstanceState == null) {

            ChartFragment chartFragment = (ChartFragment) getFragmentManager().findFragmentById(R.id.chart);

            shinobiChart = chartFragment.getShinobiChart();
            shinobiChart.setLicenseKey("JrfEUR236erlJiOMjAxNTA0MjR4YXZpLmFsbW9sZGFAZ21haWwuY29tFheDScsETAViOlFqbX5XPKhpEpoH8YOvacXZ/jGlkpvxxLmP/AHiMNYXctDBR2dFEu88jNwyq4wIs3ZsNGH+e6+1kA7GqZz+2MkI7GCIThqsDgUPvM2FL2NdSGEGkM83nl6JBR6tm0o3tLcNsWXw79HGvrFw=BQxSUisl3BaWf/7myRmmlIjRnMU2cA7q+/03ZX9wdj30RzapYANf51ee3Pi8m2rVW6aD7t6Hi4Qy5vv9xpaQYXF5T7XzsafhzS3hbBokp36BoJZg8IrceBj742nQajYyV7trx5GIw9jy/V6r0bvctKYwTim7Kzq+YPWGMtqtQoU=PFJTQUtleVZhbHVlPjxNb2R1bHVzPnh6YlRrc2dYWWJvQUh5VGR6dkNzQXUrUVAxQnM5b2VrZUxxZVdacnRFbUx3OHZlWStBK3pteXg4NGpJbFkzT2hGdlNYbHZDSjlKVGZQTTF4S2ZweWZBVXBGeXgxRnVBMThOcDNETUxXR1JJbTJ6WXA3a1YyMEdYZGU3RnJyTHZjdGhIbW1BZ21PTTdwMFBsNWlSKzNVMDg5M1N4b2hCZlJ5RHdEeE9vdDNlMD08L01vZHVsdXM+PEV4cG9uZW50PkFRQUI8L0V4cG9uZW50PjwvUlNBS2V5VmFsdWU+");
            shinobiChart.setTitle("Water Level Values");

            DateTimeAxis xAxis = new DateTimeAxis();
            xAxis.setLabelFormat(new DateFormat() {

                private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                @Override
                public StringBuffer format(Date date, StringBuffer buffer, FieldPosition field) {
                    return dateFormat.format(date, buffer, field);
                }

                @Override
                public Date parse(String string, ParsePosition position) {
                    return null;
                }
            });
            shinobiChart.setXAxis(xAxis);

            NumberAxis yAxis = new NumberAxis();
            shinobiChart.setYAxis(yAxis);

            yAxis.enableGesturePanning(true);
            yAxis.enableGestureZooming(true);

            xAxis.enableGesturePanning(true);
            xAxis.enableGestureZooming(true);

        }


        Bundle b = getIntent().getExtras();
        String strCode = b.getString("code");
        new GetWMLFromODMTask().execute(strCode);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_xyplot, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseXML( XmlPullParser parser ) throws XmlPullParserException, IOException {

        Series currentSeries = null;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            int eventType = parser.getEventType();

            while (eventType != XmlPullParser.END_DOCUMENT) {

                String name;

                switch (eventType) {
                    case XmlPullParser.START_TAG:

                        name = parser.getName();

                        if (name.equals("timeSeries")) {
                            currentSeries = new Series();
                        } else if (name.equals("variableCode")) {
                            currentSeries.Code = parser.nextText();
                        } else if (name.equals("value")) {

                            String strDate = parser.getAttributeValue("", "dateTime");
                            Date oDate = sdf.parse(strDate);
                            currentSeries.X.add(oDate.getTime());
                            currentSeries.Y.add(Double.parseDouble(parser.nextText()));

                        }

                        break;


                    case XmlPullParser.END_TAG:

                        name = parser.getName();

                        if (name.equals("timeSeries")) {
                            _series.add(currentSeries);
                        }
                        break;
                } // end switch

                eventType = parser.next();
            } // end while
        }
        catch(Exception ex) {

            Log.d("parse", "parse xml error");

        }

    }

    private class GetWMLFromODMTask extends AsyncTask<String, Void, Void> {


        public GetWMLFromODMTask(){

        }

        @Override
        protected Void doInBackground(String... params) {

            String urlString="http://h2vmservice.cloudapp.net/GetWMLFromODM?sitecodes=" + params[0] + "&variablecodes=z_mitja_d,z_mitja_d_sim&sourceid=2,4&clientid=aca"; // URL to call

            InputStream in = null;

            // HTTP Get
            try {

                URL url = new URL(urlString);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                in = new BufferedInputStream(urlConnection.getInputStream());

            } catch (Exception e ) {

                System.out.println(e.getMessage());

            }


            try {
                XmlPullParserFactory pullParserFactory;

                pullParserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = pullParserFactory.newPullParser();

                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);

                parseXML(parser);


            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();

            }


            return null;

        }

        protected void onPostExecute(Void v) {

            for(Series s : _series) {

                SimpleDataAdapter<Date, Double> dataAdapter = new SimpleDataAdapter<>();

                for (int i = 0; i < s.X.size(); i++) {
                    dataAdapter.add(new DataPoint<>(new Date(s.X.get(i).longValue()), s.Y.get(i).doubleValue()));
                }

                LineSeries series = new LineSeries();
                series.setDataAdapter(dataAdapter);
                shinobiChart.addSeries(series);

                if (s.Code.equals("z_mitja_d_sim")) {
                    LineSeriesStyle style = series.getStyle();
                    style.setFillStyle(SeriesStyle.FillStyle.NONE);
                    style.setLineColor(Color.argb(255, 0, 0, 255));

                }
                else{
                    LineSeriesStyle style = series.getStyle();
                    style.setFillStyle(SeriesStyle.FillStyle.NONE);
                    style.setLineShown(false);
                    style.getPointStyle().setPointsShown(true);
                    style.getPointStyle().setColor(Color.argb(255, 255, 0, 0));
                    style.getPointStyle().setInnerColor(Color.argb(255, 255, 0, 0));
                    style.getPointStyle().setRadius(0.5f);
                    style.setLineColor(Color.argb(255, 255, 0, 0));

                }



            }

            shinobiChart.redrawChart();

        }

    }

}
