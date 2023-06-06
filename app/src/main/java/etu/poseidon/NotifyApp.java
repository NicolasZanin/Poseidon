package etu.poseidon;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

public class NotifyApp extends Application {

    public static final String CHANNEL_ID = "my_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("NotifyApp", "onCreate");

        String channelName = "my channel name";

        NotificationChannel channel = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = getSystemService(NotificationManager.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            manager.createNotificationChannel(channel);


    }
}