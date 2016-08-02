package com.example.gcs.faster5.ui.activity;

import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.gcs.faster5.R;

import java.io.IOException;
import java.util.List;

/**
 * Created by Kien on 07/25/2016. Get Location from GPS
 */
public class PopupLoginGPS extends AppCompatActivity implements LocationListener {
    EditText mEditTextLocation;
    String provider;
    Geocoder geocoder;
    LocationManager locationManager;
    TextView mTextViewLocation;
    Location location;
    Criteria criteria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        getSupportActionBar().hide();
        setContentView(R.layout.popup_connection);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .8));



        checkPermission();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        geocoder = new Geocoder(this);
        onLocationChanged(location);
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 10);
            if (addresses.size() > 0)
                this.mEditTextLocation.setText(addresses.get(0).getAddressLine(3));

        } catch (IOException e) {
            Log.e("LocateMe", "Could not get Geocoder data", e);
        }
    }

    @Override
    public void onProviderEnabled(String var1) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.slide_in_top, R.animator.slide_out_top);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this); //<7>
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1000);
            //https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
            //https://viblo.asia/bui.huu.tuan/posts/3wjAM7lBGmWe
        }
    }
    public void setLayout() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .9), (int) (height * .8));
    }
}
