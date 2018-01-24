package com.schedulix;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crash.FirebaseCrash;
import com.schedulix.ModelClasses.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.name;
import static com.schedulix.AppController.PREF_NAME;
import static com.schedulix.AppController.userArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class SendFacebookData extends IntentService {
    RequestQueue queue;
    String username , email,firstName,lastName;

    public static String TAG = SendFacebookData.class.getSimpleName();



    public SendFacebookData()
    {
        super("SendFacebookData");
        Log.d(TAG, "SendFacebookData: ");

    }

    private void makeRequest() {

        Log.d(TAG, "makeRequest: ");
        queue = Volley.newRequestQueue(this);

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_FB_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //   Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, response.toString());

                        try {
                            // Parsing json object response
                            // response will be a json object

                            JSONObject res = new JSONObject(response);

                            JSONObject success = res.getJSONObject("success");
                            int status = success.getInt("status");
                            if(status == 0)
                            {
                                Log.d(TAG, "onResponse: "+success.getString("message"));
                            }
                            else
                            {
                                Log.d(TAG, "onResponse: "+success.getString("message"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrash.report(new Exception(e.getMessage()));
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse == null) {
                            if (error instanceof NoConnectionError) {
                                Toast.makeText(getApplicationContext(), "No internet Access, Check your internet connection", Toast.LENGTH_SHORT).show();
                            }

                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(getApplicationContext(),
                                        "No internet connection.  Please connect to the internet and try again",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("email", email);
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("device_token", getToken());
                return params;
            }
        };
             /*  RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
             */
        strRequest.setShouldCache(false);
        queue.add(strRequest);



    }


    private String getToken() {

        Log.d(TAG, "getToken: ");

        SharedPreferences prefs = getSharedPreferences(AppController.PREF_NAME, MODE_PRIVATE);

        String token = prefs.getString("token", "");

        if (!TextUtils.isEmpty(token)) {
            Log.d(TAG, "getToken "+token);
            return token;
        }
        return token;
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            this.username = intent.getStringExtra("username");
            this.email = intent.getStringExtra("email");
            this.firstName = intent.getStringExtra("first_name");
            this.lastName = intent.getStringExtra("last_name");
            Log.d(TAG, "onHandleIntent: Data From Facebook"+username+"-"+email+"-"+firstName+"-"+lastName);
        }

        makeRequest();

    }


}
