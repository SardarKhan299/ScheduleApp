package com.schedulix.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
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
import com.schedulix.AppController;
import com.schedulix.ModelClasses.Categories;
import com.schedulix.ModelClasses.Service;
import com.schedulix.ModelClasses.StaffTimings;
import com.schedulix.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddService.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddService#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddService extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context context;
    String category_id;

    private OnFragmentInteractionListener mListener;


    /////////////Edit Text///////////////////////
    @BindView(R.id.edtCategory) EditText edtCategory;
    @BindView(R.id.edtTitle)EditText edtTitle;
    @BindView(R.id.edtDescription)EditText edtDescription;
    @BindView(R.id.edtDuration) EditText edtDuration;
    @BindView(R.id.edtDurationType) EditText edtDurationType;
    @BindView(R.id.edtPrice)EditText edtPrice ;
    @BindView(R.id.edtCapacity)EditText edtCapacity ;
    @BindView(R.id.edtSingular)EditText edtSingular;
    @BindView(R.id.edtPlural)EditText edtPlural;

    //////////Text input Layout//////////////////////////
    @BindView(R.id.categoryLayout)TextInputLayout categoryLayout;
    @BindView(R.id.titleLayout)TextInputLayout titleLayout;
    @BindView(R.id.descriptionLayout)TextInputLayout descriptionLayout;
    @BindView(R.id.durationLayout)TextInputLayout durationLayout;
    @BindView(R.id.priceLayout)TextInputLayout priceLayout;
    @BindView(R.id.capacityLayout)TextInputLayout capacityLayout;

    /////////// Buttons /////////////////
    @BindView(R.id.btn_cancel)Button close;
    @BindView(R.id.btn_register_service)Button register;
    @BindView(R.id.btn_save)Button updateGeneralName;

    RequestQueue queue;
    ArrayList<Categories> categoriesArrayList ;

    public AddService() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddService.
     */
    // TODO: Rename and change types and number of parameters
    public static AddService newInstance(String param1, String param2) {
        AddService fragment = new AddService();
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

        categoriesArrayList = new ArrayList<>();
        ///////////// get Singular and Plural Data from webservice///////////////////////
        callGetGeneralName();
        /////////////// Get Category Name //////////////////////
        getCategories();
        // set Toolbar title
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(TAG);
        final View view = inflater.inflate(R.layout.fragment_add, container, false);
        ButterKnife.bind(this, view);
        edtDurationType.setText("Minute");
        edtDurationType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showDurationDialog();
                }
                return true;
            }
        });

        edtCategory.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    showCategoryList(categoriesArrayList);
                }
                return true;
            }
        });

        updateGeneralName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callUpdateGeneralName();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerStaff();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void getCategories() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_GET_CATEGRORIES,
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

                                JSONArray services = success.getJSONArray("categories");
                                for (int i = 0; i < services.length(); i++) {
                                    JSONObject serviceJsonObject = services.getJSONObject(i);
                                    Categories category = new Categories();
                                    category.setId(serviceJsonObject.getString("id"));
                                    category.setName(serviceJsonObject.getString("category"));
                                    categoriesArrayList.add(category);
                                }
                                Log.d(TAG, "onResponse: " + categoriesArrayList.size());

                                //////////////// Set first category name and id//////////////////////
                                edtCategory.setText(categoriesArrayList.get(0).getName().toString());
                                category_id = categoriesArrayList.get(0).getId().toString();

                                // user successfully login..
                                Toast.makeText(context,
                                        "" + success.getString("message"),
                                        Toast.LENGTH_LONG).show();
                            }




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
                params.put("company_id", Services.userId);
                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);
    }

    private void showDurationDialog() {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Minute");
        arrayAdapter.add("Hour");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                edtDurationType.setText(strName);
                dialog.dismiss();
            }
        });
        builderSingle.show();
    }

    private void showCategoryList(final ArrayList<Categories> categoriesArrayList) {

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
        for(int i =0; i<categoriesArrayList.size();i++)
        {
            arrayAdapter.add(categoriesArrayList.get(i).getName());
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = categoriesArrayList.get(which).getName();
                edtCategory.setText(strName);
                category_id = categoriesArrayList.get(which).getId();
                dialog.dismiss();
            }
        });
        builderSingle.show();
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

    public void registerStaff()
    {
        Log.d(TAG, "register Service: ");

        if (!validateTitle()) {
            return;
        }

        if (!validateDescription()) {
            return;
        }

        if (!validateDuration()) {
            return;
        }

        if (!validatePrice()) {
            return;
        }

        if (!validateCapacity()) {
            return;
        }

////////// Call Api /////////////
        callAddService();
      //  Log.d(TAG, "onClick: Register Staff: " + name + "" + email + "" + mobileNo + "" + description);

    }


    /////////////// Utility Methods/////////////////////

    private boolean validateTitle() {
        if (edtTitle.getText().toString().trim().isEmpty()) {
            titleLayout.setError(getString(R.string.err_msg_name));
            requestFocus(edtTitle);
            return false;
        } else {
            titleLayout.setErrorEnabled(false);
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


    private boolean validateDuration() {
        if (edtDuration.getText().toString().trim().isEmpty()) {
            durationLayout.setError(getString(R.string.err_msg_name));
            requestFocus(edtDuration);
            return false;
        } else {
            durationLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePrice() {
        if (edtPrice.getText().toString().trim().isEmpty()) {
            priceLayout.setError(getString(R.string.err_msg_name));
            requestFocus(edtPrice);
            return false;
        } else {
            priceLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCapacity() {
        if (edtCapacity.getText().toString().trim().isEmpty()) {
            capacityLayout.setError(getString(R.string.err_msg_name));
            requestFocus(edtCapacity);
            return false;
        } else {
            capacityLayout.setErrorEnabled(false);
        }

        return true;
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

/////////////////////////// END OF METHODS/////////////////////////////////////////////////////

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
                Log.d(TAG, "getParams: Company Id is "+Services.userId);
                params.put("company_id", Services.userId);
                params.put("type", "1");
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
                Log.d(TAG, "getParams: Company Id is "+Services.userId);
                params.put("company_id", Services.userId);
                params.put("type", "1");
                params.put("singular_name", edtSingular.getText().toString());
                params.put("plural_name", edtPlural.getText().toString());

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }




    private void callAddService() {

        StringRequest strRequest = new StringRequest(Request.Method.POST, AppController.URL_ADD_SERVICE,
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
                             //   Services.MoveNextFragment();

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

                Log.d(TAG, "getParams: Params "+ Services.userId+"categoryId is"+category_id);

                final String title = edtTitle.getText().toString().trim();
                final String description = edtDescription.getText().toString().trim();
                final String duration = edtDuration.getText().toString().trim();
                final String duration_type = edtDurationType.getText().toString().trim();
                final String price = edtPrice.getText().toString().trim();
                final String capacity = edtCapacity.getText().toString().trim();

                params.put("company_id", Services.userId);
                params.put("title", title);
                params.put("description", description);
                params.put("price", price);
                params.put("duration", duration);
                params.put("duration_type", duration_type);
                params.put("capacity", capacity);
                params.put("category_id", category_id);

                return params;
            }
        };
        strRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        strRequest.setShouldCache(false);
        queue.add(strRequest);


    }





}
