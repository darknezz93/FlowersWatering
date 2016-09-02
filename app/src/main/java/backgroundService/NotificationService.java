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
import android.location.LocationManager;
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

    private static final int CHECK_INTERVAL =/* 1000 * 20; */1000 * 60 * 15; // 60 seconds *10 //sprawdzanie co 15 min

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

        if(checkHour(new Date(), "01:00:00", "02:00:00")) {
            isNotificationSend = false;
        }

        List<Flower> flowers = FlowerLab.get(this).getNotificationFlowers();
        if (flowers.size() > 0 && !isNotificationSend) {
            int id = 0;
            for (Flower flower : flowers) {
                if (removeTime(flower.getEndDate()).equals(removeTime(currentDate))) {
                    if (checkHour(currentDate, "09:00:00", "21:00:00")) {
                        List<Localization> loc = localizationLab.getLocalizations();
                        Localization localization = loc.get(0);
                        if(localization != null) {
                            if(checkLocalization(localization.getLatitude(), localization.getLongitude())) {
                                performNotification(id, flower);
                                id++;
                                isNotificationSend = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void performNotification(Integer id, Flower flower) {
        Intent intent = new Intent(this, FlowerListActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Your plant needs water").setContentText(flower.getName())
                .setContentIntent(pIntent)
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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        float distance = calculateDistance(latitude, longitude, locationGPS.getLatitude(), locationGPS.getLongitude());
        if(distance > 40) {
            return false;
        }
        return true;
    }


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



    private boolean checkHour(Date date, String stringHour1, String stringHour2) {
        try {
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(stringHour1);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(stringHour2);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");

            String currentTime = localDateFormat.format(date);

            Date d = new SimpleDateFormat("HH:mm:ss").parse(currentTime);
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                return true;
            }
        } catch (ParseException e) {
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
