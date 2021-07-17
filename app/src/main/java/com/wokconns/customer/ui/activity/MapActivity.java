package com.wokconns.customer.ui.activity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.wokconns.customer.R;
import com.wokconns.customer.databinding.ActivityMapBinding;
import com.wokconns.customer.dto.LocationDTO;
import com.wokconns.customer.https.HttpsRequest;
import com.wokconns.customer.interfacess.Consts;
import com.wokconns.customer.interfacess.Helper;
import com.wokconns.customer.network.NetworkManager;
import com.wokconns.customer.preferences.SharedPrefrence;
import com.wokconns.customer.utils.ProjectUtils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_LOCATION = 0;
    private static GoogleMap mMap;
    Marker marker;
    Timer timer = new Timer();
    boolean flag = true;
    String ar_id = "";
    private Context mContext;
    private String staffLat = "";
    private String TAG = MapActivity.class.getSimpleName();
    private String staffLongi = "";
    private int markerCount;
    private LocationDTO locationDTO;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation = new Location("test");
    Runnable mTask = new Runnable() {
        @Override
        public void run() {


            ProjectUtils.pauseProgressDialog();
            if (mGoogleApiClient.isConnected()) {
                //  startLocationUpdates();
                if (NetworkManager.isConnectToInternet(mContext)) {
                    getLocation();

                } else {
                    ProjectUtils.showToast(mContext, getResources().getString(R.string.internet_connection));
                }
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        if (NetworkManager.isConnectToInternet(mContext)) {
                            getLocation();
                        }
                    }
                }, 0, 4000);
            } else {

            }
        }
    };
    private Handler handler = new Handler();
    private SharedPrefrence prefrence;
    private ActivityMapBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        mContext = MapActivity.this;
        prefrence = SharedPrefrence.getInstance(mContext);
        if (getIntent().hasExtra(Consts.ARTIST_ID)) {
            ar_id = getIntent().getStringExtra(Consts.ARTIST_ID);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        binding.ivBack.setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(prefrence.getValue(Consts.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts.LONGITUDE))), 18);
            mMap.animateCamera(cameraUpdate);
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(prefrence.getValue(Consts.LATITUDE)), Double.parseDouble(prefrence.getValue(Consts.LONGITUDE)))).title("My Location"));
        }
        if (mMap != null) {
            handler.postDelayed(mTask, 4000);
        }
    }

    public void getLocation() {
        new HttpsRequest(Consts.GET_LOCATION_ARTIST_API, getParmlocation(), mContext).stringPost(TAG, (flag, msg, response) -> {
            if (flag) {
                try {
                    locationDTO = new Gson().fromJson(response.getJSONObject("data").toString(), LocationDTO.class);

                    staffLat = locationDTO.getLati();
                    staffLongi = locationDTO.getLongi();
                    Log.e("VarunSir", staffLat + " " + staffLongi);
                    displayLocation();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                ProjectUtils.showToast(mContext, msg);
            }
        });
    }

    public Map<String, String> getParmlocation() {

        HashMap<String, String> params = new HashMap<>();
        params.put(Consts.ARTIST_ID, ar_id);
        Log.e(TAG, params.toString());

        return params;
    }

    //Method to display the location on UI
    private void displayLocation() {
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                // Check Permissions Now
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            } else {

                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mCurrentLocation != null) {

                    double latitude = Double.parseDouble(staffLat);
                    double longitude = Double.parseDouble(staffLongi);


                    mCurrentLocation.setLatitude(Double.parseDouble(staffLat));
                    mCurrentLocation.setLongitude(Double.parseDouble(staffLongi));

                    addMarker(mMap, latitude, longitude);

                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.location_enabled),
                            Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {

        }
    }

    // Add A Map Pointer To The MAp
    public void addMarker(GoogleMap googleMap, double lat, double lon) {

        if (markerCount == 1) {
            Log.e("lat", mCurrentLocation.getLatitude() + "");
            Log.e("lat", mCurrentLocation.getLongitude() + "");

            animateMarker(mCurrentLocation, marker);


        } else if (markerCount == 0) {
            mMap = googleMap;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18);
            mMap.animateCamera(cameraUpdate);
            marker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(getResources().getString(R.string.artist_here)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));

            markerCount = 1;
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                return;
            }
            startLocationUpdates();
        }
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }


    public void animateMarker(final Location destination, final Marker marker) {
        if (marker != null) {
            final LatLng startPosition = marker.getPosition();
            final LatLng endPosition = new LatLng(destination.getLatitude(), destination.getLongitude());
            if (!startPosition.equals(endPosition)) {

                final float startRotation = marker.getRotation();


                Log.e("lat", destination.getLatitude() + " " + destination.getLongitude());
                Log.e("destination", destination.getBearing() + "");

                final LatLngInterpolator latLngInterpolator = (LatLngInterpolator) new LatLngInterpolator.LinearFixed();
                ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
                valueAnimator.setDuration(500); // duration 1 second
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(animation -> {
                    try {
                        float v = animation.getAnimatedFraction();
                        LatLng newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition);
                        marker.setPosition(newPosition);
                        marker.setAnchor(0.5f, 0.5f);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(newPosition));

                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
                                .target(newPosition)
                                .zoom(18.0f)
                                .build()));

                        marker.setRotation(getBearing(startPosition, endPosition));
                    } catch (Exception ex) {
                        // I don't care atm..
                    }
                });
                valueAnimator.start();
            }

        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.mGoogleApiClient != null) {
            this.mGoogleApiClient.connect();
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }

    private interface LatLngInterpolator {
        LatLng interpolate(float fraction, LatLng a, LatLng b);

        class LinearFixed implements LatLngInterpolator {
            @Override
            public LatLng interpolate(float fraction, LatLng a, LatLng b) {
                double lat = (b.latitude - a.latitude) * fraction + a.latitude;
                double lngDelta = b.longitude - a.longitude;
                // Take the shortest path across the 180th meridian.
                if (Math.abs(lngDelta) > 180) {
                    lngDelta -= Math.signum(lngDelta) * 360;
                }
                double lng = lngDelta * fraction + a.longitude;
                return new LatLng(lat, lng);
            }
        }
    }
}
