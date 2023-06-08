package etu.poseidon;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

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
            switch (weather) {
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

            Drawable logo = getResources().getDrawable(R.mipmap.ic_launcher);

            Log.d("NOTIFICATION PERCUE", "onMessageReceived: " + Message.getInstance().getTitle() + " " + Message.getInstance().getBody() );

            NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext(), NotifyApp.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(Message.getInstance().getTitle())
                    .setContentText(Message.getInstance().getBody())
                    .setLargeIcon(drawableToBitmap(logo, 100, 0))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(drawableToBitmap(drawable, 200, 200)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notif.build());
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable, int imageSize, int marginSize) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        float scale = Math.max((float) imageSize / width, (float) imageSize / height);

        int scaledWidth = Math.round(scale * width);
        int scaledHeight = Math.round(scale * height);

        int bitmapSize = imageSize + 2 * marginSize; // Taille du Bitmap avec la marge

        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int left = (bitmapSize - scaledWidth) / 2;
        int top = (bitmapSize - scaledHeight) / 2;
        int right = left + scaledWidth;
        int bottom = top + scaledHeight;

        drawable.setBounds(left, top, right, bottom);
        drawable.draw(canvas);

        return bitmap;
    }



}

