package com.example.adiad.articulo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class ShowLikes extends AppCompatActivity {

    PrefManager prefManager;
    public static String useit="";
    String enterainment[] = {"Enterainment","TV", "Series","Movie", "Show","Television","Play"};
    String news[] = {"News","Media", "Magazine"};
    String e_comm[] = {"Ecommerce","E-Commerce", "Shopping","Retail", "Brand","Telecom","Clothing","Product"};
    String tech[] = {"Tech","Computer", "App","Internet", "Website","Web","Company"};
    String people[] = {"Community","Social", "Club","Group", "Cultural"};
    String sports[] = {"Sports","Team", "Cricket","Football", "Basketball"};
    String health[] = {"Health","Beauty", "Fitness","Yoga", "Gym","Exercise","Nutrition"};
    String food[] = {"Food","Beverage", "Dish","Drink"};
    String music[] = {"Music","Band", "Lyrics","Artist","Album","Song"};
    String education[] = {"School","University","College","Study"};
    int  centerainment =0;
    int  cnews =0;
    int cecomm = 0;
    int ctech = 0;
    int cpeople = 0;
    int cfood =0;
    int chealth = 0;
    int ceducation = 0;
    int csports = 0;
    int cmusic = 0;
    int cothers= 0;
    ProgressDialog pd;
    AccessToken currentAccessToken;
     public TextView disp;
    Button feed_button;
    Button merabutton;
    RequestQueue queue;
    List<String> likes = new ArrayList<String>();
    public String feed_data="";

    String getnext(JSONObject obj) {
        String next_url ="";
        try {
            if(obj.getJSONObject("paging").has("next"))
            {
                next_url= obj.getJSONObject("paging").getString("next");
            }
            //disp.setText(next_url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return next_url;
    }

    void makeRequest(String url) {
        //disp.setText(url);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    show_likes(response.getJSONArray("data"));
                    //Log.i("Json",response.getJSONArray("data").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String  next=getnext(response);
                if (!next.isEmpty())
                {
                    makeRequest(next);
                }
                else
                {
                    float total = likes.size();
                    float   penterainment = (centerainment/total)*100;
                    float phealth = (chealth/total)*100;
                    float pnews = (cnews/total)*100;
                    float pecomm = (cecomm/total)*100;
                    float psports= (csports/total)*100;
                    float ptech = (ctech/total)*100;
                    float pmusic = (cmusic/total)*100;
                    float pothers =(cothers/total)*100;
                    float ppeople = (cpeople/total)*100;
                    float peducation = (ceducation/total)*100;
                    disp.setText(""+penterainment + ","+pmusic +","+ ppeople +","+ psports+","+pnews+ "," + pecomm+ ","+ peducation+","+phealth + ","+ pothers +"," + ptech);

                    pd.hide();
                }


                //  boolean done = categorise(likes);
               /* if(done)
                {

                }*/

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


            }
        });
        queue.add(jsObjRequest);

    }
    void show_likes(JSONArray objects) throws JSONException {
        //disp.setText(objects.getJSONObject(0).toString());
        for (int i = 0; i < objects.length(); ++i) {
            JSONObject object = null;
            try {
                object = objects.getJSONObject(i);
                likes.add(object.getString("category"));
                categorise(object.getString("category"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_likes);
        Bundle extras = getIntent().getExtras();
        currentAccessToken = (AccessToken) extras.get("currentAccessToken");
        disp = (TextView) findViewById(R.id.disp);



        merabutton=(Button)findViewById(R.id.mera_button);
        feed_button=(Button)findViewById(R.id.feed_button);
        queue = Volley.newRequestQueue(this);

        prefManager=new PrefManager(this);
        pd = new ProgressDialog(this);
        pd.setMessage("loading");
        pd.show();
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        try {
                            show_likes(object.getJSONObject("likes").getJSONArray("data"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String next = null;
                        try {
                            next = getnext(object.getJSONObject("likes"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //disp.setText(next);
                        if(!next.isEmpty()){
                            makeRequest(next);
                        }


                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "likes{category}");
        request.setParameters(parameters);
        request.executeAsync();

        merabutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useit = disp.getText().toString();

// put percentage in shared preferences

                prefManager.setPercentage(useit);


                Toast.makeText(getApplicationContext(),useit,Toast.LENGTH_LONG).show();
                new HttpAsyncTask().execute();
                Toast.makeText(getBaseContext(), "Button pressed", Toast.LENGTH_SHORT).show();
            }
        });

        feed_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ShowLikes.this,MainActivity.class);
                i.putExtra("feed_data", feed_data);
                startActivity(i);
            }
        });
    }
//
//    public void andro_flask(View view)
//    {
//
//    }

    protected boolean categorise(String like)
    {
        int flag=1;
       /* for(int i=0;i<likes.size();i++)
        {*/
        String temp = like;
        for(int j=0 ; j< enterainment.length;j++)
        {
            if(temp.contains(enterainment[j]))
            {
                centerainment++;
                flag=0;
                break;
            }
        }
        for(int j=0 ; j< news.length;j++)
        {
            if(temp.contains(news[j]))
            {
                cnews++;
                flag=0;
                break;
            }
        }
        for(int j=0 ; j< e_comm.length;j++)
        {
            if(temp.contains(e_comm[j]))
            {
                cecomm++;
                flag=0;
                break;
            }
        }
        for(int j=0 ; j< tech.length;j++)
        {
            if(temp.contains(tech[j]))
            {
                ctech++;
                flag=0;
                break;
            }
        }
        for(int j=0 ; j< people.length;j++)
        {
            if(temp.contains(people[j]))
            {
                cpeople++;
                flag=0;
                break;
            }
        }
        for(int j=0 ; j< food.length;j++)
        {
            if(temp.contains(food[j]))
            {
                cfood++;
                flag=0;
                break;
            }

        }
        for(int j=0 ; j< health.length;j++)
        {
            if(temp.contains(health[j]))
            {
                chealth++;
                flag=0;
                break;
            }
        }
        for(int j=0 ; j< sports.length;j++)
        {
            if(temp.contains(sports[j]))
            {
                csports++;
                flag=0;
                break;
            }
        }
        for(int j=0 ; j< education.length;j++)
        {
            if(temp.contains(education[j]))
            {
                ceducation++;
                flag=0;
                break;
            }
        }
        for(int j=0 ; j< music.length;j++)
        {
            if(temp.contains(music[j]))
            {
                cmusic++;
                flag=0;
                break;
            }
        }

        if(flag==1)
        {
            cothers++;
        }

        //  }
        return true;
    }

    public static String POST(){
        InputStream inputStream = null;
        String result = "";
        try {

            Log.e("tag","reached post");

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost("http://192.168.43.58:5000/data");

            String json = "";

            // 3. build jsonObject
//            String movie=movie_name.getText().toString();
            JSONObject jsonObject = new JSONObject();
//            jsonObject.accumulate("movie", movie);


            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
//            for(int i=0;i<messages.size();i++)
//            {
//                messages.set(i,messages.get(i)+"$");
//            }
            //String mystr=disp.getText().toString();
            StringEntity se = new StringEntity(useit);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result

        Log.e("tag", result);
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        String var="";
        @Override
        protected String doInBackground(String... urls) {

            Log.e("http", "reached async");
            //Toast.makeText(getBaseContext(), "reached async", Toast.LENGTH_SHORT).show();
            feed_data= POST();
            return var;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            //make_UI_changes(var);
            Toast.makeText(getBaseContext(), feed_data, Toast.LENGTH_LONG).show();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}