package etu.poseidon;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import etu.poseidon.activities.main.MainActivity;

public class IHMFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Message.getInstance().set(remoteMessage);

        System.out.println("Message received");

        if(!Message.getInstance().isNull()){
            Intent intent = new Intent(this, MainActivity.class);
            // pour pouvoir empecher de revenir en arrière
            // on supprimer tout ce qu'il y a au dessus de la main activity dans la stack
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            String weather = Message.getInstance().getWeather();
            Drawable drawable;
            switch(weather){
                case "SUN":
                    drawable = getResources().getDrawable(R.drawable.ic_weather_sun);
                    break;
                case "RAIN":
                    drawable = getResources().getDrawable(R.drawable.ic_weather_rain);
                    break;
                case "THUNDERSTORM":
                    drawable = getResources().getDrawable(R.drawable.ic_weather_thunderstorm);
                    break;
                case "WIND":
                    drawable = getResources().getDrawable(R.drawable.ic_weather_wind);
                    break;
                case "CLOUD":
                    drawable = getResources().getDrawable(R.drawable.ic_weather_cloud);
                    break;
                case "STORM":
                    drawable = getResources().getDrawable(R.drawable.ic_weather_storm);
                    break;
                default:
                    drawable = getResources().getDrawable(R.drawable.ic_btn_alert);
                    break;
            }

            NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext(), NotifyApp.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(Message.getInstance().getTitle())
                    .setContentText(Message.getInstance().getBody())
                    .setLargeIcon(drawableToBitmap(drawable))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notif.build());
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

}

