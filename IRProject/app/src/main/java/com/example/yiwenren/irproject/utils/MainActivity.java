package com.example.yiwenren.irproject.utils;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiwenren.irproject.R;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Spinner loc_spinner;
    ImageButton searchBarBtn;
    EditText editText;
    String query = "";
    String locationString = "";
    boolean locationClick = false;

    private static Gson gson = new Gson();
    String resultpost;

    public final static String IP = "http://150.212.67.243:8080/";
    public final static String IPTop10 = "2140final/ProcessQueryRetrieveTopTen";
    public final static String IPById = "2140final/ProcessRetrieveDocDetailByID";

//    public final static String IP = "http://150.212.84.85:8080/";
//    public final static String IPTop10 = "DynmacinIR/ProcessQueryRetrieveTopTen";
//    public final static String IPById = "DynmacinIR/ProcessRetrieveDocDetailByID";


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSpinner(inputLocData());
        setupSearchButton();

    }

    //Image Button
    private void setupSearchButton() {
        searchBarBtn = (ImageButton)findViewById(R.id.search_bar_btn);
        searchBarBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MainActivity.this, EditSearchResultActivity.class);
//                String textSent = postRequest();
//                intent.putExtra("act1_json", textSent);
//                startActivity(intent);
                sendData();
            }
        });
    }

    //setup location spinner
    private void setupSpinner(String[] loc_data_arr){
        //initialize EditText
        editText = (EditText)findViewById(R.id.search_bar_text1);
        //location spinner
        loc_spinner = (Spinner) findViewById(R.id.location_spinner);
        List<String> loc_data = new ArrayList<>();

        for (int i = 0; i < loc_data_arr.length; i++) {
            loc_data.add(loc_data_arr[i]);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loc_data);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loc_spinner.setAdapter(dataAdapter);

        loc_spinner.setSelection(0, false);
        loc_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                locationClick = true;
                Toast.makeText(MainActivity.this, loc_spinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    //post
    private void sendData(){
        System.out.print("sent data  starts !!");

        query = ((EditText) findViewById(R.id.search_bar_text1)).getText().toString().trim();
        locationString = loc_spinner.getSelectedItem().toString().trim();
        System.out.println(query+locationString);
        if((query == null || query.length() < 1) && !locationClick){
            Toast.makeText(MainActivity.this, "Please enter your key word", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else {
            new Thread() {
                public void run(){
                   // String result = postRequest(IP + "DynmacinIR/ProcessQueryRetrieveTopTen", query, locationString);
                    String result = postRequest(IP + IPTop10, query, locationString);
                    //System.out.println(result);
                    //resultpost = result;

                    System.out.println("result is: " + result);

                    Intent intent = new Intent();

                    intent.putExtra("act1_json", resultpost);
                    intent.putExtra("query", query);
                    intent.putExtra("locationIndex", loc_spinner.getSelectedItemPosition());
                    intent.setClass(MainActivity.this, EditSearchResultActivity.class);
                    startActivity(intent);
                }
            }.start();
        }
    }

    public String postRequest(String urlString, String query, String locationString){
        resultpost="";
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);

            connection.setInstanceFollowRedirects(true);
            connection.setReadTimeout(20000);
            connection.setConnectTimeout(20000);
            connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            connection.connect();

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            String data = "query=" + URLEncoder.encode(query, "UTF-8") + "&location=" + URLEncoder.encode(locationString, "UTF-8");

            os.writeBytes(data);
            os.flush();
            os.close();

            // input stream
            InputStream is = connection.getInputStream();
            // output stream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len = 0;

            byte buffer[] = new byte[1024];

            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            // release resource
            is.close();
            baos.close();

            resultpost = new String(baos.toByteArray());
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("HTTP post failed！");
            resultpost = "http post error";
        }
        return resultpost;
    }



    private String[] inputLocData() {
       // Gson gson = new Gson();

        String text = "";
        String[] error = new String[0];


        try {
            InputStream is = getAssets().open("locationsum.txt");
            int size = is.available();
            byte[] buffer  = new byte[size];
            is.read(buffer);
            text = new String(buffer);

            String[] loc_data_arr = text.split("\n");

            System.out.println("arr 1" + loc_data_arr[0] + " arr 10" + loc_data_arr[11]);
            return loc_data_arr;

            //Toast.makeText(MainActivity.this, "success! good start!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return error;
    }

}
