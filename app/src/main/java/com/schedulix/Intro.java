package com.schedulix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;

import java.util.ArrayList;
import java.util.List;

import static com.schedulix.AppController.PREF_NAME;
import static com.schedulix.AppController.PREF_NAME_TOKEN;

/**
 * Created by Nsol on 3/21/2017.
 */

public class Intro extends AhoyOnboarderActivity {
    private static final String TAG = Intro.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState){


        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );

        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Schedulix",getResources().getString(R.string.splashText), R.drawable.walk);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));

        AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Schedulix", getResources().getString(R.string.splashText), R.drawable.walk);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));

        AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Schedulix", getResources().getString(R.string.splashText), R.drawable.walk);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setTitleColor(R.color.white);
        ahoyOnboarderCard1.setDescriptionColor(R.color.grey_200);
        ahoyOnboarderCard1.setTitleTextSize(dpToPixels(10, this));
        ahoyOnboarderCard1.setDescriptionTextSize(dpToPixels(8, this));

        List<AhoyOnboarderCard> pages = new ArrayList<>();
        pages.add(ahoyOnboarderCard1);
        pages.add(ahoyOnboarderCard2);
        pages.add(ahoyOnboarderCard3);


        setInactiveIndicatorColor(R.color.colorBlack);
        setActiveIndicatorColor(R.color.white);
        setFinishButtonTitle("Get Started");


        setOnboardPages(pages);

    }

    @Override
    public void onFinishButtonPressed() {
        // save login first time to true..
        getSharedPreferences(PREF_NAME_TOKEN, MODE_PRIVATE)
                .edit()
                .putBoolean("first_time_login",false)
                .commit();
        startActivity(new Intent( Intro.this , NavigationS.class));
        this.finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: ");
    }




}
