package com.service.serveigo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String CHANNEL_ID="SSUAChannel";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        startService(new Intent(this,MyFirebaseMessagingService.class));

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

        Log.d("msgData", "onMessageReceived: " + remoteMessage.getData());
//        Log.d("msg", "onMessageReceived: " + remoteMessage.getNotification().getBody());
        String messageTitle = remoteMessage.getData().get("title");
        String messageBody = remoteMessage.getData().get("body");

        String click_action = remoteMessage.getData().get("click_action");

        /*Intent intent=new Intent(getApplicationContext(),HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/
        NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(
                    CHANNEL_ID,"SSU App Service Channel", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("SSU channel for app text FCM");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.BLUE);
            //notificationChannel.setVibrationPattern(new long[]{0, 100,100});
            notificationChannel.enableVibration(true);
            manager.createNotificationChannel(notificationChannel);
        }

        //String dataMessage = remoteMessage.getData().get("message");
        //String dataFrom = remoteMessage.getData().get("from_user_id");
        Intent intent =  new Intent(this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //intent.putExtra("dataMessage",dataMessage);
        //intent.putExtra("dataFrom",dataFrom);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_stat_logomanhalf)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());

        if(click_action.equals("all")){
            Bitmap bitmap = getBitmapfromUrl(remoteMessage.getData().get("jobID"));
            builder.setStyle(
                    new NotificationCompat.BigPictureStyle()
                            .bigPicture(bitmap)
                            .bigLargeIcon(null)
            ).setLargeIcon(bitmap);
        }

        manager.notify(m,builder.build());
    }
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }
}
