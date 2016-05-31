package su.moy.chernihov.mapapplication;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;


public class MapAZSFragment extends SupportMapFragment {
    public static final String TAG = "AZS";
    CameraPosition cameraPosition;
    GoogleMap thisMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getMapAsync((OnMapReadyCallback) getActivity());
        Log.d(TAG, "MapAZSFragment onCreate");
    }

    @Override
    public void onPause() {
        super.onPause();
        thisMap = ((AzsFragmentsActivity) getActivity()).getMap();
        if (thisMap != null) {
            cameraPosition = thisMap.getCameraPosition();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (thisMap != null && cameraPosition != null)
            thisMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom));

        ((AzsFragmentsActivity) getActivity()).getBtnShowList().setVisibility(View.VISIBLE);
        RadioGroup rgFilters = ((AzsFragmentsActivity) getActivity()).getRgFilters();
        rgFilters.setVisibility(View.VISIBLE);
        /*((AzsFragmentsActivity)getActivity()).getBtnShowList().setVisibility(View.VISIBLE);
        GoogleMap thisMap = ((AzsFragmentsActivity)getActivity()).getMap();
        Location location = ((AzsFragmentsActivity)getActivity()).getLocation();
        RadioGroup rgFilters = ((AzsFragmentsActivity)getActivity()).getRgFilters();
        rgFilters.setVisibility(View.VISIBLE);
        if (thisMap != null && location != null) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            switch (rgFilters.getCheckedRadioButtonId()) {
                case R.id.rb_filter_5_km:
                    // Current location zoom
                    thisMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, AzsFragmentsActivity.FIVE_KM_ZOOM));
                    break;

                case R.id.rb_filter_10_km:
                    // Current location zoom
                    thisMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, AzsFragmentsActivity.TEN_KM_ZOOM));
                    break;

                case R.id.rb_filter_20_km:
                    thisMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, AzsFragmentsActivity.TWENTY_KM_ZOOM));
                    break;

                case R.id.rb_all:
                case R.id.rb_view_all_checked:
                default:
                    thisMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, AzsFragmentsActivity.KIEV_ZOOM));
            }
        }*/
        Log.d(TAG, "MapAZSFragment onResume");
    }
}

/*


package su.moy.chernihov.mapapplication;

        import android.content.Intent;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.os.Bundle;
        import android.os.PersistableBundle;
        import android.support.v4.app.FragmentActivity;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentTransaction;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.RadioGroup;
        import android.widget.Toast;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.UiSettings;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.LatLngBounds;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import java.util.ArrayList;


public class MapAZSFragment extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private static final long POLLING_FREQ = 1000 * 10;
    private static final float MIN_DISTANCE = 1000.0f;

    private static final double FIVE_KM = 5.0d;
    private static final double TEN_KM = 10.0d;
    private static final double TWENTY_KM = 20.0d;
    private static final String RADIO_GROUP_ID = "radio button id";


    private final String TAG = "LocationGetLocationActivity";

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private Button btnShowList;
    private RadioGroup rgFilters;
    Location mLocation;
    private static ArrayList<AZS> currentAzsList;
    private LocationListener locationListener;
    private Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = savedInstanceState;
        setContentView(R.layout.fragment_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // create and init AZS Lab
        LabAzs labAzs = LabAzs.getInstance(this);
        labAzs.init();
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (mLocationManager == null) finish();


        rgFilters = (RadioGroup) findViewById(R.id.rg_filters);
        // checked listener for radio group filters
        rgFilters.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switchByRadioButtonId(checkedId, mLocation);
            }
        });

        btnShowList = (Button) findViewById(R.id.btn_show_azs_list);
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapAZSFragment.this, AzsFragmentsActivity.class);
                startActivity(intent);
            }
        });

        // Called back when mLocation changes
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "onLocationChanged");
                if (location != null) {
                    mLocation = location;
                    rgFilters.setVisibility(View.VISIBLE);
                    Log.d(TAG, "mLastLocation is not null");
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    //marker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in current"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    Log.d(TAG, "Location set");
                    switchByRadioButtonId(rgFilters.getCheckedRadioButtonId(), location);
                    //mLocationManager.removeUpdates(this);
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMyLocationEnabled(true);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        mMap.setOnMarkerClickListener(this);

        // жду полной ицициализации инфы
        while(!LabAzs.isInit());

        if (bundle != null) {
          *//*  mLocation = new Location("AZS");
            mLocation.setLatitude(bundle.getInt("lat"));
            mLocation.setLongitude(bundle.getInt("lng"));*//*
            rgFilters.check(bundle.getInt(RADIO_GROUP_ID));
        }
        else
            switchByRadioButtonId(rgFilters.getCheckedRadioButtonId(), mLocation);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != mLocationManager
                .getProvider(LocationManager.NETWORK_PROVIDER)) {

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, POLLING_FREQ,
                    MIN_DISTANCE, locationListener);
            Log.d(TAG,"Net provider set");
        }

        // Register for GPS mLocation updates
        if (null != mLocationManager
                .getProvider(LocationManager.GPS_PROVIDER)) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, POLLING_FREQ,
                    MIN_DISTANCE, locationListener);
            Log.d(TAG, "GPS provider set");
        }

        // Schedule a runnable to unregister mLocation listeners
       *//* Executors.newScheduledThreadPool(1).schedule(new Runnable() {
            @Override
            public void run() {

                mLocationManager.removeUpdates(locationListener);

            }
        }, MEASURE_TIME, TimeUnit.MILLISECONDS);*//*
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(RADIO_GROUP_ID, rgFilters.getCheckedRadioButtonId());
       *//* outState.putDouble("lat", mLocation.getLatitude());
        outState.putDouble("lng",mLocation.getLongitude());*//*
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(locationListener);

    }
    public static ArrayList<AZS> getCurrentAzsList() {
        return currentAzsList;
    }


    private void switchByRadioButtonId(int id, Location location) {
        ArrayList<AZS> azsList = LabAzs.getAzsList();
        currentAzsList = azsList;
        if (location != null) {
            switch (id) {
                case R.id.rb_filter_5_km:
                    replaceAZSListByMap(filterAZSListByRadius(azsList, location, FIVE_KM), 13);
                    break;

                case R.id.rb_filter_10_km:
                    replaceAZSListByMap(filterAZSListByRadius(azsList, location, TEN_KM), 11);
                    break;

                case R.id.rb_filter_20_km:
                    replaceAZSListByMap(filterAZSListByRadius(azsList, location, TWENTY_KM), 10);
                    break;

                case R.id.rb_all:
                default:
                    replaceAZSListByMap(azsList, 8);
            }
        }
        else {
            replaceAZSListByMap(azsList, 8);
            Toast.makeText(MapAZSFragment.this, getString(R.string.toast_not_location), Toast.LENGTH_LONG).show();
        }
    }

    private void replaceAZSListByMap(ArrayList<AZS> azsList, float zoom) {
        if (azsList != null && mMap != null) {
            // clear all marker on map
            mMap.clear();
            for(AZS azs: azsList) {
                // Add a marker in azs and move the camera
                LatLng currentLocation = new LatLng(azs.getLatitude(), azs.getLongitude());

                Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title(azs.getBrandName())
                                .icon(BitmapDescriptorFactory.fromResource(azs.getIconRes()))
                );
                azs.setMarker(marker);
            }
            // Kiev region Zoom
            LatLngBounds kievObl = new LatLngBounds(
                    new LatLng(50, 30), new LatLng(51, 31));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kievObl.getCenter(), zoom));
        }
    }



    private ArrayList<AZS> filterAZSListByRadius(ArrayList<AZS> azsList, Location location, double radius) {
        ArrayList<AZS> resultAZSList = new ArrayList<>();
        for(AZS azs: azsList) {
            // gps coordinate AZS
            double latZ = azs.getLatitude();
            double lngZ = azs.getLongitude();
            //gps coordinate User (current mLocation)
            double latU = location.getLatitude();
            double lngU = location.getLongitude();

            if (Math.abs(measure(latZ, lngZ, latU, lngU)) <= radius) {
                resultAZSList.add(azs);
            }
        }
        currentAzsList = resultAZSList;
        return resultAZSList;
    }

    // return distance between two points in km
    private double measure(double lat1, double lon1, double lat2, double lon2) {  // generally used geo measurement function
        double radius = 6378.137; // Radius of earth in KM
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = radius * c;
        return d ; // km
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(AZS azs: currentAzsList) {
            if (azs.getMarker().equals(marker)) {
                *//*FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AZSFragment azsFragment = new AZSFragment(azs);
                fragmentTransaction.replace(R.id.azs_container, azsFragment).addToBackStack(null)
                        .commit();*//*
                Toast.makeText(MapAZSFragment.this, azs.getAddress(), Toast.LENGTH_LONG).show();
            }
        }
        return false;
    }
}*/
