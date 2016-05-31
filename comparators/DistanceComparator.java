package su.moy.chernihov.mapapplication.comparators;

import android.location.Location;

import java.util.Comparator;

import su.moy.chernihov.mapapplication.AZS;
import su.moy.chernihov.mapapplication.AzsFragmentsActivity;


//This class will sort ASZ by Distance (current place to AZS), in ascending order
public class DistanceComparator implements Comparator<AZS> {
    private AzsFragmentsActivity activity;
    public DistanceComparator(AzsFragmentsActivity activity) {
        this.activity = activity;
    }

    @Override
    public int compare(AZS azs1, AZS azs2) {
        Location location = activity.getLocation();
        if (location == null) return 0;

        // AZS1
        double azs1Longitude = azs1.getLongitude();
        double azs1Latitude = azs1.getLatitude() ;
        double distance1 = Math.abs(activity.measure(azs1Latitude, azs1Longitude, location.getLatitude(), location.getLongitude()));
        // AZS2
        double azs2Longitude = azs2.getLongitude();
        double azs2Latitude = azs2.getLatitude() ;
        double distance2 = Math.abs(activity.measure(azs2Latitude, azs2Longitude, location.getLatitude(), location.getLongitude()));

        if (distance1 > distance2) return 1;
        if (distance1 < distance2) return -1;
        return 0;
    }
}
