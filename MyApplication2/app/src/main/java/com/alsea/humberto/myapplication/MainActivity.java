package com.alsea.humberto.myapplication;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.LinkedList;


public class MainActivity extends ListActivity {

    private LinkedList<String> mListItems;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new GetDataTask().execute("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");

        // Set a listener to be invoked when the list should be refreshed.
        ((PullToRefresh) getListView()).setOnRefreshListener(new PullToRefresh.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GetDataTask().execute("http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_hour.geojson");
            }
        });

    }

    private class GetDataTask extends AsyncTask<String, Void, String> {

        ArrayList<String> arrayvalores = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            return readJSONFeed(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            Log.i("result","result = "+result);


            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray postalCodesItems = new
                        JSONArray(jsonObject.getString("features"));



                Log.i("postalCodesItems","postalCodesItems = "+postalCodesItems);

                //---print out the content of the json feed---
                for (int i = 0; i < postalCodesItems.length(); i++) {
                    JSONObject postalCodesItem =
                            postalCodesItems.getJSONObject(i);
                    Log.i("properties","properties = "+postalCodesItem.getString("properties"));

                        arrayvalores.add(postalCodesItem.getString("properties"));

                    Toast.makeText(getBaseContext(),
                            postalCodesItem.getString("properties"),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.d("ReadPlacesFeedTask", e.getLocalizedMessage());
            }


            adapter = new Adapter(arrayvalores);
            setListAdapter(adapter);

        }
    }


    public class Adapter extends BaseAdapter{

        ArrayList<String> Title;

        public Adapter(ArrayList<String> text) {
            Title = text;
        }

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row;
            row = inflater.inflate(R.layout.activity_main, parent, false);
            //TextView title;
            //ImageView i1;
            //title = (TextView) row.findViewById(R.id.title);
            //i1=(ImageView)row.findViewById(R.id.img);
            //title.setText(Title[position]);
            //i1.setImageResource(imge[position]);

            return (row);
        }


    }


    public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("JSON", "Failed to download file");
            }
        } catch (Exception e) {
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return stringBuilder.toString();
    }


    private String[] mStrings = {
            "Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam",
            "Abondance", "Ackawi", "Acorn", "Adelost", "Affidelice au Chablis",
            "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"
    };

}
