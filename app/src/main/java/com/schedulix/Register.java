package com.schedulix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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

public class Register extends AppCompatActivity {


    private static final String TAG =Register.class.getSimpleName() ;
    Button register;
    @BindView(R.id.editTextFirstName) EditText firstname;
    @BindView(R.id.editTextLastName) EditText lastname;
    @BindView(R.id.editTextUsername) EditText username;
    @BindView(R.id.editTextPassword)EditText password;
    @BindView(R.id.editTextEmail) EditText emailEdt;
    @BindView(R.id.editTextMobileNo) EditText mobileNo;
    @BindView(R.id.editTextDescription) EditText description;

    @BindView(R.id.firstNameLayout) TextInputLayout firstNameLayout;
    @BindView(R.id.lastNameLayout) TextInputLayout lastNameLayout;
    @BindView(R.id.passwordLayout) TextInputLayout passwordLayout;
    @BindView(R.id.emailLayout) TextInputLayout emailLayout;
    @BindView(R.id.usernameLayout)TextInputLayout usernameLayout;
    @BindView(R.id.mobileNoLayout) TextInputLayout mobileNoLayout;
    RequestQueue queue;

    @BindView(R.id.progressBar)ProgressBar progressBar;

    String mFirstname,mLastname,mUsername,mPassword,mEmail,mMobileNo,mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_register);
        queue = Volley.newRequestQueue(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initLayout();
    }

    private void initLayout() {
        ButterKnife.bind(this);
        register = (Button) findViewById(R.id.btn_register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitForm();
            }
        });
    }

    private void submitForm() {
        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }

        if (!validateUsername()) {
            return;
        }


        if (!validatePassword()) {
            return;
        }

        if (!validateMobileNo()) {
            return;
        }


        if (!validateEmail()) {
            return;
        }

        if(!isNetworkAvailable())
        {
            Toast.makeText(getApplicationContext(),
                    "Check Your Internet Connect",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mFirstname = firstname.getText().toString().trim();
        mLastname = lastname.getText().toString().trim();
        mUsername = username.getText().toString().trim();
        mPassword = password.getText().toString().trim();
        mEmail = emailEdt.getText().toString().trim();
        mMobileNo = mobileNo.getText().toString().trim();
        if(!TextUtils.isEmpty(description.getText().toString().trim()))
        {
            mDescription = description.getText().toString().trim();
        }
        else
        {
            mDescription ="";
        }



        makeRequest();

   //     Toast.makeText(getApplicationContext(), "Thank You!", Toast.LENGTH_SHORT).show();
    }



    private void makeRequest() {

        showpDialog();

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_REGISTER,
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

                                JSONObject data = success.getJSONObject("data");

                                // user successfully login..
                                Toast.makeText(getApplicationContext(),
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }






                            // txtResponse.setText(jsonResponse);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrash.report(new Exception(e.getMessage()));
                            Toast.makeText(getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
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

                Log.d(TAG, "get Params Data is "+mFirstname+"\n"+mLastname+"\n"+
                        mUsername+"\n"+mPassword+"\n"+mEmail+"\n"+mMobileNo);

                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name", mFirstname);
                params.put("last_name", mLastname);
                params.put("email",mEmail);
                params.put("mobile_no",mMobileNo);
                params.put("username",mUsername);
                params.put("password",mPassword);
                /// country and professions List are provided by api team ////
                params.put("profession_id",String.valueOf(1));
                params.put("country_id",String.valueOf(1));
                params.put("device_token",getToken());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    private boolean validateEmail() {
        String email = emailEdt.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailLayout.setError(getString(R.string.err_msg_email));
            requestFocus(emailEdt);
            return false;
        } else {
            emailLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    private boolean validateFirstName() {
        if (firstname.getText().toString().trim().isEmpty()) {
            firstNameLayout.setError(getString(R.string.err_msg_name));
            requestFocus(firstname);
            return false;
        } else {
            firstNameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLastName() {
        if (lastname.getText().toString().trim().isEmpty()) {
            lastNameLayout.setError(getString(R.string.err_msg_name));
            requestFocus(lastname);
            return false;
        } else {
            lastNameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateUsername() {
        if (username.getText().toString().trim().isEmpty()) {
            usernameLayout.setError(getString(R.string.err_msg_name));
            requestFocus(username);
            return false;
        } else {
            usernameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            passwordLayout.setError(getString(R.string.err_msg_name));
            requestFocus(password);
            return false;
        } else {
            passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobileNo() {
        if (mobileNo.getText().toString().trim().isEmpty()) {
            mobileNoLayout.setError(getString(R.string.err_msg_name));
            requestFocus(mobileNo);
            return false;
        } else {
            mobileNoLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void showpDialog() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hidepDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }



}
