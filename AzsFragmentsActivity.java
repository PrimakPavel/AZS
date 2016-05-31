package su.moy.chernihov.mapapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class AzsFragmentsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener{
    protected static final double FIVE_KM = 5;
    protected static final float FIVE_KM_ZOOM = 13;
    protected static final double TEN_KM = 10;
    protected static final float TEN_KM_ZOOM = 11;
    protected static final double TWENTY_KM = 20;
    protected static final float TWENTY_KM_ZOOM = 10;
    protected static final float KIEV_ZOOM = 9;

    private static final long POLLING_FREQ = 1000 * 10;
    private static final float MIN_DISTANCE = 1000.0f; // минимальная дистанция в метрах
    private static final String TAG = "AZS";
    private static final String RADIO_GROUP_CHECKED_ID = "radio group checked id";


    private LocationManager mLocationManager;
    //private ArrayList<AZS> currentAzsList;
    private Button btnShowList;
    private RadioGroup rgFilters;
    private RadioButton rbAll, rbViewChecked, rb5Km, rb10Km, rb20Km;
    private LocationListener locationListener;
    private GoogleMap mMap;
    private Location mLocation;
    private Bundle bundle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_map);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // инициализирую менеджер локации и слушателя изменения локации
        locationManagerAndListenerInit();
        bundle = savedInstanceState;
                // если нет сохраненных данных, то фрагмент КАРТА добавляется в контейнер
        if (savedInstanceState == null) { // очень нужная проверка!!!!! При поворотах экрана новый фрагмент не создается
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = new MapAZSFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
        initButtonListeners();
        // инициализирую радио группу и ее слушателя
        rgFilters = (RadioGroup) findViewById(R.id.rg_filters);

        if (savedInstanceState != null)
            rgFilters.check(savedInstanceState.getInt(RADIO_GROUP_CHECKED_ID));


        // инициализирую кнопку и ее слушателя
        btnShowList = (Button) findViewById(R.id.btn_show_azs_list);
        // если кнопка нажата, то открываю Лист заправок
        btnShowList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                Fragment fragment = new AZSListFragment();
                fm.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        Log.d(TAG, "AZSFragmentActivity onCreate");
    }

    private void initButtonListeners() {
        rbAll = (RadioButton) findViewById(R.id.rb_all);
        rbAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LabAzs.isInit())
                switchByRadioButtonId(rbAll.getId(), mLocation);
            }
        });

        rb5Km = (RadioButton) findViewById(R.id.rb_filter_5_km);
        rb5Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LabAzs.isInit())
                switchByRadioButtonId(rb5Km.getId(), mLocation);
            }
        });
        rb10Km = (RadioButton) findViewById(R.id.rb_filter_10_km);
        rb10Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LabAzs.isInit())
                switchByRadioButtonId(rb10Km.getId(), mLocation);
            }
        });
        rb20Km = (RadioButton) findViewById(R.id.rb_filter_20_km);
        rb20Km.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LabAzs.isInit())
                switchByRadioButtonId(rb20Km.getId(), mLocation);
            }
        });
        rbViewChecked = (RadioButton) findViewById(R.id.rb_view_all_checked);
        rbViewChecked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(LabAzs.isInit())
                    switchByRadioButtonId(rbViewChecked.getId(), mLocation);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        // инициализирую провайдера для локации
        locationProviderInit();
        Log.d(TAG, "AZSFragmentActivity onResume");

    }


    @Override
    public void onPause() {
        super.onPause();
        // деинициализирую провайдера для локации
        locationProviderRemove();
        Log.d(TAG, "AZSFragmentActivity onPause");
    }

    // когда карта готова к использованию
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // тип карты классическая
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        // опредиления локации на карте разрешить
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            mMap.setMyLocationEnabled(true);
        }
        // настройки карты
        UiSettings uiSettings = mMap.getUiSettings();
        // компас отключен
        uiSettings.setCompassEnabled(false);
        // зум включен
        uiSettings.setZoomControlsEnabled(true);
        // кнопка найти текущую локацию включена
        uiSettings.setMyLocationButtonEnabled(true);
        // добавлен слушатель нажатия на Маркеры на карте
        mMap.setOnMarkerClickListener(this);
        //  инициализирую список всех заправок при вервом запуске
        if (bundle == null) {
            LabAzs labAzs = LabAzs.getInstance(this);
            labAzs.init();
        }
        else {
            if(LabAzs.isInit())
                switchByRadioButtonId(rgFilters.getCheckedRadioButtonId(), mLocation);
        }
        Log.d(TAG, "AZSFragmentActivity onMapReady");
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for(AZS azs: LabAzs.getCurrentAzsList()) {
            if (azs.getMarker().equals(marker)) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                AZSFragment azsFragment = new AZSFragment(azs);
                fragmentTransaction
                        .replace(R.id.fragment_container, azsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
        Log.d(TAG, "AZSFragmentActivity onMarkerClick");
        return false;
    }



    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(RADIO_GROUP_CHECKED_ID, rgFilters.getCheckedRadioButtonId());
        Log.d(TAG, "AZSFragmentActivity onSaveInstanceState");
    }


   /* // возвращает текущий лист заправок
    public ArrayList<AZS> getCurrentAzsList() {
        return currentAzsList;
    }*/

    public RadioGroup getRgFilters() {
        return rgFilters;
    }

    public Button getBtnShowList() {
        return btnShowList;
    }

    public GoogleMap getMap() {
        return mMap;
    }

    public Location getLocation() {
        return mLocation;
    }


    // в зависимоти от выбраного пункта отфильтровать данные и вывести маркеры на карту
    private void switchByRadioButtonId(int id, Location location) {
        // взять весь лист
        ArrayList<AZS> azsList = LabAzs.getAzsList();
        if (location != null) {
            switch (id) {
                case R.id.rb_filter_5_km:
                    replaceAZSListByMap(filterAZSListByRadius(azsList, location, FIVE_KM), FIVE_KM_ZOOM, location);
                    break;

                case R.id.rb_filter_10_km:
                    replaceAZSListByMap(filterAZSListByRadius(azsList, location, TEN_KM), TEN_KM_ZOOM, location);
                    break;

                case R.id.rb_filter_20_km:
                    replaceAZSListByMap(filterAZSListByRadius(azsList, location, TWENTY_KM), TWENTY_KM_ZOOM, location);
                    break;
                case R.id.rb_view_all_checked:
                    ArrayList<AZS> highLightList = new ArrayList<>();
                    for (AZS azs: azsList){
                        if (azs.isHighlight()){
                            highLightList.add(azs);
                        }
                    }
                    replaceAZSListByMap(highLightList, KIEV_ZOOM, location);
                    break;

                case R.id.rb_all:
                default:
                    replaceAZSListByMap(azsList, KIEV_ZOOM, location);
            }
        }
        else {
            {
            replaceAZSListByMap(azsList, KIEV_ZOOM, null);
            rgFilters.check(R.id.rb_all);
            Toast.makeText(AzsFragmentsActivity.this, getString(R.string.toast_no_location), Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(AzsFragmentsActivity.this, getString(R.string.toast_no_location), Toast.LENGTH_LONG).show();
        }
    }

    private void replaceAZSListByMap( final ArrayList<AZS> azsList, float zoom, Location location) {
        if (azsList != null && mMap != null) {
            // обновляю текущий лист
            LabAzs.setCurrentAzsList(azsList);
            // clear all marker on map
            mMap.clear();
            // добавить маркеры из листа заправок
            for(AZS azs: azsList) {
                // Add a marker in azs and move the camera
                LatLng currentLocation = new LatLng(azs.getLatitude(), azs.getLongitude());
                int iconRes = azs.getIconRes();
                /*if (azs.isHighlight()) {
                    iconRes = R.drawable.ic_local_gas_station_black_24dp;
                }*/
                Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title(azs.getBrandName())
                                .icon(BitmapDescriptorFactory.fromResource(iconRes))
                );
                azs.setMarker(marker);
            }
            // если текущее местонахождение не известно, то показать Киев на карте
            if (location == null) {
                // Kiev region Zoom
                LatLngBounds kievObl = new LatLngBounds(
                        new LatLng(50, 30), new LatLng(51, 31));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kievObl.getCenter(), zoom));
            }
            // если текущее местопложение известно, то показать область текущего местоположения
            else {
                // Current location zoom
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoom));
            }
        }
    }

    // обратный вызов для LAbAZS. Добавляет маркеры на карту после инициализации всех заправок
    protected void callBackAddAZSMarkers(final ArrayList<AZS> azsLists)
    {
        LabAzs.setCurrentAzsList(azsLists);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                replaceAZSListByMap(azsLists, KIEV_ZOOM, mLocation);
            }
        });

    }

    // фильтрует все заправки по радиусу от текущего места
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

        return resultAZSList;
    }

    // return distance between two points in km
    public double measure(double lat1, double lon1, double lat2, double lon2) {  // generally used geo measurement function
        double radius = 6378.137; // Radius of earth in KM
        double dLat = (lat2 - lat1) * Math.PI / 180;
        double dLon = (lon2 - lon1) * Math.PI / 180;
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return radius * c;
       // km
    }



////////////////////////////////////////////////////// LOCATION ///////////////////////
    private void locationManagerAndListenerInit() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (mLocationManager == null) finish();
        // Called back when mLocation changes
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, "onLocationChanged");
                if (location != null) {
                    mLocation = location;
                    //rgFilters.setVisibility(View.VISIBLE);
                    Log.d(TAG, "mLastLocation is not null");
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
                    Log.d(TAG, "Location set");
                    if (LabAzs.isInit())
                        switchByRadioButtonId(rgFilters.getCheckedRadioButtonId(), location);

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

    private void locationProviderInit() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (null != mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER, POLLING_FREQ,
                            MIN_DISTANCE, locationListener);
                    Log.d(TAG, "Net provider set");
                }


                // Register for GPS mLocation updates
                if (null != mLocationManager
                        .getProvider(LocationManager.GPS_PROVIDER)) {
                    mLocationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, POLLING_FREQ,
                            MIN_DISTANCE, locationListener);
                    Log.d(TAG, "GPS provider set");
                }
            }
        } else {
            //Not in api-23, no need to prompt
            if (null != mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER)) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, POLLING_FREQ,
                        MIN_DISTANCE, locationListener);
                Log.d(TAG, "Net provider set");
            }


            // Register for GPS mLocation updates
            if (null != mLocationManager
                    .getProvider(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, POLLING_FREQ,
                        MIN_DISTANCE, locationListener);
                Log.d(TAG, "GPS provider set");
            }
        }


    }

    private void locationProviderRemove() {

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.removeUpdates(locationListener);
            }
        } else {
            //Not in api-23, no need to prompt
            mLocationManager.removeUpdates(locationListener);
        }

    }

////////////////////////////PERMISSIONS CONTROL//////////////////////////////////////////////////////////////
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],@NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }

        }
    }


}
