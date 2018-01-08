package com.example.jingyun.hdarchallenge.Items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;

import java.lang.ref.WeakReference;
/**
 * Created by Jing Yun on 8/1/2018.
 */


public class GoogleLocationEngine extends LocationEngine implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private static final String LOG_TAG = GoogleLocationEngine.class.getSimpleName();

    private static LocationEngine instance;

    private WeakReference<Context> context;
    private GoogleApiClient googleApiClient;

    public GoogleLocationEngine(Context context) {
        super();
        this.context = new WeakReference<>(context);
        googleApiClient = new GoogleApiClient.Builder(this.context.get())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    public static synchronized LocationEngine getLocationEngine(Context context) {
        if (instance == null) {
            instance = new GoogleLocationEngine(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void activate() {
        Log.i("location engine","activate");
        if (googleApiClient != null && !googleApiClient.isConnected()) {
            Log.i("location engine","activate2");
            Log.i("location engine","googleAPI"+String.valueOf(googleApiClient!=null));
            Log.i("location engine","googleAPInoconnect"+String.valueOf(!googleApiClient.isConnected()));
            googleApiClient.connect();
            //TODO: google API client is attempting to connect
            Log.i("location engine","googleAPInull"+String.valueOf(googleApiClient==null));
            Log.i("location engine","googleAPIconnecting "+String.valueOf(googleApiClient.isConnecting()));

        }

    }

    @Override
    public void deactivate() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public boolean isConnected() {
        return googleApiClient.isConnected();
    }



    @SuppressLint("MissingPermission")
    @Override
    public Location getLastLocation() {
        if (googleApiClient.isConnected()) {
            //noinspection MissingPermission
            Log.i("location Engine","here2");
            return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        Log.i("location Engine",String.valueOf(googleApiClient.isConnected()));
        Log.i("location Engine","here1");
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates() {
        // Common params
        LocationRequest request = LocationRequest.create()
                .setFastestInterval(1000)
                .setSmallestDisplacement(3.0f);

        // Priority matching is straightforward
        if (priority == LocationEnginePriority.NO_POWER) {
            request.setPriority(LocationRequest.PRIORITY_NO_POWER);
        } else if (priority == LocationEnginePriority.LOW_POWER) {
            request.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        } else if (priority == LocationEnginePriority.BALANCED_POWER_ACCURACY) {
            request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        } else if (priority == LocationEnginePriority.HIGH_ACCURACY) {
            request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }

        if (googleApiClient.isConnected()) {
            //noinspection MissingPermission
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeLocationUpdates() {
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

    @Override
    public Type obtainType() {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        for (LocationEngineListener listener : locationListeners) {
            listener.onLocationChanged(location);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("LOCATIONENG","onConnected");
        for (LocationEngineListener listener : locationListeners) {
            listener.onConnected();
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("LOCATIONENG","onConnectSus");
        Log.d(LOG_TAG, "Connection suspended: " + cause);

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("LOCATIONENG","onCnntFail");
        Log.d(LOG_TAG, "Connection failed:" + connectionResult.getErrorMessage());

    }
}
