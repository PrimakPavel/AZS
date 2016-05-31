package su.moy.chernihov.mapapplication;

import android.content.res.Resources;
import android.util.Log;

import java.util.ArrayList;


public class LabAzs {
    private static LabAzs ourInstance = new LabAzs();
    protected static ArrayList<AZS> azsList = new ArrayList<>();
    protected static ArrayList<AZS> currentAzsList = new ArrayList<>();
    private static AzsFragmentsActivity mContext;
    private static final String TAG = "LabAzs";
    protected static boolean isInit = false;

    public static LabAzs getInstance(AzsFragmentsActivity context) {
        mContext = context;
        return ourInstance;
    }

    private LabAzs() {
    }
    public void init() {

        Thread initThread = new Thread(new Runnable() {
            @Override
            public void run() {
                // загружаю прайс из инета
                Price price = Price.getInstance(mContext);
                price.downloadPrices();
                FireBaseConnections fireBaseConnections = FireBaseConnections.getInstance(mContext);
                fireBaseConnections.loadFromCloud("Kiev/");
                /*// загружаю список заправок из .xml файла дополняя данные прайсом цен на топливо
                loadFromXML(price);*/
                // отображаю на карте все заправки из списка
                //call back
                mContext.callBackAddAZSMarkers(azsList);

            }
        });
        initThread.start();


    }
    public static void setCurrentAzsList(ArrayList<AZS> currentAzsList) {
        LabAzs.currentAzsList = currentAzsList;
    }

    public static ArrayList<AZS> getCurrentAzsList() {
        return currentAzsList;
    }

    private void loadFromXML(Price price) {
        isInit = false;
        Resources res = mContext.getResources();
        String[] azsArray = res.getStringArray(R.array.azs_array);
        azsList = new ArrayList<>();

        for (String azs: azsArray) {

            String[] splitStr = azs.split(";");
            // id
            String id = splitStr[0];
            // name
            String brandName = splitStr[1];
            // address
            String address = splitStr[2];
            // phone number
            String telephoneNumber = splitStr[3];
            // gps
            String gpsCoordinate = splitStr[4];

            String[] splitGPS = gpsCoordinate.split(" ");
            double latitude = 0.0d;
            double longitude = 0.0d;
            try {
                latitude = Double.valueOf(splitGPS[0]);
                longitude = Double.valueOf(splitGPS[1]);
            } catch (NumberFormatException e) {
                Log.d(TAG, "problem with gps coordinate format");
            }
            // icon
            int iconRes;
            String oilPrice = "";
            switch (brandName){
                case Price.SOCAR:
                    iconRes = R.drawable.socar_logo;
                    oilPrice = price.getPrice(Price.SOCAR);
                    break;
                case Price.SHELL:
                    iconRes = R.drawable.shell_logo;
                    oilPrice = price.getPrice(Price.SHELL);
                    break;
                case Price.AVIAS:
                    iconRes = R.drawable.avias_logo;
                    oilPrice = price.getPrice(Price.AVIAS);
                    break;
                case Price.ANP:
                    iconRes = R.drawable.anp_logo;
                    oilPrice = price.getPrice(Price.ANP);
                    break;
                case Price.ULTRA:
                    iconRes = R.drawable.ultra_logo;
                    oilPrice = price.getPrice(Price.ULTRA);
                    break;
                case Price.MARSHAL:
                    iconRes = R.drawable.marshal_logo;
                    oilPrice = price.getPrice(Price.MARSHAL);
                    break;
                case Price.LUXWEN:
                    iconRes = R.drawable.luxwen_logo;
                    oilPrice = price.getPrice(Price.LUXWEN);
                    break;
                case Price.NARODNA:
                    iconRes = R.drawable.narodna_logo;
                    oilPrice = price.getPrice(Price.NARODNA);
                    break;
                case Price.INTERLOGOS:
                    iconRes = R.drawable.interlogos_logo;
                    oilPrice = price.getPrice(Price.INTERLOGOS);
                    break;
                case Price.UPG:
                    iconRes = R.drawable.upg_logo;
                    oilPrice = price.getPrice(Price.UPG);
                    break;
                case Price.UKR_AUTO:
                    iconRes = R.drawable.ukr_avto_logo;
                    oilPrice = price.getPrice(Price.UKR_AUTO);
                    break;
                case Price.AMIC:
                    iconRes = R.drawable.amic_logo;
                    oilPrice = price.getPrice(Price.AMIC);
                    break;
                case Price.UKR_PETROL:
                    iconRes = R.drawable.ukr_petrol_logo;
                    oilPrice = price.getPrice(Price.UKR_PETROL);
                    break;
                case Price.WOG:
                    iconRes = R.drawable.wog_logo;
                    oilPrice = price.getPrice(Price.WOG);
                    break;
                case Price.BRSM_NAFTA:
                    iconRes = R.drawable.brsm_logo;
                    oilPrice = price.getPrice(Price.BRSM_NAFTA);
                    break;
                case Price.KLO:
                    iconRes = R.drawable.klo_logo;
                    oilPrice = price.getPrice(Price.KLO);
                    break;
                case Price.OKKO:
                    iconRes = R.drawable.okko_logo;
                    oilPrice = price.getPrice(Price.OKKO);
                    break;
                case Price.TNK:
                    iconRes = R.drawable.tnk_logo;
                    oilPrice = price.getPrice(Price.TNK);
                    break;
                case Price.FORMULA_RETAIL:
                    iconRes = R.drawable.formula_logo;
                    oilPrice = price.getPrice(Price.TNK);
                    break;
                case Price.GOLD_GEPARD:
                    iconRes = R.drawable.tnk_gold_gepard_logo;
                    oilPrice = price.getPrice(Price.TNK);
                    break;
                case Price.UKRNAFTA:
                    iconRes = R.drawable.ukr_nafta_logo;
                    oilPrice = price.getPrice(Price.UKRNAFTA);
                    break;
                default:
                    iconRes = R.drawable.default_logo;
            }

            if (latitude != 0.0d && longitude != 0.0d )
            {
                AZS currentAzs = new AZS(id, brandName, address, telephoneNumber, latitude, longitude);
                currentAzs.setIconRes(iconRes);
                currentAzs.setOilPrice(oilPrice);
                azsList.add(currentAzs);
            }
        }
        isInit = true;

    }

    public static boolean isInit() {
        return isInit;
    }

    public static ArrayList<AZS> getAzsList() {
        return azsList;
    }
}
