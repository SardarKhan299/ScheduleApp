package com.schedulix;

import android.view.View;

import com.schedulix.ModelClasses.City;
import com.schedulix.ModelClasses.Country;
import com.schedulix.ModelClasses.Customer;
import com.schedulix.ModelClasses.Region;
import com.schedulix.ModelClasses.Staff;

import java.util.ArrayList;

/**
 * Created by Nsol on 5/8/2017.
 */

public interface ItemClickWithArray {
    public void onClickWithArray(View view, int position , ArrayList<Country> arrayList);

    public interface ItemClickWithArrayR {
        public void onClickWithArray(View view, int position , ArrayList<Region> arrayList);
    }
    public interface ItemClickWithArrayC {
        public void onClickWithArray(View view, int position , ArrayList<City> arrayList);
    }
    public interface ItemClickWithArrayS {
        public void onClickWithArray(View view, int position , ArrayList<Staff> arrayList);
    }
    public interface ItemClickWithArrayCu {
        public void onClickWithArray(View view, int position , ArrayList<Customer> arrayList);
    }
}


