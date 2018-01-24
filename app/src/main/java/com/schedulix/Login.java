package com.schedulix;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.facebook.CallbackManager;
import com.google.firebase.crash.FirebaseCrash;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.schedulix.ModelClasses.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.attr.factor;
import static android.R.attr.name;
import static com.schedulix.AppController.PREF_NAME;
import static com.schedulix.AppController.userArrayList;

public class Login extends AppCompatActivity {



    ///// Butter Knife Varaible Initialization..////
    @BindView(R.id.textViewForgot) TextView forgetpassword;
    @BindView(R.id.btn_signin) Button signin;
    @BindView(R.id.editTextUsername) EditText username;
    @BindView(R.id.editTextPassword) EditText password;
    @BindView(R.id.progressBar)ProgressBar progressBar;
 //   @BindView(R.id.login_button)LoginButton loginButton;
    @BindView(R.id.rememberMe)CheckBox remember;
 //   @BindView(R.id.fb)Button fb;
//    @BindView(R.id.usernameInput) TextInputLayout usernameLayout;
//    @BindView(R.id.passwordInput) TextInputLayout passwordLayout;
    @BindView(R.id.tv_admin_login) TextView adminLogin;
    @BindView(R.id.tv_staff_login) TextView staffLogin;
    @BindView(R.id.line_admin) View adminLine;
    @BindView(R.id.line_staff) View staffLine;

    public  final String TAG = Login.class.getSimpleName();
    public String jsonResponse;

    RequestQueue queue;
    CallbackManager callbackManager;

    PermissionListener permissionlistener;

    boolean loginFirstTime = false;
    public static boolean  loginAdmin = false;
    public  String  is_admin = "0";
    private static View systemUIView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /////////////////// Permision Listener for Runtime Permission////////////////////////////
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if(Login.this != null) {
                    Toast.makeText(Login.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                if(Login.this!= null) {
                    Toast.makeText(Login.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            }


        };


        new TedPermission(Login.this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] -> [Permission]")
                .setPermissions(android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();

        // hide the native android navigation and status bar
        systemUIView = getWindow().getDecorView();

        final Window win = getWindow();
        win.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );
        queue = Volley.newRequestQueue(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initLayout();






    }



    private void setAdminLogin() {
        staffLogin.setTextColor(getResources().getColor(R.color.color_text_unselected));
        staffLine.setVisibility(View.INVISIBLE);
        adminLogin.setTextColor(getResources().getColor(R.color.color_text_selected));
        adminLine.setVisibility(View.VISIBLE);
        loginAdmin = true;
        is_admin = "1";
        // clear edittext
        username.setText("");
        username.setError(null);
        password.setText("");
        password.setError(null);

    }
    private void setStaffLogin() {
        adminLogin.setTextColor(getResources().getColor(R.color.color_text_unselected));
        adminLine.setVisibility(View.INVISIBLE);
        staffLogin.setTextColor(getResources().getColor(R.color.color_text_selected));
        staffLine.setVisibility(View.VISIBLE);
        loginAdmin = false;
        is_admin = "0";
        // clear edittext
        username.setText("");
        username.setError(null);
        password.setText("");
        password.setError(null);
    }



    private void initLayout() {
        ButterKnife.bind(this);

        adminLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               setAdminLogin();
            }
        });

        staffLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setStaffLogin();
            }
        });

        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( Login.this , ForgetPassword.class));
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()) {

                    if (!validateUsername()) {
                        return;
                    }

                    if (!validatePassword()) {
                        return;
                    }

                    if(TextUtils.isEmpty(username.getText().toString().trim()) && TextUtils.isEmpty(password.getText().toString().trim()))
                    {
                        Toast.makeText(getApplicationContext(),
                                "Username or Password cannot be empty" ,
                                Toast.LENGTH_LONG).show();
                    }else
                    {
                            signin.setEnabled(false);
                            makeRequest();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(),
                            "Check Your Internet Connect",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        if(readDataFromPrefs())
        {
            ///// set appropriate check here for admin and staff ////
            Log.d(TAG, "initLayout: Remeber checked Already");
            remember.setChecked(true);
            Intent i = new Intent(Login.this , NavigationS.class);
            i.putExtra("pref",true);
            startActivity(i);
            finish();
            return;
        }

        setAdminLogin();

        callbackManager = CallbackManager.Factory.create();
//        loginButton.setReadPermissions(Arrays.asList(
//                "public_profile", "email"));

//        fb.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                loginButton.performClick();
//            }
//        });

        // Other app specific specialization

//        // Callback registration
//        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//                Log.d(TAG, "onSuccess: "+loginResult.getAccessToken());
//
//                GraphRequest request = GraphRequest.newMeRequest(
//                        loginResult.getAccessToken(),
//                        new GraphRequest.GraphJSONObjectCallback() {
//
//                            @Override
//                            public void onCompleted(
//                                    JSONObject object,
//                                    GraphResponse response) {
//                                // Application code
//                                // Application code
//                                try {
//                                    Log.d(TAG, "onCompleted: Response is "+object.toString());
//                                    String email = object.getString("email");
//                                    String name = object.getString("name");
//                                    String firstName = object.getString("first_name");
//                                    String lastName = object.getString("last_name");
//                                    Log.d(TAG, "onCompleted: Email is "+email+"name"+name+"firstname"+firstName+"lastName"+lastName);
//
//                                    User user = new User();
//                                    user.setUser_name(name);
//                                    user.setFirst_name(firstName);
//                                    user.setLast_name(lastName);
//                                    user.setEmail(email);
//                                    AppController.userArrayList = new ArrayList<>();
//                                    userArrayList.add(0,user);
//
//                                    // start Intent service to send data ro server..
//                                    Intent msgIntent = new Intent(Login.this, SendFacebookData.class);
//                                    msgIntent.putExtra("username", name);
//                                    msgIntent.putExtra("email", email);
//                                    msgIntent.putExtra("first_name", firstName);
//                                    msgIntent.putExtra("last_name", lastName);
//                                    startService(msgIntent);
//
//                                    if(isFirstTimeLogin())
//                                    {
//                                        startActivity(new Intent( Login.this , Intro.class));
//                                    }
//                                    else
//                                    {
//                                        startActivity(new Intent( Login.this , NavigationS.class));
//                                    }
//
//                                } catch (JSONException e) {
//                                    FirebaseCrash.report(new Exception(e.getMessage()));
//                                    e.printStackTrace();
//                                }
//
//
//                            }
//                        });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,email,first_name,last_name");
//                request.setParameters(parameters);
//                request.executeAsync();
//
//
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//                Log.d(TAG, "onCancel: ");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.d(TAG, "onError: "+exception.toString());
//            }
//        });




    }



    private boolean validateUsername() {
        if (username.getText().toString().trim().isEmpty()) {
            username.setError(getString(R.string.err_msg_name));
            requestFocus(username);
            return false;
        } else {
           // username.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {
        if (password.getText().toString().trim().isEmpty()) {
            password.setError(getString(R.string.err_msg_name));
            requestFocus(password);
            return false;
        } else {
          //  passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    private void makeRequest() {

       showpDialog();

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL,
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

                                User user = new User();
                                user.setFirst_name(data.getString("first_name"));
                                user.setLast_name(data.getString("last_name"));
                                user.setUser_name(data.getString("username"));
                                user.setEmail(data.getString("email"));
                                user.setId(data.getString("company_id"));
                                AppController.userArrayList = new ArrayList<>();
                                userArrayList.add(0,user);
                                Log.d(TAG, "onResponse: size is "+userArrayList.size()+"email is "+data.getString("email"));

                                // user successfully login..
                                  Toast.makeText(getApplicationContext(),
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();

                                if(remember.isChecked())
                                {
                                    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                                            .edit()
                                            .putString("username", user.getFirst_name())
                                            .putString("password", password.getText().toString().trim())
                                            .putString("email", user.getEmail())
                                            .putString("user_id", user.getId())
                                            .putBoolean("is_admin", loginAdmin)
//                                            .putString("Art_id", artist_id)
//                                            .putString("Logintype", login_type)
//                                            .putString("p_name", f_name)
//                                            .putString("prf_image", p_image)
                                            .commit();
                                    Log.d(TAG, "onResponse: Saved in Shared Preferences..");

                                }


                                if(isFirstTimeLogin())
                                {
                                    startActivity(new Intent( Login.this , Intro.class));
                                }
                                else
                                {
                                    startActivity(new Intent( Login.this , NavigationS.class));
                                    finish();
                                }

                                signin.setEnabled(true);


                            }

                            jsonResponse = "";
                            jsonResponse += "Name: " + name + "\n\n";

                            Log.d(TAG, "onResponse: Json Response is "+jsonResponse);




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
                                signin.performLongClick();
                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username.getText().toString().trim());
                params.put("password", password.getText().toString().trim());
                params.put("device_token",getToken());
                params.put("is_admin",is_admin);
                return params;
            }
        };
             /*  RetryPolicy policy = new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                strRequest.setRetryPolicy(policy);
             */
        strRequest.setShouldCache(false);
        queue.add(strRequest);

        signin.setEnabled(true);

    }

    private String getToken() {

        Log.d(TAG, "getToken: ");

        SharedPreferences prefs = getSharedPreferences(AppController.PREF_NAME_TOKEN, MODE_PRIVATE);

        String token = prefs.getString("token", "");

        if (!TextUtils.isEmpty(token)) {
            Log.d(TAG, "getToken "+token);
            return token;
        }
        return token;
    }

    private boolean readDataFromPrefs() {
        Log.d(TAG, "readDataFromPrefs: ");
        SharedPreferences prefs = getSharedPreferences(AppController.PREF_NAME, MODE_PRIVATE);
        String username = prefs.getString("username", null);
        String password = prefs.getString("password", null);
        if (!TextUtils.isEmpty(username)) {
            Log.d(TAG, "readDataFromPrefs: "+username+"-"+password);
            loginAdmin = prefs.getBoolean("is_admin", false);
            return true;
        }
        return false;
    }

    private boolean isFirstTimeLogin()
    {
        Log.d(TAG, "checkFirstTimeLogin: ");
        SharedPreferences prefs = getSharedPreferences(AppController.PREF_NAME_TOKEN, MODE_PRIVATE);
        boolean isFirstTime = prefs.getBoolean("first_time_login", true);
        if (isFirstTime) {
            return true;
        }
        return false;
    }

    private void showpDialog() {
            progressBar.setVisibility(View.VISIBLE);
    }

    private void hidepDialog() {
        progressBar.setVisibility(View.INVISIBLE);
    }


    public  boolean hasActiveInternetConnection() {
        if (isNetworkAvailable()) {
            try {
                HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();
                return (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e(TAG, "Error checking internet connection", e);
            }
        } else {
            Log.d(TAG, "No network available!");
        }
        return false;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }





}
