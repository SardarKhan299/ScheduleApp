package com.schedulix.Firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import static com.schedulix.AppController.PREF_NAME_TOKEN;

/**
 * Created by Nsol on 3/23/2017.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);

    }

    private void sendRegistrationToServer(String token) {

        Log.d(TAG, "sendRegistrationToServer: "+token);
        //You can implement this method to store the token on your server
        //Not required for current project
        // required service to send refreshed Token
        // we can save this token in shared preferences...
        getSharedPreferences(PREF_NAME_TOKEN, MODE_PRIVATE)
                .edit()
                .putString("token", token)
                .commit();

    }
}
