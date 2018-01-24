package com.schedulix;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.schedulix.AppController.PREF_NAME;

import com.facebook.login.LoginManager;
import com.google.firebase.crash.FirebaseCrash;
import com.schedulix.Fragments.About;
import com.schedulix.Fragments.DashBoard;
import com.schedulix.Fragments.FeedBack;
import com.schedulix.Fragments.ListOfCustomers;
import com.schedulix.Fragments.Review;
import com.schedulix.Fragments.Bookings;
import com.schedulix.ModelClasses.AddAppointment;

import static com.schedulix.AppController.PREF_NAME;

public class NavigationS extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = NavigationS.class.getSimpleName() ;
    TextView headerName ,headerEmail;
    ImageView logout;
    boolean readDataFromPref = false;
    public static String CURRENT_TAG = AppController.TAG_BOOKING;
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    NavigationView navigationView;
    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;
    private Handler mHandler;
    boolean doubleBackToExitPressedOnce = false;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        final Window win = getWindow();
        win.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON );

        Intent i = getIntent();
        readDataFromPref = i.getBooleanExtra("pref",false);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
        mHandler = new Handler();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final View.OnClickListener originalToolbarListener = toggle.getToolbarNavigationClickListener();

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {

                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    toggle.setDrawerIndicatorEnabled(false);
                    toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getSupportFragmentManager().popBackStack();
                        }
                    });
                } else {
                    toggle.setDrawerIndicatorEnabled(true);
                    toggle.setToolbarNavigationClickListener(originalToolbarListener);
                }
            }
        });



        Log.d(TAG, "onCreate: Login Admin"+Login.loginAdmin);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initLayout();
        if (!Login.loginAdmin)
        {
            navigationView.getMenu().findItem(R.id.nav_reviews).setVisible(false);
        }

        if (savedInstanceState == null) {
            CURRENT_TAG = AppController.TAG_APPOINTMENT;
            loadHomeFragment();
        }

    }

    private void loadHomeFragment() {

        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {

                // popup all fragments
                // to avoid overlapping of the fragments..
                FragmentManager fm = getSupportFragmentManager();
                for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                    if(fm.findFragmentByTag("home"))
                    {

                    }
                    fm.popBackStack();
                }

                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();

            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        // refresh toolbar menu
        invalidateOptionsMenu();


    }


    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 0:
                return DashBoard.newInstance(user_id,"");

            case 1:
               // return BookingFragment.newInstance(user_id,"");
                return new Bookings();
            case 2:
                Review review = new Review();
                return review;

            case 3:
               return ListOfCustomers.newInstance("0","");
            case 4:
                FeedBack feedBack = new FeedBack();
                return feedBack;
            case 5:
                return About.newInstance("","");
//            case 6:
//                FeedBack feedBack1 = new FeedBack();
//                return feedBack1;
//            case 7:
//                return ShowStaffs.newInstance(user_id,"");
//            case 8:
//                FeedBack feedBack2 = new FeedBack();
//                return feedBack2;
//            case 9:
//                return Services.newInstance(user_id,"");
//            case 10:
//                return About.newInstance("","");

//            case 11:
//                FeedBack feedBack = new FeedBack();
//                return feedBack;

            default:
                return DashBoard.newInstance(user_id,"");
        }
    }

    private void selectNavMenu() {
            navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }
    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigationView.getMenu().getItem(0).setChecked(true);
        final DrawerLayout myDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        myDrawer.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                myDrawer.openDrawer(Gravity.LEFT);
//            }
//        }, 500);

    }

    private void initLayout() {
        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        headerName = (TextView) header.findViewById(R.id.header_text_name);
        headerEmail = (TextView) header.findViewById(R.id.header_text_email);
        logout = (ImageView) header.findViewById(R.id.img_logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                            try {
                // log out from facebook
                if (LoginManager.getInstance() != null)
                    LoginManager.getInstance().logOut();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                FirebaseCrash.report(new Exception(e.getMessage()));
            }
                SharedPreferences preferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

            finish();

            }
        });


        if(readDataFromPref)
        {
            SharedPreferences prefs = getSharedPreferences(AppController.PREF_NAME, MODE_PRIVATE);
            String username = prefs.getString("username", null);
            String email = prefs.getString("email", null);
            user_id = prefs.getString("user_id", null);
            headerEmail.setText(email);
            headerName.setText(username);
        }
        else {
            if (AppController.userArrayList != null && AppController.userArrayList.size() > 0) {
                Log.d(TAG, "initLayout: " + AppController.userArrayList.get(0).getFirst_name().toString());
                headerName.setText(AppController.userArrayList.get(0).getFirst_name().toString());
                headerEmail.setText(AppController.userArrayList.get(0).getEmail().toString());
                user_id = AppController.userArrayList.get(0).getId().toString();
            }
        }


        Log.d(TAG, "initLayout: User id "+user_id);

        //// set user id or company Id for further usage////
        AddAppointment.getInstance().setCompany_id(user_id);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please press again to Exit", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);


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
//        if (id == R.id.action_save) {
//            Log.d(TAG, "onOptionsItemSelected: text is ");
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            navItemIndex = 0;
            CURRENT_TAG = AppController.TAG_APPOINTMENT ;
        }
        if (id == R.id.nav_booking) {
            navItemIndex = 1;
            CURRENT_TAG = AppController.TAG_BOOKING ;
        } else if (id == R.id.nav_reviews) {
            navItemIndex = 2;
            CURRENT_TAG = AppController.TAG_REVIEW ;
        } else if (id == R.id.nav_customer) {
            navItemIndex = 3;
            CURRENT_TAG = AppController.TAG_CUSTOMERS;
        }
//        } else if (id == R.id.nav_cuopon) {
//            navItemIndex = 3;
//            CURRENT_TAG = AppController.TAG_CUOPON ;
//        } else if (id == R.id.nav_businessDetails) {
//            navItemIndex = 4;
//            CURRENT_TAG = AppController.TAG_BUSINESS_DETAILS ;
//        } else if (id == R.id.nav_privacy) {
//            navItemIndex = 5;
//            CURRENT_TAG = AppController.TAG_PRIVACY ;
//        }
//        else if (id == R.id.nav_staff) {
//            navItemIndex = 6;
//            CURRENT_TAG = AppController.TAG_STAFF ;
//        }
//        else if (id == R.id.nav_resources) {
//            navItemIndex = 7;
//            CURRENT_TAG = AppController.TAG_RESOURCES ;
//        }
//        else if (id == R.id.nav_services) {
//            navItemIndex = 8;
//            CURRENT_TAG = AppController.TAG_SERVICES ;
//        }
        else if (id == R.id.nav_feedback) {
            navItemIndex = 4;
            CURRENT_TAG = AppController.TAG_FEEDBACK ;
        }
        else if (id == R.id.nav_about) {
            navItemIndex = 5;
            CURRENT_TAG = AppController.TAG_ABOUT ;
        }

       else {
                navItemIndex = 0;
        }


        loadHomeFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
