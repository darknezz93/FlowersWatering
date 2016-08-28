package criminal.com.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by adam on 07.08.16.
 */
public class Flower {

    private UUID mId;
    private String mName;
    private Date mStartDate;
    private Date mEndDate;
    private int mDays;
    private boolean mNotification;


    public Flower() {
        this(UUID.randomUUID());
    }

    public Flower(UUID id) {
        mId = id;
        mStartDate = new Date();
        mEndDate = null;
    }

    public UUID getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getStartDate() {
        return mStartDate;
    }
    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    public void setEndDate(Date endDate) {
        mEndDate = endDate;
    }

    public int getDays() {
        return mDays;
    }

    public void setDays(int days) {
        mDays = days;
    }

    public boolean isNotification() {
        return mNotification;
    }
    public void setNotification(boolean notification) {
        mNotification = notification;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
