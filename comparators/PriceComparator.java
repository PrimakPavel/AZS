package su.moy.chernihov.mapapplication.comparators;

import java.util.Comparator;

import su.moy.chernihov.mapapplication.AZS;
import su.moy.chernihov.mapapplication.Price;

//This class will sort ASZ by price, in ascending order
public class PriceComparator implements Comparator<AZS> {
    @Override
    public int compare(AZS azs1, AZS azs2) {
        // first Azs
        String str1 = azs1.getOilPrice();
        str1 = str1.replace(Price.А92,";").replace(Price.А95,";").replace(Price.DF,";").trim();
        String[] splitStr1 = str1.split(";");
        double price1 = 0;

        try {
            double a92 = Double.parseDouble(splitStr1[1]);
            double a95 = Double.parseDouble(splitStr1[2]);
            double df = Double.parseDouble(splitStr1[3]);
            price1 = (a92 + a95 + df) / 3;
        }
        catch (NumberFormatException e) {}
        catch (ArrayIndexOutOfBoundsException e) {}

        // second Azs
        String str2 = azs2.getOilPrice();
        str2 = str2.replace(Price.А92,";").replace(Price.А95,";").replace(Price.DF,";").trim();
        String[] splitStr2 = str2.split(";");
        double price2 = 0;

        try{
            double a92 = Double.parseDouble(splitStr2[1]);
            double a95 = Double.parseDouble(splitStr2[2]);
            double df = Double.parseDouble(splitStr2[3]);
            price2 = (a92 + a95 + df) / 3;
        }
        catch (NumberFormatException e) {}
        catch (ArrayIndexOutOfBoundsException e) {}

        if (price1 > price2) return 1;
        if (price1 < price2) return -1;
        return 0;

    }
}
