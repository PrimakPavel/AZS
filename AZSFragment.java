package su.moy.chernihov.mapapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class AZSFragment extends Fragment {
    public static final String TAG = "AZS";
    private AZS azs;
    protected TextView tvBrandName, tvAddress, tvTelephone, tvPrice, tvMinPrice, tvMaxPrice;
    protected ImageView ivIcon;
    protected ImageButton btnCall;
    protected Button btnAddRating;
    protected CheckBox cbHighLightOnMap;
    public AZSFragment(AZS azs) {
        this.azs = azs;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        setRetainInstance(true);
        Log.d(TAG, "AZSFragment onCreate");
    }

    @Override
    public void onPause() {
        super.onPause();
        ((AzsFragmentsActivity)getActivity()).callBackAddAZSMarkers(LabAzs.getCurrentAzsList());
        Log.d(TAG, "AZSFragment onPause");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AZSFragment onCreateView");
        // получаю объект вью
        View v = inflater.inflate(R.layout.fragment_azs, container, false);
        // название заправки
        tvBrandName = (TextView)v.findViewById(R.id.tv_azs_fragment_brand_name);
        tvBrandName.setText(azs.getBrandName());
        // логотип заправки
        ivIcon = (ImageView)v.findViewById(R.id.iv_azs_fragment_brand_icon);
        ivIcon.setImageDrawable(getResources().getDrawable(azs.getIconRes()));
        // адресс заправки
        tvAddress = (TextView)v.findViewById(R.id.tv_azs_fragment_address);
        tvAddress.setText(azs.getAddress());
        // номер телефона
        tvTelephone = (TextView)v.findViewById(R.id.tv_azs_fragment_telephone);
        tvTelephone.setText(azs.getTelephoneNumber());
        // кнопка позвонить по номеру телефона
        btnCall = (ImageButton) v.findViewById(R.id.btn_azs_fragment_call);
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + azs.getTelephoneNumber().trim()));
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //User has previously accepted this permission
                    if (ActivityCompat.checkSelfPermission(getContext(),
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(callIntent);
                    }
                } else {
                    //Not in api-23, no need to prompt
                    startActivity(callIntent);
                }

            }
        });
        // прайс по этой заправке
        tvPrice = (TextView)v.findViewById(R.id.tv_azs_fragment_price);
        tvPrice.setText(azs.getOilPrice());
        //Минимальный прайс по всем заправкам Города
        tvMinPrice = (TextView)v.findViewById(R.id.tv_azs_fragment_minimal_price);
        tvMinPrice.setText(Price.getPrice(Price.MINIMAL_PRICE));
        // Максимальный прайс по всем заправкам Города
        tvMaxPrice = (TextView)v.findViewById(R.id.tv_azs_fragment_maximal_price);
        tvMaxPrice.setText(Price.getPrice(Price.MAXIMAL_PRICE));
        btnAddRating = (Button) v.findViewById(R.id.btn_azs_fragment_rating);
        btnAddRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                PostFragment fragment = new PostFragment(azs.getId());
                fm.beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        cbHighLightOnMap = (CheckBox) v.findViewById(R.id.cb_azs_fragment_view_azs_in_map);
        if (azs.isHighlight()){
            cbHighLightOnMap.setChecked(true);
        }
        cbHighLightOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                azs.setHighlight(cbHighLightOnMap.isChecked());
            }
        });


    return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AzsFragmentsActivity)getActivity()).getBtnShowList().setVisibility(View.INVISIBLE);
        ((AzsFragmentsActivity)getActivity()).getRgFilters().setVisibility(View.INVISIBLE);
        Log.d(TAG, "AZSFragment onResume");
    }

//////////////Permission CALL

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 110;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CALL_PHONE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
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
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getContext(), "permission ok", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getContext(), "permission denied", Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
