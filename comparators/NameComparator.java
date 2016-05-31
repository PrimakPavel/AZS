package su.moy.chernihov.mapapplication.comparators;

import java.util.Comparator;

import su.moy.chernihov.mapapplication.AZS;

//This class will sort ASZ by BrandName, in ascending order

public class NameComparator implements Comparator<AZS> {
    @Override
    public int compare(AZS azs1, AZS azs2) {
        return azs1.getBrandName().compareTo(azs2.getBrandName());
    }
}
