package com.schedulix;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.util.Base64;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crash.FirebaseCrash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName() ;
//    TextView splashText;
    ImageView arrow,logo;
    private GestureDetector gestureDetector;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Window win = getWindow();
        win.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initLayout();




    }

    private void initLayout() {
        gestureDetector = new GestureDetector(new SwipeGestureDetector());
//        splashText = (TextView) findViewById(R.id.splashText);
        arrow = (ImageView) findViewById(R.id.arrow);
//        Typeface typeface=Typeface.createFromAsset(getAssets(), "asset/fonts/montserrat_regular.otf");
//        splashText.setTypeface(typeface);
//        splashText.setText(getResources().getString(R.string.splashText));

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  overridePendingTransition(R.anim.slide_up,R.anim.slide_up);
            }
        });
        logo = (ImageView) findViewById(R.id.imageViewLogo);
//        Animation bounceAnimation = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.bounce);
//        logo.startAnimation(bounceAnimation);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void onSwipeUp() {
        // Do something
        Log.d(TAG, "onSwipeUp: ");
        startActivity(new Intent(MainActivity.this,Login.class));
        this.finish();
    }

    private void onSwipeDown() {
        // Do something
        Log.d(TAG, "onSwipeDown: ");
    }


    // Private class for gestures
    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        // Swipe properties, you can change it to make the swipe
        // longer or shorter and speed
        private static final int SWIPE_MIN_DISTANCE = 100;
        private static final int SWIPE_MAX_OFF_PATH = 800;
        private static final int SWIPE_THRESHOLD_VELOCITY = 300;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        if (diffX > 0) {
                        } else {
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                    if (diffY > 0) {
                        MainActivity.this.onSwipeDown();
                    } else {
                        MainActivity.this.onSwipeUp();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                FirebaseCrash.report(new Exception(exception.getMessage()));
                exception.printStackTrace();
            }
            return result;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}
