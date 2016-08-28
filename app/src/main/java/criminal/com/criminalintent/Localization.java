package criminal.com.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by adam on 28.08.16.
 */
public class Localization {

    private int mId;
    private double latitude;
    private double longitude;

    public Localization(double latitude, double longitude) {
        this(1, latitude, longitude);
    }

    public Localization(int id, double latitude, double longitude) {
        mId = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Localization() {
        this(1);
    }

    public Localization(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
