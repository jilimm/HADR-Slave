package com.example.jingyun.hdarchallenge.Fragments;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.jingyun.hdarchallenge.Items.GoogleLocationEngine;
import com.example.jingyun.hdarchallenge.R;

import com.google.android.gms.common.api.GoogleApiClient;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.services.android.telemetry.location.LocationEngine;
import com.mapbox.services.android.telemetry.location.LocationEngineListener;
import com.mapbox.services.android.telemetry.location.LocationEnginePriority;
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
public class NavigationFragment extends Fragment implements LocationEngineListener,PermissionsListener, GoogleApiClient. {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private MapView mapView;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private LocationEngine locationEngine;
    private MapboxMap map;
    private LatLng destinationCoord;
    private Button loadBttn;
    private GoogleApiClient mGoogleApiClient;
    private Location originLocation;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks()


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
        getActivity().setTitle("Navigation");

        //setting up things in xml file
        mapView = (MapView) rootView.findViewById(R.id.map_mapView);
        loadBttn = (Button) rootView.findViewById(R.id.map_load_bttn);

        //setting up mapbox map
        Mapbox.getInstance(getActivity(), getString(R.string.mapbox_access_token));
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                //setting up initial style of the map
                map = mapboxMap;
                //TODO: not displaying my current location. check the on Start and the xml
                enableLocationPlugin();

                mapboxMap.setStyleUrl(Style.SATELLITE);
                destinationCoord = new LatLng(40.73581,-73.99155);
                mapboxMap.addMarker(new MarkerOptions()
                        .position(destinationCoord)
                        .title("Destination")
                        .snippet(String.valueOf(destinationCoord.getLatitude())+","
                                +String.valueOf(destinationCoord.getLongitude())+","
                                +String.valueOf(destinationCoord.getAltitude())));
            }

        });



        //setting up the button
        loadBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Retreiving Satellite Image", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    //mapbox methods

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(getActivity())) {
            // Create an instance of LOST location engine
            initializeLocationEngine();
            locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationLayerPlugin.setLocationLayerEnabled(LocationLayerMode.TRACKING);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());

        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {
        //locationEngine = AndroidLocationEngine.getLocationEngine(getActivity());
        //locationEngine = GoogleLocationEngine.getLocationEngine(getActivity());
        locationEngine = GoogleLocationEngine.getLocationEngine(getActivity());
        Log.i("NavigationFrag", "1" );

        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        Log.i("NavigationFrag", "2" );

        locationEngine.activate();
        Log.i("NavigationFrag", "3" );

        Location lastLocation = locationEngine.getLastLocation();

        Log.i("NavigationFrag", "test" );
        Log.i("NavigationFrag", "location_null "+String.valueOf(lastLocation==null) );
        if (lastLocation != null) {
            Log.i("NavigationFrag", lastLocation.toString() );
            Log.i("NavigationFrag", "here");

            Toast.makeText(getActivity(), "lastLocation is null", Toast.LENGTH_SHORT).show();
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
        } else {
            Log.i("NavigationFrag", "here1");
            Toast.makeText(getActivity(), String.valueOf(lastLocation.getLatitude())+" , "+String.valueOf(lastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            Toast.makeText(getActivity(), "lastLocation found", Toast.LENGTH_SHORT).show();
            locationEngine.addLocationEngineListener(this);
            setCameraPosition(lastLocation);
        }

    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
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
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onStart() {
        super.onStart();
        if (locationEngine != null) {
            Log.i("NavFrag","heree1");
            locationEngine.requestLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            Log.i("NavFrag","heree2");

            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (locationEngine != null) {
            Log.i("NavFrag","heree3");

            locationEngine.removeLocationUpdates();
        }
        if (locationLayerPlugin != null) {
            Log.i("NavFrag","heree4");

            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            Log.i("NavFrag","heree5");

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





}
