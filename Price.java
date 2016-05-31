package su.moy.chernihov.mapapplication;


import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class Price {
    public static final String SHELL = "SHELL";
    public static final String AVIAS = "АВИАС";
    public static final String ANP = "ANP";
    public static final String ULTRA = "ULTRA";
    public static final String MANGO= "MANGO";
    public static final String MARSHAL = "MARSHAL";
    public static final String LUXWEN = "LUXWEN";
    public static final String NARODNA = "НАРОДНА";
    public static final String INTERLOGOS = "INTERLOGOS";
    public static final String UPG = "UPG";
    public static final String UKR_AUTO = "УКРАВТО";
    public static final String AMIC = "AMIC Energy";
    public static final String UKR_PETROL = "УКРПЕТРОЛЬ";
    public static final String SOCAR = "SOCAR";
    public static final String WOG = "WOG";
    public static final String BRSM_NAFTA = "БРСМ";
    public static final String KLO = "КЛО";
    public static final String OKKO = "OKKO";
    public static final String TNK = "ТНК";
    public static final String FORMULA_RETAIL = "FORMULA";
    public static final String GOLD_GEPARD = "Золотой Гепард";
    public static final String UKRNAFTA = "УКРНАФТА";
    public static final String MINIMAL_PRICE = "Минимальная цена";
    public static final String MAXIMAL_PRICE = "Максимальная цена";
    public static final String MIDDLE_PRICE = "Средняя цена";



    private static final String SITE_ADDRESS = "http://finance.i.ua/fuel/12/";
    private static final String TAG = "Price download";
    public static final String А92 = " A92-";
    public static final String А95 = " A95-";
    public static final String DF = " ДТ-";

    private static Price outInstance = new Price();
    private static HashMap<String, String> azsPrices = new HashMap<>();
    private static Context mContext;

    public static Price getInstance(Context context) {
        mContext = context;
        return outInstance;
    }
    private Price() {
    }

    public boolean downloadPrices() {
        Document document;
        Elements elements;
        try {
            document = Jsoup.connect(SITE_ADDRESS).get();
            elements = document.select("div#feMain2 > table > tbody > tr");
            String data;
            // Shell
            Elements shellElements = elements.get(1).select("td");
            data = А92 + shellElements.get(1).text() + А95 + shellElements.get(2).text() + DF + shellElements.get(3).text();
            azsPrices.put(SHELL, data);
            //SOCAR
            Elements socarElements = elements.get(2).select("td");
            data = А92 + socarElements.get(1).text() + А95 + socarElements.get(2).text() + DF + socarElements.get(3).text();
            azsPrices.put(SOCAR, data);
            // WOG
            Elements wogElements = elements.get(3).select("td");
            data = А92 + wogElements.get(1).text() + А95 + wogElements.get(2).text() + DF + wogElements.get(3).text();
            azsPrices.put(WOG, data);
            // ANP
            Elements alphaNaftaElements = elements.get(4).select("td");
            data = А92 + alphaNaftaElements.get(1).text() + А95 + alphaNaftaElements.get(2).text() + DF + alphaNaftaElements.get(3).text();
            azsPrices.put(ANP, data);
            //BRSM
            Elements brsmNaftaElements = elements.get(5).select("td");
            data = А92 + brsmNaftaElements.get(1).text() + А95 + brsmNaftaElements.get(2).text() + DF + brsmNaftaElements.get(3).text();
            azsPrices.put(BRSM_NAFTA, data);
            //KLO
            Elements kloElements = elements.get(6).select("td");
            data = А92 + kloElements.get(1).text() + А95 + kloElements.get(2).text() + DF + kloElements.get(3).text();
            azsPrices.put(KLO, data);
            //OKKO
            Elements okkoElements = elements.get(7).select("td");
            data = А92 + okkoElements.get(1).text() + А95 + okkoElements.get(2).text() + DF + okkoElements.get(3).text();
            azsPrices.put(OKKO, data);
            //TNK
            Elements tnkElements = elements.get(8).select("td");
            data = А92 + tnkElements.get(1).text() + А95 + tnkElements.get(2).text() + DF + tnkElements.get(3).text();
            azsPrices.put(TNK, data);
            //Ukr-Nafta
            Elements ukrNaftaElements = elements.get(9).select("td");
            data = А92 + tnkElements.get(1).text() + А95 + tnkElements.get(2).text() + DF + tnkElements.get(3).text();
            azsPrices.put(UKRNAFTA, data);
            // Formula retail
            Elements formulaRetailElements = elements.get(10).select("td");
            data = А92 + formulaRetailElements.get(1).text() + А95 + formulaRetailElements.get(2).text() + DF + formulaRetailElements.get(3).text();
            azsPrices.put(FORMULA_RETAIL, data);
            // Minimal Price
            Elements minimalPriceElements = elements.get(12).select("td");
            data = А92 + minimalPriceElements.get(1).text() + А95 + minimalPriceElements.get(2).text() + DF + minimalPriceElements.get(3).text();
            azsPrices.put(MINIMAL_PRICE, data);
            // Maximal Price
            Elements maximalPriceElements = elements.get(13).select("td");
            data = А92 + maximalPriceElements.get(1).text() + А95 + maximalPriceElements.get(2).text() + DF + maximalPriceElements.get(3).text();
            azsPrices.put(MAXIMAL_PRICE, data);
            // Middle price
            Elements middlePriceElements = elements.get(14).select("td");
            data = А92 + middlePriceElements.get(1).text() + А95 + middlePriceElements.get(2).text() + DF + middlePriceElements.get(3).text();
            azsPrices.put(MIDDLE_PRICE, data);
        } catch (IOException e) {
            Log.d(TAG,"Connect problems");
            return false;
        }
        return true;
    }
    public static String getPrice(String azsName) {
        if (azsPrices.containsKey(azsName))
            return azsPrices.get(azsName);
        else return mContext.getString(R.string.not_data);
    }
}

