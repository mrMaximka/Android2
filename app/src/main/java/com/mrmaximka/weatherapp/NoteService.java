package com.mrmaximka.weatherapp;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class NoteService extends IntentService {

    private int messageId = 0;
    private static double temperature = 0;
    String message;

    public NoteService() {
        super("NoteService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (isRunning(this)){
        if (temperature != SecondFragment.getTemperatureText()){        // Если менялась температура
                temperature = SecondFragment.getTemperatureText();
                makeNote();     // Запускаем уведомление с новой температурой
            }
            try {
                Thread.sleep(5000);     // Таймер на 5 сек
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void makeNote(){

        message = String.format(getString(R.string.service_message), temperature);  // Создаем сообщеение с новой темп.
        NotificationCompat.Builder builder;
        NotificationChannel mChannel = null;
        String CHANNEL_ID = "my_channel_01";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {     // Для API Oreo и выше
            CharSequence name = "Test";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("WeatherApp")
                    .setContentText(message)
                    .setChannelId(CHANNEL_ID);
        }
        else {                              // Остальные API
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("WeatherApp")
                    .setContentText(message);
        }


        Intent resultIntent = new Intent(this, NoteService.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   // Для API Oreo и выше
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(messageId++, builder.build());
    }

    public static boolean isRunning(Context ctx) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (NoteService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
