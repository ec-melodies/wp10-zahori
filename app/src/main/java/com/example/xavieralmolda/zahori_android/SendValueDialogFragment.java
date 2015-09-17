package com.example.xavieralmolda.zahori_android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.text.SimpleDateFormat;

/**
 * Created by Xavier Almolda on 31/08/2015.
 */
public class SendValueDialogFragment extends DialogFragment {

    private String siteCode;
    private double latitude;
    private double longitude;

    static SendValueDialogFragment newInstance(String code) {
        SendValueDialogFragment f = new SendValueDialogFragment();
        f.siteCode = code;
        return f;
    }

    static SendValueDialogFragment newInstance(double latitude, double longitude) {
        SendValueDialogFragment f = new SendValueDialogFragment();
        f.siteCode = "Current location";
        f.latitude = latitude;
        f.longitude = longitude;
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

        View v = inflater.inflate(R.layout.fragment_send_value_dialog,container, false);

        final EditText txtValue = (EditText) v.findViewById(R.id.txtNewValue);


        Button btnSend = (Button) v.findViewById(R.id.btnSendValue);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String putBody;
                String urlString;

                if(siteCode == "Current location") {

                    urlString = "http://h2vmservice.cloudapp.net/NewValuePosition?userid=3";
                    Log.d("URL", urlString);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    putBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<LatLonDataValue xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                            "  <Datetime>" + sdf.format(System.currentTimeMillis()) + "</Datetime>\n" +
                            "  <Latitude>" + latitude + "</Latitude>\n" +
                            "  <Longitude>" + longitude + "</Longitude>\n" +
                            "  <OffsetUTC>2</OffsetUTC>\n" +
                            "  <Value>" + txtValue.getText() + "</Value>\n" +
                            "  <VariableCode>q_mitja_d</VariableCode>\n" +
                            "</LatLonDataValue>";

                }
                else {

                    urlString = "http://h2vmservice.cloudapp.net/NewValue?userid=3";
                    Log.d("URL", urlString);

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    putBody = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                            "<SiteDataValue xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                            "  <Datetime>" + sdf.format(System.currentTimeMillis()) + "</Datetime>\n" +
                            "  <OffsetUTC>2</OffsetUTC>\n" +
                            "  <SiteCode>" + siteCode + "</SiteCode>\n" +
                            "  <Value>" + txtValue.getText() + "</Value>\n" +
                            "  <VariableCode>q_mitja_d</VariableCode>\n" +
                            "</SiteDataValue>";

                }

                new SendNewValueTask().execute(putBody, urlString);

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



    private class SendNewValueTask extends AsyncTask<String, Void, Boolean> {


        public SendNewValueTask(){

        }

        @Override
        protected Boolean doInBackground(String... params) {

            // HTTP Put

            HttpResponse response;

            try {

                URI url = new URI(params[1]);

                HttpClient client = new DefaultHttpClient();
                HttpPut put= new HttpPut(url);

                put.setEntity(new StringEntity(params[0]));

                put.addHeader("Content-Type", "text/xml");
                response = client.execute(put);

                Log.d("response", response.getStatusLine().toString());


            } catch (Exception e ) {

                System.out.println(e.getMessage());

                return false;

            }

            if(response.getStatusLine().getStatusCode() != 200) return false;

            return true;

        }


        protected void onPostExecute(Boolean result) {

            String message;

            if(result) {
                message = "Observation added successfully";
            }
            else
            {
                message = "Error adding observed value";
            }

            Toast toast = Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();

            getDialog().dismiss();

        }

    }

}
