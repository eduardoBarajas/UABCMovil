package com.uabcmovil.uabcmovil.BroadcastReceivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import com.uabcmovil.uabcmovil.Activities.ControlDeUsuariosActivity;
import com.uabcmovil.uabcmovil.Activities.IncidenciasActivity;
import com.uabcmovil.uabcmovil.Activities.NewsFeedActivity;
import com.uabcmovil.uabcmovil.Activities.SolicitudesActivity;
import com.uabcmovil.uabcmovil.R;

public class Notificaciones extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String tipo = "",titulo = "", actividad = "";
        if(intent.hasExtra("titulo")){
            titulo = intent.getStringExtra("titulo");
            tipo = intent.getStringExtra("tipo");
            actividad = intent.getStringExtra("actividad");
        }
        createNotification(titulo,tipo,actividad,context);
    }

    private void createNotification(String titulo,String type,String activity,Context context){
        String bigTxt = titulo;
        String title = type;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "notify_001");
        Intent ii = null;
        switch (activity){
            case "NewsFeed":{
                ii = new Intent(context, NewsFeedActivity.class);
                break;
            }
            case "Solicitudes":{
                ii = new Intent(context, SolicitudesActivity.class);
                break;
            }
            case "ControlUsuarios":{
                ii = new Intent(context, ControlDeUsuariosActivity.class);
                break;
            }
            case "Incidencias":{
                ii = new Intent(context, IncidenciasActivity.class);
                break;
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 777, ii, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(bigTxt);
        bigText.setBigContentTitle(title);
        
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.logo);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(bigTxt);
        mBuilder.setAutoCancel(true);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mBuilder.setStyle(bigText);

        Vibrator vibrator = (Vibrator) context.getSystemService(context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001",
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
}

