package com.example.adiad.articulo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FacebookLogin extends AppCompatActivity {
    private GoogleApiClient client;
    CallbackManager callbackManager;
    TextView status ;
    Button likes;
    Button posts;
    LoginButton loginButton;
    private ProfilePictureView profilePictureView;
    AccessTokenTracker access_tracker;
    ProfileTracker profile_tracker;
    Profile currentProfile;
    AccessToken currentAccessToken;
    Button pics,stats;
    RequestQueue queue;
    List<String> imageids;
    List<String> imageurls;
    ProgressDialog pd;


    FacebookCallback<LoginResult> callback= new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            currentAccessToken = loginResult.getAccessToken();
            currentProfile= Profile.getCurrentProfile();
            displayinfo(currentProfile);
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException error) {

        }
    };
    protected  void displayinfo(Profile profile ){
        if(profile!=null)
        {

            profilePictureView.setProfileId(profile.getId());
            likes.setEnabled(true);

        }else
        {
            profilePictureView.setProfileId(null);

            likes.setEnabled(false);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook_login);


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        List<String> permissions = new ArrayList<String>();
        permissions.add("user_likes");
        permissions.add("user_posts");
        permissions.add("user_birthday");
        permissions.add("user_photos");
        loginButton.setReadPermissions(permissions);
        loginButton.registerCallback(callbackManager, callback);

        likes = (Button)findViewById(R.id.likes);


        stats=(Button)findViewById(R.id.stats);

        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(FacebookLogin.this,Statistics.class);
                startActivity(i);
            }
        });
        profilePictureView = (ProfilePictureView) findViewById(R.id.user_pic);
        displayinfo(Profile.getCurrentProfile());
        imageids  = new ArrayList<String>();
        imageurls = new ArrayList<String>();
        profilePictureView.setCropped(true);
        queue = Volley.newRequestQueue(this);
        pd = new ProgressDialog(this);
        pd.setMessage("loading");


        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(FacebookLogin.this,ShowLikes.class);
                i1.putExtra("currentAccessToken",AccessToken.getCurrentAccessToken());
                startActivity(i1);
            }
        });
        access_tracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken newtAccessToken) {
                currentAccessToken=newtAccessToken;
            }
        };
        profile_tracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                currentProfile=newProfile;
                displayinfo(currentProfile);
            }
        };
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        access_tracker.stopTracking();
        profile_tracker.stopTracking();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    protected void getpics(AccessToken cat)
    {

        GraphRequest request = GraphRequest.newGraphPathRequest(
                cat,
                "/me/photos",
                new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        JSONObject tempo= null;
                        try {
                            tempo = new JSONObject(response.getRawResponse());
                            getids(tempo.getJSONArray("data"));
                            String next = getnext(tempo);
                            // Log.d("Json","next 1: "+ next);

                            if(!next.isEmpty())
                                makeRequests(next);
                            // Log.d("Json","fin fn");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id");
        request.setParameters(parameters);
        request.executeAsync();
    }

    protected void makeRequests(String url) {
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    getids(response.getJSONArray("data"));
                    //Log.i("Json",response.getJSONArray("data").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String  next= getnext(response);
                // Log.i("Json","next : "+ next);
                if (!next.isEmpty())
                {
                    makeRequests(next);
                }
                else
                {
                    Log.d("Json"," ids :" +imageids.toString()+"\n");
                    // getlinks(AccessToken.getCurrentAccessToken());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(jsObjRequest);
    }

    protected String getnext(JSONObject obj)
    {
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

    protected void getids(JSONArray data) throws JSONException {
        JSONObject temp;
        for(int i=0;i<data.length();i++) {
            temp = data.getJSONObject(i);
            imageids.add(temp.getString("id"));
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    AccessToken.getCurrentAccessToken(),
                    "/"+imageids.get(i)+"/picture",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            JSONObject obj= null;
                            try {
                                obj = new JSONObject(response.getRawResponse());
                                // Log.d("Json","URLS : "+response.toString()+"\n");
                                imageurls.add(obj.getJSONObject("data").getString("url"));
                                //Log.d("Json","URLS runtime : "+obj.getJSONObject("data").getString("url")+"\n");
                                if(imageids.size() == imageurls.size())
                                {
                                    Log.d("Json","URLS after : "+imageurls.toString()+"\n");
                                    pd.hide();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("type", "normal");
            parameters.putBoolean("redirect", false);
            request.setParameters(parameters);
            request.executeAsync();
        }


    }

    protected void getlinks(AccessToken cat)
    {
        for(int i=0;i<imageids.size();i++)
        {
            GraphRequest request = GraphRequest.newGraphPathRequest(
                    cat,
                    "/"+imageids.get(i)+"/picture",
                    new GraphRequest.Callback() {
                        @Override
                        public void onCompleted(GraphResponse response) {
                            JSONObject obj= null;
                            try {
                                obj = new JSONObject(response.getRawResponse());
                                // Log.d("Json","URLS : "+response.toString()+"\n");
                                imageurls.add(obj.getJSONObject("data").getString("url"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("type", "normal");
            parameters.putBoolean("redirect", false);
            request.setParameters(parameters);
            request.executeAsync();
        }
        Log.d("Json", "URLS after : " + imageurls.toString() + "\n");
    }

}



