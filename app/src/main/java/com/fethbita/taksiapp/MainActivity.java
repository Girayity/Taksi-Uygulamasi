package com.fethbita.taksiapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.duraklar);
    }

    public void buttonOnClick(View view) {
        AsyncWebCall asyncWebCall = new AsyncWebCall(this);
        asyncWebCall.execute();
    }

    private class AsyncWebCall extends AsyncTask<String, List<Object>, List<Object>> {
        //https://stackoverflow.com/a/9118319
        Context context;
        private AsyncWebCall(Context context) {
            this.context = context.getApplicationContext();
        }

        private List<Object> callApi() {
            try {
                OkHttpClient client = new OkHttpClient();
                String url = "http://bil4118.somee.com/api/cabstand";

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                Response response = client.newCall(request).execute();
                List<Object> duraklar = new ArrayList<>();
                if (response.code() != 200) {
                    return null;
                } else {
                    String answer = response.body().string();
                    JSONArray array = new JSONArray(answer);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        duraklar.add(object.getString("Name"));
                        duraklar.add(object.getString("Phone"));
                        duraklar.add(object.getDouble("Latitude"));
                        duraklar.add(object.getDouble("Longitude"));
                    }
                    return duraklar;
                }
            } catch (Exception e) {
                Log.e("test", "fetchUrl:error" + e.toString());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Object> doInBackground(String... params) {
            return callApi();
        }

        @Override
        protected void onPostExecute(final List<Object> s) {
            super.onPostExecute(s);
            if(s == null){
                return;
            }
            ArrayList<String> listItems = new ArrayList<String>();
            ArrayAdapter<String> adapter;
            adapter = new ArrayAdapter<String>(context,
                    android.R.layout.simple_list_item_1,
                    listItems);
            listView.setAdapter(adapter);
            for (int i = 0 ;  i < s.size() ; i++){
                if(i % 4 == 0){
                    listItems.add(s.get(i) + "   ---   " + s.get(i + 1));
                }
            }
            //https://stackoverflow.com/a/9097790
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Object o = listView.getItemAtPosition(position);
                    Intent intent = new Intent(context , MapsActivity.class);
                    intent.putExtra("name", (String) s.get(position * 4));
                    intent.putExtra("lat", (double)s.get(position * 4 + 2));
                    intent.putExtra("lon", (double)s.get(position * 4 + 3));
                    startActivity(intent);
                }
            });
        }
    }
}