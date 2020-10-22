package ru.sukhikh.appgetcoupon;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

import ru.sukhikh.appgetcoupon.Fragments.ListFragment;
import ru.sukhikh.appgetcoupon.Fragments.ShopFragment;

public class FirebaseMessaging extends FirebaseMessagingService {

    private List<String> favshops = new ArrayList<>();
    private List<String> tokenAndCheck =new ArrayList<>();
    public static final String CHANNEL_ID = "channel1";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());

    }

    private void sendNotification(String messageBody, String title) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        createNotificationChannels();

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.ic_notification)
                .setColor(0xff0000)
                .setContentText(messageBody)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setTicker(messageBody)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(0, notificationBuilder.build());

    }
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(CHANNEL_ID, "Channel 1", NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is Channel 1");

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel1);
        }
    }
    @Override
    public void onNewToken(final String token) {
        Log.d("TAG", "Refreshed token: " + token);
        FavoritesDB favDB = new FavoritesDB(getBaseContext());

        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("TOKEN_VALUE", token);

        favshops=favDB.getListShops();
        tokenAndCheck.add(token);
        Boolean check = ListFragment.SettingsFragment.getPushState();//true;
        if(check==null){
            check = NotificationManagerCompat.from(getBaseContext()).areNotificationsEnabled();
        }
        tokenAndCheck.add(check.toString());
        ShopFragment.JsonNotification w = (ShopFragment.JsonNotification) new ShopFragment.JsonNotification().execute(tokenAndCheck, favshops);
    }
}