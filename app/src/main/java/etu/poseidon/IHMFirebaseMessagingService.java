package etu.poseidon;

import android.app.NotificationManager;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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

            NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext(), NotifyApp.CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle(Message.getInstance().getTitle())
                    .setContentText(Message.getInstance().getBody())
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(0, notif.build());
        }
    }
}
