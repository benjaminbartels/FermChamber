package com.orangesword.fermchamber.app;

/**
 * Created by benjamin.bartels on 5/27/15.
 */

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GraphFragment extends Fragment {

    LinearLayout graph1;

    private Handler handler = new Handler();
    private GraphView graphView;
    private LineGraphSeries<DataPoint> series1;
    private LineGraphSeries<DataPoint> series2;
    private DateTimeFormatter formatter = DateTimeFormat.forPattern("k:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_graph, container, false);

        graph1 = (LinearLayout) (rootView.findViewById(R.id.graph1));


        graphView = new GraphView(getActivity());

        graphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // transform number to time
                    DateTime dt = new DateTime((long) value);

                    return formatter.print(dt);
                } else {
                    return super.formatLabel(value, false);
                }
            }
        });
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
        //graphView.getGridLabelRenderer().setGridColor(Color.parseColor("#5588ee"));
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(40);
        graphView.getViewport().setMaxY(90);
        graphView.getGridLabelRenderer().setNumVerticalLabels(11);
        // graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.parseColor("#5588ee"));
        // graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.parseColor("#5588ee"));
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphView.getLegendRenderer().setWidth(200);

        graph1.addView(graphView);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Ben", "Graph onResume called");
        handler.postDelayed(runnable, 0);
    }

    public void onPause() {
        super.onPause();
        Log.d("Ben", "Graph onPause called");
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {
        public void run() {
            new HttpAsyncTask().execute("http://orangesword.duckdns.org:3000/temperatures");
        }
    };


    public static String GET(String url) {
        InputStream inputStream;
        String result = "";
        try {
            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null) {

                result = convertInputStreamToString(inputStream);
            } else {

                result = "Did not work!";
            }

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
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

            try {

                long xMax = 0;
                long xMin = 0;

                JSONArray items = new JSONArray(result);

                DateTimeFormatter parser2 = ISODateTimeFormat.dateTime();

                ArrayList<JSONObject> temp1Objs = new ArrayList<JSONObject>();
                ArrayList<JSONObject> temp2Objs = new ArrayList<JSONObject>();

                for (int i = 0; i < items.length(); i++) {
                    JSONObject c = items.getJSONObject(i);

                    if (c.getString("sensor").equals("Fermenter"))
                        temp1Objs.add(c);
                    else if (c.getString("sensor").equals("Ice"))
                        temp2Objs.add(c);
                }

                ArrayList<DataPoint> temp1list = new ArrayList<DataPoint>();
                ArrayList<DataPoint> temp2list = new ArrayList<DataPoint>();

                for (JSONObject item : temp1Objs) {
                    DateTime dt = parser2.parseDateTime(item.getString("createdOn"));
                    temp1list.add(new DataPoint(dt.getMillis(), ((item.getDouble("value")) * 9 / 5) + 32));

                    if (dt.getMillis() > xMax || xMax == 0)
                        xMax = dt.getMillis();

                    if (dt.getMillis() < xMin || xMin == 0)
                        xMin = dt.getMillis();

                }

                for (JSONObject item : temp2Objs) {
                    DateTime dt = parser2.parseDateTime(item.getString("createdOn"));
                    temp2list.add(new DataPoint(dt.getMillis(), ((item.getDouble("value")) * 9 / 5) + 32));

                    if (dt.getMillis() > xMax || xMax == 0)
                        xMax = dt.getMillis();

                    if (dt.getMillis() < xMin || xMin == 0)
                        xMin = dt.getMillis();
                }

                Comparator<DataPoint> comparator = new Comparator<DataPoint>() {
                    public int compare(DataPoint a, DataPoint b) {
                        if (a.getX() > b.getX())
                            return 1;
                        else
                            return -1;
                    }
                };

                Collections.sort(temp1list, comparator);
                Collections.sort(temp2list, comparator);

                DataPoint[] graphData1 = temp1list.toArray(new DataPoint[temp1list.size()]);
                DataPoint[] graphData2 = temp2list.toArray(new DataPoint[temp2list.size()]);

                if (series1 == null) {
                    series1 = new LineGraphSeries(graphData1);
                    series1.setTitle("Ferm");
                    series1.setColor(Color.BLUE);
                    series1.setThickness(3);
                    graphView.addSeries(series1);
                } else {
                    series1.resetData(graphData1);
                }

                if (series2 == null) {
                    series2 = new LineGraphSeries(graphData2);
                    series2.setTitle("Ice");
                    series2.setColor(Color.GREEN);
                    series2.setThickness(3);
                    graphView.addSeries(series2);
                } else {
                    series2.resetData(graphData2);
                }

                graphView.getViewport().setMinX(xMin);
                graphView.getViewport().setMaxX(xMax);
                graphView.getGridLabelRenderer().reloadStyles();
                graphView.onDataChanged(false, false);


                handler.postDelayed(runnable, 10000);


            } catch (Exception e) {
                Log.e("Exception", e.getLocalizedMessage());
            }
        }
    }
}