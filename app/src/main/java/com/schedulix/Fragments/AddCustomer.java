package com.schedulix.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.schedulix.AppController;
import com.schedulix.ModelClasses.*;
import com.schedulix.ModelClasses.AddAppointment;
import com.schedulix.R;
import com.schedulix.Register;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.schedulix.AppController.PREF_NAME;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddCustomer.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddCustomer#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCustomer extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters

    private static String fromAppointment = "0";
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String base64Image = null;
    public Boolean initialized = false;

    Context context;
    PermissionListener permissionlistener;
    @BindView(R.id.edtFirstName)
    EditText edtName;
    @BindView(R.id.edtLastName)
    EditText edtLastName;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.edtMobileNo)
    EditText edtMobileNo;
    @BindView(R.id.edtAddress)
    EditText edtAddress;
    @BindView(R.id.edtCountry)
    EditText edtCountry;
    @BindView(R.id.edtRegion)
    EditText edtRegion;
    @BindView(R.id.edtCity)
    EditText edtCity;

    @BindView(R.id.NameLayout)
    TextInputLayout nameLayout;
    @BindView(R.id.NameLayoutLast)
    TextInputLayout nameLayoutLast;
    @BindView(R.id.emailLayout)
    TextInputLayout emailLayout;
    @BindView(R.id.addressLayout)
    TextInputLayout addressLayout;
    @BindView(R.id.mobileNoLayout)
    TextInputLayout mobileNoLayout;
    @BindView(R.id.countryLayout)
    TextInputLayout countryLayout;
    @BindView(R.id.regionLayout)
    TextInputLayout regionLayout;
    @BindView(R.id.cityLayout)
    TextInputLayout cityLayout;

    @BindView(R.id.btn_cancel)
    Button close;
    @BindView(R.id.btn_register_staff)
    Button register;
    @BindView(R.id.profile_img)
    CircleImageView p_img;

    RequestQueue queue;

    public static String customerId;

    private OnFragmentInteractionListener mListener;
    private String TAG = "AddCoustomer";
    private java.util.Calendar startTime = java.util.Calendar.getInstance();

    public AddCustomer() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCustomer.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCustomer newInstance(String param1, String param2) {
        AddCustomer fragment = new AddCustomer();
        Bundle args = new Bundle();
        fromAppointment = param1;
        customerId = param2;
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("starttime", startTime);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_add_customer, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Customer");
        ButterKnife.bind(this, view);

        if (fromAppointment.equals("2")) {
            // Get Customer detail  ///
            if (initialized == false) {
                getcustomersdetails();
            } else {

            }


        }

        p_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImg();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // for not calling get customer detail again////
                context.getSharedPreferences(PREF_NAME, 0)
                        .edit()
                        .putBoolean("first_time_show",false)
                        .commit();

                // move back //////
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.popBackStack();
            }
        });

        edtCountry.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {
                    // Display Dialog with Service List....
                    Fragment fragment = ShowCountry.newInstance("", "");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(TAG);
                    fragmentTransaction.commit();
                }

                return true; // return is important...
            }
        });


        edtRegion.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {

                    if (!validateCountry()) {
                        return true;
                    }

                    // Display Dialog with Service List....
                    Fragment fragment = ShowRegion.newInstance("", "");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(TAG);
                    fragmentTransaction.commit();
                }

                return true; // return is important...
            }
        });

        edtCity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_DOWN == motionEvent.getAction()) {


                    if (!validateCountry()) {
                        return true;
                    }

                    if (!validateRegion()) {
                        return true;
                    }

                    // Display Dialog with Service List....
                    Fragment fragment = ShowCity.newInstance("", "");
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.frame, fragment);
                    fragmentTransaction.addToBackStack(TAG);
                    fragmentTransaction.commit();
                }


                return true; // return is important...
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // register customer here //
                registerCustomer();
            }
        });


        if (fromAppointment.equals("2")) {
            register.setText("Update");
            p_img.setVisibility(View.VISIBLE);


        } else {
            register.setText("Add");
            p_img.setVisibility(View.GONE);
        }


        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            }


        };
        // Inflate the layout for this fragment
        return view;
    }

    private void registerCustomer() {
        Log.d(TAG, "registerStaff: ");

        if (base64Image == null) {

            Log.d(TAG, "registerStaff: Image Null");
            BitmapDrawable drawable = (BitmapDrawable) p_img.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            base64Image = Base64.encodeToString(b, Base64.DEFAULT);

        }

        if (!validateFirstName()) {
            return;
        }

        if (!validateLastName()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        if (!validateMobileNo()) {
            return;
        }

        if (!validateAddress()) {
            return;
        }

        final String firstName = edtName.getText().toString().trim();
        final String lastName = edtLastName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String mobileNo = edtMobileNo.getText().toString().trim();
        final String address = edtAddress.getText().toString().trim();
        Log.d(TAG, "onClick: Register Staff: " + firstName + "" + lastName + email + "" + mobileNo + "" + address);


        if (fromAppointment.equals("2")) {
            // Update Customer here ///
            Updatecustomersdetails();
        } else {
            callAddCustomer(firstName, lastName, email, mobileNo, address);
        }



    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (Country.getInstance().getName() != null) {
            edtCountry.setText(Country.getInstance().getName().toString());
            edtCountry.setError(null);

            if (Region.getInstance().getName() != null) {
                edtRegion.setText(Region.getInstance().getName().toString());
                edtCountry.setError(null);
                edtRegion.setError(null);
            }
            if (City.getInstance().getName() != null) {
                edtCity.setText(City.getInstance().getName().toString());
                edtCountry.setError(null);
                edtRegion.setError(null);
            }
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    /////////////// Utility Methods/////////////////////

    private boolean validateFirstName() {
        if (edtName.getText().toString().trim().isEmpty()) {
            edtName.setError(getString(R.string.err_msg_name));
            requestFocus(edtName);
            return false;
        } else {
            nameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateLastName() {
        if (edtLastName.getText().toString().trim().isEmpty()) {
            edtLastName.setError(getString(R.string.err_msg_name));
            requestFocus(edtLastName);
            return false;
        } else {
            nameLayoutLast.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateMobileNo() {
        if (edtMobileNo.getText().toString().trim().isEmpty()) {
            edtMobileNo.setError(getString(R.string.err_msg_name));
            requestFocus(edtMobileNo);
            return false;
        } else {
            mobileNoLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAddress() {
        if (edtAddress.getText().toString().trim().isEmpty()) {
            edtAddress.setError(getString(R.string.err_msg_name));
            requestFocus(edtAddress);
            return false;
        } else {
            addressLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCountry() {
        if (edtCountry.getText().toString().trim().isEmpty()) {
            edtCountry.setError(getString(R.string.err_msg_name));
            requestFocus(edtCountry);
            return false;
        } else {
            //  passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateRegion() {
        if (edtRegion.getText().toString().trim().isEmpty()) {
            edtRegion.setError(getString(R.string.err_msg_name));
            requestFocus(edtRegion);
            return false;
        } else {
            //  passwordLayout.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateEmail() {
        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            edtEmail.setError(getString(R.string.err_msg_email));
            requestFocus(edtEmail);
            return false;
        } else {
            emailLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }




    private void callAddCustomer(final String firstName, final String lastName, final String email, final String mobileNo, final String address) {

        Log.d(TAG, "callAddStaff: " + firstName + "" + email + "" + mobileNo + "" + address);
        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_ADD_CUSTOMER,
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
                            if (status == 0) {
                                // user not login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            } else {

                                JSONObject customer = success.getJSONObject("Customers");

//                                Staff staffObj = new Staff();
//                                staffObj.setId(staffJsonObj.getString("id"));
//                                staffObj.setCompany_id(staffJsonObj.getString("company_id"));
//                                staffObj.setFirst_name(staffJsonObj.getString("first_name"));
//                                staffObj.setEmail(staffJsonObj.getString("email"));
//                                staffObj.setDescription(staffJsonObj.getString("description"));
//                                Log.d(TAG, "onResponse: New Item Position "+(staffArrayList.size()-1));
//                                staffArrayList.add((staffArrayList.size()-1),staffObj);
                                // user successfully login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();

                                if (fromAppointment.equals("0")) {
                                    // move back //////
                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    manager.popBackStack();
                                    return;
                                }

                                // set Customer name for  previous Screen///
                                Customer.getInstance().setFirst_name(firstName);
                                // pass customer id for add apointment
                                com.schedulix.ModelClasses.AddAppointment.getInstance().setCustomer_id(customer.getString("id"));
                                Log.d(TAG, "onResponse: Id is " + customer.getString("id"));

                                // move back //////
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                manager.popBackStack();

                            }

//                            progressBar.setVisibility(View.GONE);
                            //// call the query here again////


                            // txtResponse.setText(jsonResponse);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            FirebaseCrash.report(new Exception(e.getMessage()));
                            Toast.makeText(context,
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
                                Toast.makeText(context, "No internet Access, Check your internet connection", Toast.LENGTH_SHORT).show();
                            }

                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(context,
                                        "No internet connection.  Please connect to the internet and try again",
                                        Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.d(TAG, "getParams: Company Id is " + com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());

                params.put("company_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());
                params.put("first_name", firstName);
                params.put("last_name", lastName);
                params.put("email", email);
                params.put("mobile", mobileNo);
                params.put("address", address);
                params.put("country", Country.getInstance().getId());
                params.put("region", Region.getInstance().getId());
                params.put("city", City.getInstance().getId());
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }


    public void getcustomersdetails() {
        Log.d(TAG, "getcustomersdetails: ");
        initialized = true;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_CUSTOMER_DETAIL, new Response.Listener<String>() {
            // StringRequest stringRequest = new StringRequest
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectdata = jsonObject.getJSONObject("success");

                    int status = jsonObjectdata.getInt("status");
                    if (status == 0) {
                        // user not login..
                        Toast.makeText(context,
                                "" + jsonObjectdata.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } else {
                        String imagePath = jsonObjectdata.getString("imagepath");
                        String path = imagePath;
                        Log.d(TAG, "onResponse: Full path is "+path);

                        JSONObject jsonObjectdata1 = jsonObjectdata.getJSONObject("customers");
                           // String zipcode = jsonObjectdata.getString("zip");
                            edtName.setText(jsonObjectdata1.getString("first_name"));
                            edtLastName.setText(jsonObjectdata1.getString("last_name"));
                            edtEmail.setText(jsonObjectdata1.getString("email"));
                            edtMobileNo.setText(jsonObjectdata1.getString("mobile"));
                            edtAddress.setText(jsonObjectdata1.getString("address"));
                            edtCountry.setText(jsonObjectdata1.getString("country_name"));
                            edtRegion.setText(jsonObjectdata1.getString("region_name"));
                            edtCity.setText(jsonObjectdata1.getString("city_name"));
                            Country.getInstance().setId(jsonObjectdata1.getString("country"));
                            Region.getInstance().setId(jsonObjectdata1.getString("region"));
                            City.getInstance().setId(jsonObjectdata1.getString("city"));


                            Picasso.with(context).load(imagePath+"/"+jsonObjectdata1.getString("picture_url")).error(R.drawable.face).fit().into(p_img);




                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse == null) {
                    if (volleyError instanceof NoConnectionError) {

                        Toast.makeText(getActivity(), "No internet Access, Check your internet connection", Toast.LENGTH_SHORT).show();
                    }

                    if (volleyError.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message

                        Toast.makeText(getActivity(),
                                "No internet connection.  Please connect to the internet and try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id", customerId);

                return params;

            }


        };
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);

        // for not calling get customer detail again////
        context.getSharedPreferences(PREF_NAME, 0)
                .edit()
                .putBoolean("first_time_show",false)
                .commit();

    }


    public void Updatecustomersdetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, AppController.URL_UPDATE_CUSTOMER_DETAIL, new Response.Listener<String>() {
            // StringRequest stringRequest = new StringRequest
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObjectdata = jsonObject.getJSONObject("success");

                    int status = jsonObjectdata.getInt("status");
                    if (status == 0) {
                        // user not login..
                        Toast.makeText(context,
                                "" + jsonObjectdata.getString("message"),
                                Toast.LENGTH_LONG).show();
                    } else {

                        Toast.makeText(context,
                                "" + jsonObjectdata.getString("message"),
                                Toast.LENGTH_LONG).show();

                        // move back //////

                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.popBackStack();
                    }

                   /* JSONArray jsonobj = jsonObjectdata.getJSONArray("customers");
                    for (int i = 0; i < jsonobj.length(); i++) {
                        JSONObject jobj = jsonobj.getJSONObject(i);
                        String zipcode = jobj.getString("zip");
                        edtName.setText(jobj.getString("first_name"));
                        edtLastName.setText(jobj.getString("last_name"));
                        edtEmail.setText(jobj.getString("email"));
                        edtMobileNo.setText(jobj.getString("mobile"));
                        edtAddress.setText(jobj.getString("address"));
                        edtCountry.setText(jobj.getString("country"));
                        edtRegion.setText(jobj.getString("region"));
                        edtCity.setText(jobj.getString("city"));


                    }*/


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse == null) {
                    if (volleyError instanceof NoConnectionError) {

                        Toast.makeText(getActivity(), "No internet Access, Check your internet connection", Toast.LENGTH_SHORT).show();
                    }

                    if (volleyError.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message

                        Toast.makeText(getActivity(),
                                "No internet connection.  Please connect to the internet and try again",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("customer_id", customerId);
                params.put("company_id", AddAppointment.getInstance().getCompany_id());
                params.put("first_name", edtName.getText().toString());
                params.put("last_name", edtLastName.getText().toString());
                params.put("email", edtEmail.getText().toString());
                params.put("mobile", edtMobileNo.getText().toString());
                params.put("address", edtAddress.getText().toString());
                //  params.put("country", edtCountry.getText().toString());
                //    params.put("region", edtRegion.getText().toString());
                params.put("country", Country.getInstance().getId());
                params.put("region", Region.getInstance().getId());
                params.put("city", City.getInstance().getId());
                params.put("profile_pic", base64Image);

                return params;

            }


        };
        stringRequest.setShouldCache(false);
        queue.add(stringRequest);

    }

    private void uploadImg() {

        new TedPermission(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] -> [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();


        //////////// Dialog Picker////////////////

//        Config config = new Config();
//        config.setSelectionMin(1);
//        config.setSelectionLimit(1);
//        ImagePickerActivity.setConfig(config);
//        Intent intent  = new Intent(getActivity(), ImagePickerActivity.class);
//        startActivityForResult(intent,INTENT_REQUEST_GET_IMAGES);


        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), INTENT_REQUEST_GET_IMAGES);


    }


    @Override
    public void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resuleCode == getActivity().RESULT_OK) {

            Uri uri = null;
            if (intent != null) {
                uri = intent.getData();
                Log.i(TAG, "Uri: " + uri.getPath());
            }

            Log.d(TAG, "onActivityResult: " + uri.toString());
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                p_img.setImageBitmap(bmp);
            } catch (IOException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Must compress the Image to reduce image size to make upload easy
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);


            byte[] byte_arr = stream.toByteArray();
//            long lengthbmp = byte_arr.length;
//            long fileSizeInKB = lengthbmp / 1024;
//            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
//            long fileSizeInMB = lengthbmp / 1024;
//            // Encode Image to String
            base64Image = Base64.encodeToString(byte_arr, Base64.DEFAULT);


            Log.d(TAG, "onActivityResult: Image is " + base64Image);


            //do something
        }
    }


}
