package com.schedulix;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.name;
import static com.schedulix.AppController.PREF_NAME;
import static com.schedulix.AppController.userArrayList;

public class ForgetPassword extends AppCompatActivity {


    private static final String TAG = ForgetPassword.class.getSimpleName();
    @BindView(R.id.btn_signin_forget) Button btn_signin;
    @BindView(R.id.progressBarForget) ProgressBar progressBar;
    @BindView(R.id.editTextEmail) EditText emailEdt;
    @BindView(R.id.editTextUsername) EditText usernameEdt;
    @BindView(R.id.textViewBackLogin) TextView backToLogin;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        queue = Volley.newRequestQueue(this);
        initToolbar();

    }

    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.back); // your drawable
//        toolbar.setTitleTextColor(getResources().getColor(R.color.black));
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);
        btn_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isNetworkAvailable())
                {
                    if (!validateUsername()) {
                        return;
                    }

                    if (!validateEmail()) {
                        return;
                    }

                    if(TextUtils.isEmpty(emailEdt.getText().toString().trim()))
                {
                    Toast.makeText(getApplicationContext(),
                            "Email Cannot be Empty" ,
                            Toast.LENGTH_LONG).show();
                }
                else {
                    requestEmail();
                }

            }
            else {
                Toast.makeText(getApplicationContext(),
                        "Check Your Internet Connect",
                        Toast.LENGTH_LONG).show();
            }
            }
        });

        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }

    private void requestEmail() {
        showpDialog();

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_RECOVER_EMAIL,
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
                                // user not login..
                                Toast.makeText(getApplicationContext(),
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                // user successfully login..
                                Toast.makeText(getApplicationContext(),
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            FirebaseCrash.report(new Exception(e.getMessage()));
                        }
                        hidepDialog();

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
                params.put("email", emailEdt.getText().toString().trim());
                return params;
            }
        };
             /*  RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
             */
        strRequest.setShouldCache(false);
        queue.add(strRequest);



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private boolean validateEmail() {
        String email = emailEdt.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailEdt.setError(getString(R.string.err_msg_email));
            requestFocus(emailEdt);
            return false;
        } else {
//            emailLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateUsername() {
        if (usernameEdt.getText().toString().trim().isEmpty()) {
            usernameEdt.setError(getString(R.string.err_msg_name));
            requestFocus(usernameEdt);
            return false;
        } else {
            // username.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidusername(String username) {
        return !TextUtils.isEmpty(username);
    }

    private void showpDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hidepDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
