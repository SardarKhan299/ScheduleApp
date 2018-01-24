package com.schedulix.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.schedulix.AppController;
import com.schedulix.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UpdateProfile.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UpdateProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateProfile extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = UpdateProfile.class.getSimpleName() ;

    static String companyId = null;
    private static String staffId = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private static final int INTENT_REQUEST_GET_IMAGES = 14;
    String base64Image = null;
    Context context;
    RequestQueue queue;

    private OnFragmentInteractionListener mListener;
    PermissionListener permissionlistener;

    @BindView(R.id.edtName_update)EditText edtName;
    @BindView(R.id.edtNameLast_update) EditText edtLastName;
    @BindView(R.id.edtEmail_update)EditText edtEmail;
    @BindView(R.id.edtMobileNo_update)EditText edtMobileNo;
    @BindView(R.id.edtDescription_update)EditText edtDescription;

    @BindView(R.id.NameLayout_update)TextInputLayout nameLayout;
    @BindView(R.id.NameLayoutLast_update)TextInputLayout nameLayoutLast;
    @BindView(R.id.emailLayout_update)TextInputLayout emailLayout;
    @BindView(R.id.descriptionLayout_update)TextInputLayout descriptionLayout;
    @BindView(R.id.mobileNoLayout_update)TextInputLayout mobileNoLayout;
    @BindView(R.id.btn_cancel)Button cancel;
    @BindView(R.id.btn_update_profile)Button update;
    @BindView(R.id.profile_img_update)CircleImageView profilePic;
    @BindView(R.id.img_edit_profile)ImageView editPicture;

    public UpdateProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateProfile newInstance(String param1, String param2) {
        UpdateProfile fragment = new UpdateProfile();
        companyId = param1;
        staffId = param2;
        Log.d(TAG, "newInstance: Company Id is "+companyId+"Staff id is "+staffId);
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
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        ButterKnife.bind(this, view);
        getStaffDetail();

        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // popup all fragments
                FragmentManager fm = getActivity().getSupportFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    fm.popBackStack();
                }
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
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

    private void updateProfile() {
        /// if no image selected///
        if(base64Image == null) {
            Log.d(TAG, "updateProfile: Image Null");
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
        callUpdateStaff(name, email, mobileNo, description);
        Log.d(TAG, "onClick: Register Staff: " + name + "" + email + "" + mobileNo + "" + description);


    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void updateImage() {

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



    private void callUpdateStaff(final String name, final String email, final String mobileNo, final String description) {

        Log.d(TAG, "callAddStaff: " + name + "" + email + "" + mobileNo + "" + description);
        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_UPDATE_STAFF,
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

                                //JSONObject staffJsonObj = success.getJSONObject("staff");
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

                                // popup all fragments
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                                    fm.popBackStack();
                                }


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

                final String last_name = edtLastName.getText().toString().trim();
                Log.d(TAG, "getParams: Company Id is "+com.schedulix.ModelClasses.AddAppointment.getInstance().getCompany_id()+"Staff image"+base64Image);

                params.put("company_id", companyId);
                params.put("staff_id", staffId);
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



    private void getStaffDetail() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_STAFF_DETAIL,
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

                                JSONObject staffJsonObj = success.getJSONObject("staff");
                                String imagePath = success.getString("imagepath");
                                String imageUri = staffJsonObj.getString("profile_pic");
                                String path = imagePath+imageUri;
                                Log.d(TAG, "onResponse: Full path is "+path);
                                Picasso.with(context).load(path).error(R.drawable.face).placeholder(R.drawable.face).into(profilePic);
                                edtName.setText(staffJsonObj.getString("first_name"));
                                edtLastName.setText(staffJsonObj.getString("last_name"));
                                edtEmail.setText(staffJsonObj.getString("email"));
                                edtDescription.setText(staffJsonObj.getString("description"));
                                edtMobileNo.setText(staffJsonObj.getString("mobile_no"));

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
                params.put("company_id", companyId);
                params.put("staff_id", staffId);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


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
        if (edtLastName.getText().toString().trim().isEmpty()) {
            nameLayout.setError(getString(R.string.err_msg_name));
            requestFocus(edtLastName);
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
