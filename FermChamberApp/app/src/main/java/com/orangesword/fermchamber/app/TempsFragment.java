package com.orangesword.fermchamber.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

/**
 * Created by benjamin.bartels on 5/27/15.
 */
public class TempsFragment extends Fragment {

    TextView tvTemp1;
    TextView tvTemp2;
    TextView tvTemp3;

    private Handler handler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_temps, container, false);


        tvTemp1 = (TextView)(rootView.findViewById(R.id.tvTemp1));
        tvTemp2 = (TextView)(rootView.findViewById(R.id.tvTemp2));

        return rootView;
    }

    @Override
    public void onResume () {
        super.onResume();
        Log.d("Ben", "Temps onResume called");
        handler.postDelayed(runnable,0);
    }

    public void onPause () {
        super.onPause();
        Log.d("Ben", "Temps onPause called");
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable(){
        public void run() {
            new HttpAsyncTask().execute("http://orangesword.duckdns.org:3000/temperatures");
        }
    };


    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null){

                result = convertInputStreamToString(inputStream);
            }
            else {

                result = "Did not work!";
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();
            try {
                JSONArray items = new JSONArray(result);

                for (int i = 0; i < 2; i++) {
                    JSONObject c = items.getJSONObject(i);
                    String sensor = c.getString("sensor");

                    Log.d("Ben", "TEST raw: " + c.getDouble("value"));

                    DecimalFormat df = new DecimalFormat("###.##");
                    double temp =(9.0/5.0) * c.getDouble("value") + 32;


                    Log.d("Ben", "TEST: " + sensor + " - " + temp);

                    if (sensor.equals("Fermenter")) {
                        tvTemp1.setText(df.format(temp) + (char) 0x00B0);

                    } else if (sensor.equals("Ice")) {
                        tvTemp2.setText(df.format(temp) + (char) 0x00B0);

                    } else if (sensor.equals("Ambient")) {
                        tvTemp3.setText(df.format(temp) + (char) 0x00B0);

                    }

                }

                handler.postDelayed(runnable,10000);

            } catch (Exception e) {
                Log.d("Ben", "!Exception: " + e.getLocalizedMessage());
            }
        }
    }
}
