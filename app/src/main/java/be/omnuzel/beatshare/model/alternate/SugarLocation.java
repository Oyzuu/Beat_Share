package be.omnuzel.beatshare.model.alternate;

import com.orm.SugarRecord;

/**
 * Created by isdc on 9/11/16.
 */
public class SugarLocation extends SugarRecord {

    private int latitude;
    private int longitude;
    private String country;
    private String city;
    private String neighbourhood;

    public SugarLocation() {

    }

    public int getLatitude() {
        return latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public void setNeighbourhood(String neighbourhood) {
        this.neighbourhood = neighbourhood;
    }
}
