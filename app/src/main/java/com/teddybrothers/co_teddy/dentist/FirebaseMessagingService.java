package com.teddybrothers.co_teddy.dentist;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by co_teddy on 10/3/2017.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notification_title = remoteMessage.getNotification().getTitle();
        String notification_body = remoteMessage.getNotification().getBody();
        String sound = remoteMessage.getNotification().getSound();

        String click_action = remoteMessage.getNotification().getClickAction();
        String id_jadwal = remoteMessage.getData().get("id_jadwal");
        String idDokter = remoteMessage.getData().get("idDokter");
        String idPasien = remoteMessage.getData().get("idPasien");
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] vibrate = {0,100,200,300};

        System.out.println("id_jadwal = "+id_jadwal);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.dentists)
                        .setContentTitle(notification_title)
                        .setContentText(notification_body)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setSound(alarmSound, AudioManager.STREAM_NOTIFICATION)
                        .setVibrate(vibrate);


        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("id_jadwal",id_jadwal);
        resultIntent.putExtra("idDokter",idDokter);
        resultIntent.putExtra("idPasien",idPasien);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);


        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(mNotificationId, mBuilder.build());



    }
}
