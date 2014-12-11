package me.xbt.wearlocation;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import java.util.Date;

public class WatchActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String TAG = "WatchActivity";
    private static final long UPDATE_INTERVAL_MS = 5000L;
    private static final long FASTEST_INTERVAL_MS = 3000L;

    private GoogleApiClient mGoogleApiClient;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        if (hasGps()) {
            Log.d(TAG, "this hardware has gps.");
        } else {
            Log.d(TAG, "This hardware doesn't have GPS.");
            // Fall back to functionality that does not use location or
            // warn the user that location function is not available.
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        //mGoogleApiClient.connect();

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Log.d(TAG, "last location=" + location);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mGoogleApiClient.connect(); // remember to connect()
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi
                    .removeLocationUpdates(mGoogleApiClient, this);
        }
        mGoogleApiClient.disconnect();
    }

    private boolean hasGps() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onconnected");

        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_INTERVAL_MS);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                .setResultCallback(new ResultCallback<Status>() {

                    @Override
                    public void onResult(Status status) {
                        if (status.getStatus().isSuccess()) {
                            //if (Log.isLoggable(TAG, Log.DEBUG)) {
                                Log.d(TAG, "Successfully requested location updates.  status=" + status);
                                // do something
                            //}
                        } else {
                            Log.e(TAG,
                                    "Failed in requesting location updates, "
                                            + "status code: "
                                            + status.getStatusCode()
                                            + ", message: "
                                            + status.getStatusMessage());
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "connection to location client suspended");
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "location changed: " + location);
        mTextView.setText("location changed: " + location + "\ntime: " + new Date());
        //addLocationEntry(location.getLatitude(), location.getLongitude());
        // do something
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.d(TAG, "connection failed.  result: " + result);
    }
}
