package com.example.yiwenren.irproject.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yiwenren.irproject.R;
import com.example.yiwenren.irproject.models.DetailedResult;
import com.example.yiwenren.irproject.models.SearchResult;
import com.example.yiwenren.irproject.models.SearchResultGeneral;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yiwenren on 12/1/16.
 */

public class EditDetailedResult extends AppCompatActivity {
    private static Gson gson = new Gson();
    Button recommandedJob;
    String ID;
    String jsonString;


    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_result);

        //add back button in the UI
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(receiveJsonData()){
            setupButton();
            setupUI(convertJson());
        } else {

        }


        //setupUI(receiveJsonData());

    }

    //setup UI
    private void setupUI(DetailedResult data){
        TextView jobtitle = (TextView) findViewById(R.id.title3);
        jobtitle.setText(data.getTitle());
        ((TextView) findViewById(R.id.organization)).setText("Organization: " + data.getOrganizationName());
        ((TextView) findViewById(R.id.location3)).setText("Location: " + data.getLocation());
        ((TextView) findViewById(R.id.postTime)).setText("post on " + data.getDatePosted());
        ((TextView) findViewById(R.id.jobDescription)).setText(data.getJobDescription());
        TextView url = (TextView) findViewById(R.id.url);
        url.setAutoLinkMask(Linkify.ALL);
        url.setText(data.getURL());

    }


    //add "back button" function to this activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Image Button
    private void setupButton() {
        recommandedJob = (Button)findViewById(R.id.recommendJobButon);
        recommandedJob.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                sendListViewItemData();
            }
        });
    }

    private DetailedResult mockDetailedResult(){
        String jobDes = "Drive advertising revenue with aggressive sales efforts that penetrate new markets and increase existing market presence\\n" + "Consult with customers to be able to properly articulate the value proposition of a product package that exceeds customer goals and aligns with the business direction.\\n"+ "Build customer relationships on an ongoing basis";
        String responsibility = "College degree preferred" + "2-5 years experience in print and/or online advertising sales and be able to show consistent sales results in previous positions";
        String experience = responsibility;
        String skills = responsibility;
        DetailedResult detailedResult = new DetailedResult("Media Consultant", "2016-03-17", "http://my.jobs/ac1ebffe73894baf8571398bbcba1a951839","location1","1001", "1105 Media, Inc.", jobDes);

//debug
String jsonString = gson.toJson(detailedResult);
System.out.println("mock data for detailed result : " + jsonString);
DetailedResult detailedResult2 = gson.fromJson(jsonString, new TypeToken<DetailedResult>(){}.getType());


        return detailedResult;

    }

    private boolean receiveJsonData(){

        String resultJsonFromListview = getIntent().getStringExtra("ListviewItem");
        String resultJsonFromRecommand = getIntent().getStringExtra("RecommandedJobDetail");

        if(resultJsonFromListview == null){
            jsonString = resultJsonFromRecommand;
        } else {
            jsonString = resultJsonFromListview;
        }
        if(jsonString.trim().equals("no result")){
            return false;
        } else {
            return true;
        }

    }

    private DetailedResult convertJson(){
        DetailedResult srg = gson.fromJson(jsonString, new TypeToken<DetailedResult>(){}.getType());
        ID = srg.getID();
        return srg;
    }

    //debug
    private String inputJson() {
        // Gson gson = new Gson();

        String text = "";
        try {
            InputStream is = getAssets().open("test1206.json");
            int size = is.available();
            byte[] buffer  = new byte[size];
            is.read(buffer);
            text = new String(buffer);
            System.out.println("recommand button " + text);
            //Toast.makeText(MainActivity.this, "success! good start!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return text;


    }

    //post query: query + location + ID
    private void sendListViewItemData(){

        new Thread() {
            public void run(){

                String getString = getIntent().getStringExtra("dataFromActivity2_query_location");
                System.out.println("getString : " + getString);
                String getStringArr[] = getString.split("&");
                String query = getStringArr[0];
                String location = getStringArr[1];
                ID = getIntent().getStringExtra("ID");

                System.out.println("editDetail ID : " + ID);

                //get return Json value
                String resultJson = postRequest(MainActivity.IP + MainActivity.IPById, query, location, ID);
                System.out.println(resultJson);
                Intent intent = new Intent(EditDetailedResult.this, RecommendedJob.class);
                intent.putExtra("RecommandListviewItem", resultJson);
                startActivity(intent);
            }
        }.start();


//        if((query == null || query.length() < 1) && !locationClick){
//            Intent intent = new Intent(EditSearchResultActivity.this, EditSearchResultActivity.class);
//            startActivity(intent);
//        } else {
//
//        }
    }

    private String postRequest(String urlString, String query, String locationString, String ID){
        String resultpost="";
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
            //String data = "query = " + URLEncoder.encode(query, "UTF-8") + "&location = " + URLEncoder.encode(locationString, "UTF-8") +"&feedbackjobid = " + URLEncoder.encode(ID, "UTF-8");
            String data = "query=" + URLEncoder.encode(query, "UTF-8") + "&location=" + URLEncoder.encode(locationString, "UTF-8") + "&feedbackjobid=" + URLEncoder.encode(ID, "UTF-8");


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

    //add shortcut to go to home page
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
