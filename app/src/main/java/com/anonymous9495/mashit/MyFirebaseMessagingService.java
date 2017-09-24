package com.anonymous9495.mashit;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.net.URLDecoder;

/**
 * Created by staffonechristian on 2017-09-22.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG="MyGcmListenerService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String image = remoteMessage.getNotification().getIcon();
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();
        String sound = remoteMessage.getNotification().getSound();


        System.out.print("----->"+text+title);
        int id=0;
        Object obj = remoteMessage.getData().get("id");
        if(obj!=null)
        {
            id = Integer.valueOf(obj.toString());
        }

        this.sendNotification(new NotificationData(image,id,title,text,sound) );
        super.onMessageReceived(remoteMessage);
    }

    private void sendNotification(NotificationData notificationData) {
        Intent intent = new Intent(this,MainActivity.class);
        MainActivity.flag=true;
        intent.putExtra(NotificationData.TEXT,notificationData.getTextMessage());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0/*Request Code*/,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = null;
        try{

            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.flame_icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                            R.drawable.flame_icon))
                    .setContentTitle(URLDecoder.decode(notificationData.getTitle(),"UTF-8"))
                    .setContentText(URLDecoder.decode(notificationData.getTextMessage(),"UTF-8"))
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentIntent(pendingIntent);
        }catch (Exception e)
        {

            e.printStackTrace();
        }

        if (notificationBuilder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(notificationData.getId(), notificationBuilder.build());
        } else {
            Log.d(TAG, "notificationBuilder is null");
        }
    }

}
