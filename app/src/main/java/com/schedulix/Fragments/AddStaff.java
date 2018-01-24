package com.schedulix.Fragments;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.schedulix.AppController;
import com.schedulix.ModelClasses.*;
import com.schedulix.R;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.bitmap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddStaff.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddStaff#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddStaff extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Create Staff" ;
    private static final int INTENT_REQUEST_GET_IMAGES = 13;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Context context;
    PermissionListener permissionlistener;

    @BindView(R.id.edtName) EditText edtName;
    @BindView(R.id.edtNameLast) EditText lastName;
    @BindView(R.id.edtEmail)EditText edtEmail;
    @BindView(R.id.edtMobileNo)EditText edtMobileNo;
    @BindView(R.id.edtDescription)EditText edtDescription;
    @BindView(R.id.edtSingular)EditText edtSingular;
    @BindView(R.id.edtPlural)EditText edtPlural;

    @BindView(R.id.NameLayout)TextInputLayout nameLayout;
    @BindView(R.id.NameLayoutLast)TextInputLayout nameLayoutLast;
    @BindView(R.id.emailLayout)TextInputLayout emailLayout;
    @BindView(R.id.descriptionLayout)TextInputLayout descriptionLayout;
    @BindView(R.id.mobileNoLayout)TextInputLayout mobileNoLayout;
    @BindView(R.id.btn_cancel)Button close;
    @BindView(R.id.btn_register_staff)Button register;
    @BindView(R.id.btn_upload)Button uploadImg;
    @BindView(R.id.btn_save)Button updateGeneralName;
    @BindView(R.id.profile_img)CircleImageView profilePic;


    String base64Image = null;


    RequestQueue queue;


    public AddStaff() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddStaff.
     */
    // TODO: Rename and change types and number of parameters
    public static AddStaff newInstance(String param1, String param2) {
        AddStaff fragment = new AddStaff();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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

///////////// get Singular and Plural Data from webservice///////////////////////
        callGetGeneralName();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);
        final View view = inflater.inflate(R.layout.fragment_add_staff, container, false);
        ButterKnife.bind(this, view);





        updateGeneralName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUpdateGeneralName();
            }
        });
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImg();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerStaff();
            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup all fragments
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
            }
        });

/////////////////// Permision Listener for Runtime Permission////////////////////////////
        permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                if(getActivity() != null) {
                    Toast.makeText(getActivity(), "Permission Granted", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                if(getActivity() != null) {
                    Toast.makeText(getActivity(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            }


        };


        return view;
    }

    private void uploadImg() {

        new TedPermission(getActivity())
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] -> [Permission]")
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void registerStaff()
    {
        Log.d(TAG, "registerStaff: ");
        /// if no image selected///
        if(base64Image == null) {

            Log.d(TAG, "registerStaff: Image Null");
            BitmapDrawable drawable = (BitmapDrawable) profilePic.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
            byte[] b = baos.toByteArray();
            base64Image = Base64.encodeToString(b, Base64.DEFAULT);

        }
        Log.d(TAG, "registerStaff: ");

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

        if (!validateDescription()) {
            return;
        }

        final String name = edtName.getText().toString().trim();
        final String email = edtEmail.getText().toString().trim();
        final String mobileNo = edtMobileNo.getText().toString().trim();
        final String description = edtDescription.getText().toString().trim();
        callAddStaff(name, email, mobileNo, description);
        Log.d(TAG, "onClick: Register Staff: " + name + "" + email + "" + mobileNo + "" + description);

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
            nameLayout.setError(getString(R.string.err_msg_name));
            requestFocus(edtName);
            return false;
        } else {
            nameLayout.setErrorEnabled(false);
        }

        return true;
    }
    private boolean validateLastName() {
        if (lastName.getText().toString().trim().isEmpty()) {
            nameLayout.setError(getString(R.string.err_msg_name));
            requestFocus(lastName);
            return false;
        } else {
            nameLayoutLast.setErrorEnabled(false);
        }

        return true;
    }


    private boolean validateMobileNo() {
        if (edtMobileNo.getText().toString().trim().isEmpty()) {
            mobileNoLayout.setError(getString(R.string.err_msg_name));
            requestFocus(edtMobileNo);
            return false;
        } else {
            mobileNoLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateDescription() {
        if (edtDescription.getText().toString().trim().isEmpty()) {
            descriptionLayout.setError(getString(R.string.err_msg_name));
            requestFocus(edtDescription);
            return false;
        } else {
            descriptionLayout.setErrorEnabled(false);
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
            emailLayout.setError(getString(R.string.err_msg_email));
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




    private void callAddStaff(final String name, final String email, final String mobileNo, final String description) {

        Log.d(TAG, "callAddStaff: " + name + "" + email + "" + mobileNo + "" + description);
        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_ADD_STAFF,
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

                                JSONObject staffJsonObj = success.getJSONObject("data");
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

                final String last_name = lastName.getText().toString().trim();
                Log.d(TAG, "getParams: Company Id is "+ com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id()+"Staff image"+base64Image);

                params.put("company_id", com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id());
                params.put("first_name", name);
                params.put("last_name", last_name);
                params.put("email", email);
                params.put("mobile_no", mobileNo);
                params.put("description", description);
                params.put("profile_pic", base64Image);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }



    private void callGetGeneralName() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_GENERAL_NAME,
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

                                JSONArray staffs = success.getJSONArray("generalname");
                                for (int i = 0; i < staffs.length(); i++) {
                                    JSONObject staffJsonObj = staffs.getJSONObject(i);
                                    edtSingular.setText(staffJsonObj.getString("singular_name"));
                                    edtPlural.setText(staffJsonObj.getString("plural_name"));
                                }
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();


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
                Log.d(TAG, "getParams: Company Id is "+ShowStaffs.userId);
                params.put("company_id", ShowStaffs.userId);
                params.put("type", "2");
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }



    private void callUpdateGeneralName() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_UPDATE_GENERAL_NAME,
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
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();


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
                Log.d(TAG, "getParams: Company Id is "+ShowStaffs.userId);
                params.put("company_id", ShowStaffs.userId);
                params.put("type", "2");
                params.put("singular_name", edtSingular.getText().toString());
                params.put("plural_name", edtPlural.getText().toString());

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }

    @Override
    public void onActivityResult(int requestCode, int resuleCode, Intent intent) {
        super.onActivityResult(requestCode, resuleCode, intent);

        if (requestCode == INTENT_REQUEST_GET_IMAGES && resuleCode == getActivity().RESULT_OK ) {

            Uri uri = null;
            if (intent != null) {
                uri = intent.getData();
                Log.i(TAG, "Uri: " + uri.getPath());
            }

            Log.d(TAG, "onActivityResult: "+uri.toString());
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                profilePic.setImageBitmap(bmp);
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


            Log.d(TAG, "onActivityResult: Image is "+base64Image);


            //do something
        }
    }




}
