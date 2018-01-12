package com.example.jingyun.hdarchallenge.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jingyun.hdarchallenge.Items.Weather;
import com.example.jingyun.hdarchallenge.R;

//imports used for getting navigation route
import com.mapbox.geojson.Point;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.commons.models.Position;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//imports used to instantiate map in mapbox and track current user location
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
import com.mapbox.services.android.telemetry.location.LostLocationEngine;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;
import com.mapbox.services.android.telemetry.permissions.PermissionsManager;


import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NavigationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NavigationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

//TODO: have alert dialog if GPS not on, if can customize the layers


public class NavigationFragment extends Fragment implements LocationEngineListener, PermissionsListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    //Creating view of fragment
    private MapView mapView;
    private MapboxMap map;
    private LatLng destinationCoord;
    private Button navigationBttn;
    private TextView etaText;
    private ImageView weatherIc;
    private Weather weatherType;

    //things needed to get and update user current location
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    public Location currentLocation; //will be determine by locationEngine

    //things needed to create and draw the navigation route
    private Position originPosition;
    private Position destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    public NavigationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NavigationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NavigationFragment newInstance(String param1, String param2) {
        NavigationFragment fragment = new NavigationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: check if the user has switched on GPS

        //setting up destination information
        destinationCoord = new LatLng(1.3314,103.9477); //latitude longitude is provided by master app

        //TODO: obtain from firebase
        weatherType = Weather.RAIN;

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        getActivity().setTitle("Navigation");

        //setting up things in xml file
        mapView = (MapView) rootView.findViewById(R.id.map_mapView);
        navigationBttn = (Button) rootView.findViewById(R.id.map_start_nav_bttn);
        etaText = (TextView) rootView.findViewById(R.id.map_notes) ;
        weatherIc = (ImageView) rootView.findViewById(R.id.map_weather_logo);


        if (weatherType==Weather.CLOUDY){
            weatherIc.setBackgroundResource(R.mipmap.weather_cloudy);
        }else if(weatherType==Weather.RAIN){
                weatherIc.setBackgroundResource(R.mipmap.weather_storm_rainy);
        } else if(weatherType==Weather.SNOW){
            weatherIc.setBackgroundResource(R.mipmap.weather_storm_snowy);
        } else if(weatherType==Weather.SUNNY){
            weatherIc.setBackgroundResource(R.mipmap.weather_sunny);
        }



        //setting up the map
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                //setting up initial style of the map
                map = mapboxMap;
                mapboxMap.setStyleUrl(Style.SATELLITE_STREETS);
                mapboxMap.addMarker(new MarkerOptions()
                        .position(destinationCoord)
                        .title("Destination")
                        .snippet(String.valueOf(destinationCoord.getLatitude())+","
                                +String.valueOf(destinationCoord.getLongitude())+","
                                +String.valueOf(destinationCoord.getAltitude())));
                enableLocationPlugin(); //get the user's current location and destination, zooms camera to appropriate level
            }
        });

        //setting up the button
        navigationBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //to launch navigation mode
                if (currentRoute!=null){
                    //navigation = new MapboxNavigation(getActivity(),getString(R.string.mapbox_access_token));
                    NavigationViewOptions options = NavigationViewOptions.builder()
                            .directionsRoute(currentRoute)
                            .awsPoolId(null)
                            .shouldSimulateRoute(false) //the app wont tell you how to walk there
                            .build();
                    NavigationLauncher.startNavigation(getActivity(),options);
                    // Call this method with Context from within an Activity
                }else{
                    Toast.makeText(getActivity(), "Please wait for current location to de detected", Toast.LENGTH_SHORT).show();
                    Log.i("Navigation Frag","current location null2");
                }
            }
        });

        return rootView;
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            //TODO: ensure marker displays user information
            locationPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = new LostLocationEngine(getActivity());
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            currentLocation = lastLocation;
            Log.i("NavigationFrag", "DETECTED"+currentLocation.toString());
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void setCameraPosition(Location location) {
        LatLngBounds zoomBound = new LatLngBounds.Builder()
                .include(destinationCoord)
                .include(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
                .build();
        map.easeCamera(CameraUpdateFactory.newLatLngBounds(zoomBound,100));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            getActivity().finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        Log.i("NavigationFrag","connected");
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            //Log.i("NavigationFrag","locationChanged");
            currentLocation = location;
            setCameraPosition(location);
            destinationPosition = Position.fromCoordinates(destinationCoord.getLatitude(),destinationCoord.getLongitude());
            originPosition = Position.fromCoordinates(currentLocation.getLatitude(),currentLocation.getLongitude());
            getRoute(originPosition,destinationPosition);
            locationEngine.removeLocationEngineListener(this);
            Log.i("NavigationFrag","route obtained");
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    //used to get Route

    private void getRoute(Position origin, Position destination) {
        NavigationRoute.builder()
                .accessToken(Mapbox.getAccessToken())
                .origin(Point.fromLngLat(origin.getLatitude(),origin.getLongitude(),origin.getAltitude()))
                .destination(Point.fromLngLat(destination.getLatitude(),destination.getLongitude(),destination.getAltitude()))
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            Log.e(TAG, "navigationMapRoute!=null");
                            navigationMapRoute.removeRoute();
                        } else {
                            Log.e(TAG, "make new NavigationMapRoute");
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map);
                        }
                        Log.e(TAG, "addingRoute");
                        navigationMapRoute.addRoute(currentRoute);
                        Log.e(TAG, "route added");
                        Log.e(TAG, currentRoute.toString());
                        Double min = currentRoute.duration()/60;
                        Double sec = currentRoute.duration()%60;
                        etaText.setText("ETA :"+String.valueOf(Integer.valueOf(min.intValue()))+"min "+String.valueOf(sec.intValue())+"sec"+"\n"+
                                        "Distance: "+String.valueOf(currentRoute.distance()/1000)+"km"+"\n"+
                                        "Weather Expected: ");


                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.i(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }




}
