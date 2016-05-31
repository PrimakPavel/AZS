package su.moy.chernihov.mapapplication;


import com.google.android.gms.maps.model.Marker;

public class AZS {
    private String brandName;
    private String address;
    private String telephoneNumber;
    private double latitude;
    private double longitude;
    private String oilPrice;
    private int iconRes;
    private Marker marker;
    private String id;
    private boolean isHighlight;

    public AZS() {
    }

    // В конструктор передаю : бренд, адрес, телефон, координаты, типы топлива
    public AZS(String id, String brandName, String address, String telephoneNumber, double latitude, double longitude) {
        this.id = id;
        this.brandName = brandName;
        this.address = address;
        this.telephoneNumber = telephoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;

    }

    // ID
    public String getId() {
        return id;
    }

    //бренд
    public String getBrandName() {
        return brandName;
    }

    //адрес
    public String getAddress() {
        return address;
    }

    //телефон
    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    //координаты Lat
    public double getLatitude() {
        return latitude;
    }

    //координаты Lng
    public double getLongitude() {
        return longitude;
    }

    // типы топлива
    public String getOilPrice() {
        return oilPrice;
    }

    public int getIconRes() {
        return iconRes;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public void setOilPrice(String oilPrice) {
        this.oilPrice = oilPrice;
    }

    public void setHighlight(boolean highlight) {
        isHighlight = highlight;
    }

    public boolean isHighlight() {
        return isHighlight;
    }

}
