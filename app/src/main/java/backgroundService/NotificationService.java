package backgroundService;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import criminal.com.criminalintent.Flower;
import criminal.com.criminalintent.FlowerLab;
import criminal.com.criminalintent.FlowerListActivity;
import criminal.com.criminalintent.Localization;
import criminal.com.criminalintent.LocalizationLab;
import criminal.com.criminalintent.R;

/**
 * Created by adam on 29.08.16.
 */
public class NotificationService extends IntentService {

    private static final String TAG = "NotificationService";
    private static boolean isNotificationSend = true;

    private static final int CHECK_INTERVAL = /*1000 * 20;*/ 1000 * 60 * 15; // 60 seconds *10 //sprawdzanie co 15 min

    private static final long LOCATION_REFRESH_TIME = 1000 * 60 * 15;

    private static final long LOCATION_REFRESH_DISTANCE = 200;

    private LocationManager locationManager;

    Location locationGPS = null;

    public static Intent newIntent(Context context) {
        return new Intent(context, NotificationService.class);
    }

    public NotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);

        Date currentDate = new Date();
        LocalizationLab localizationLab = LocalizationLab.get(this);

        if (checkHour("01:00:00", "02:00:00", currentDate)) {
            isNotificationSend = false;
        }

        List<Flower> flowers = FlowerLab.get(this).getNotificationFlowers();
        if (flowers.size() > 0 && !isNotificationSend) {
            int id = 0;
            for (Flower flower : flowers) {
                if (removeTime(flower.getEndDate()).equals(removeTime(currentDate))) {
                    if (checkHour("09:00:00", "21:00:00", currentDate)) {
                        List<Localization> loc = localizationLab.getLocalizations();
                        Localization localization = loc.get(0);
                        if (localization != null) {
                            if (checkLocalization(localization.getLatitude(), localization.getLongitude())) {
                                performNotification(id, flower);
                                id++;
                                isNotificationSend = true;
                                updateFlower(flower);
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateFlower(Flower flower) {
        flower.setStartDate(flower.getEndDate());
        flower.setEndDate(addDays(flower.getStartDate(), Integer.valueOf(String.valueOf(flower.getDays()))));
        FlowerLab.get(this).updateFlower(flower);
    }

    public static Date addDays(Date date, int days) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }


    private void performNotification(Integer id, Flower flower) {
        Intent intent = new Intent(this, FlowerListActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Your plant needs water").setContentText(flower.getName())
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_menu_add).getNotification();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(id, notification);

    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent i = NotificationService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), CHECK_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    private Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }


    public boolean checkLocalization(double latitude, double longitude) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        /*Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = locationManager.getBestProvider(criteria, true);
        Intent intent = new Intent(this, FlowerListActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        locationManager.requestLocationUpdates(provider, 0, 0, pIntent); */
        //Location locationGPS = getLastKnownLocation();//locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);*/

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                LOCATION_REFRESH_DISTANCE, mLocationListener);

        float distance = 100;
        if (locationGPS != null) {
            distance = calculateDistance(latitude, longitude, locationGPS.getLatitude(), locationGPS.getLongitude());
        }

        if (distance > 50) {
            return false;
        }
        return true;
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            locationGPS = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };


    private static float calculateDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(latitude2-latitude1);
        double dLng = Math.toRadians(longitude2-longitude1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }


    public static boolean checkHour(String initialTime, String finalTime, Date date) {
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = localDateFormat.format(date);

        try {
            if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
                boolean valid = false;
                //Start Time
                java.util.Date inTime = new SimpleDateFormat("HH:mm:ss").parse(initialTime);
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(inTime);

                //Current Time
                java.util.Date checkTime = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
                Calendar calendar3 = Calendar.getInstance();
                calendar3.setTime(checkTime);

                //End Time
                java.util.Date finTime = new SimpleDateFormat("HH:mm:ss").parse(finalTime);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(finTime);

                if (finalTime.compareTo(initialTime) < 0) {
                    calendar2.add(Calendar.DATE, 1);
                    calendar3.add(Calendar.DATE, 1);
                }

                java.util.Date actualTime = calendar3.getTime();
                if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0)
                        && actualTime.before(calendar2.getTime())) {
                    valid = true;
                }
                return valid;
            } else {
                throw new IllegalArgumentException("Not a valid time, expecting HH:MM:SS format");
            }
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return false;

    }

    private Date parseDate(String date, String format)
    {
        SimpleDateFormat formatnow = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZ yyyy", Locale.ENGLISH);
        SimpleDateFormat formatneeded=new SimpleDateFormat(format);
        Date date1 = null;
        try {
            date1 = formatnow.parse(date);
            String date2 = formatneeded.format(date1);

            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
