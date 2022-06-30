package com.bluescript.youknowit;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationsService extends Service {
    private final IBinder binder = new NotificationsServiceBinder();
    HashMap<String, Handler> runningNotificationsHandlers;
    HashMap<String, Runnable> runningNotificationsRunnables;
    public NotificationsService() {
    }

    class NotificationsServiceBinder extends Binder {
        NotificationsService getService() {
            return NotificationsService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        runningNotificationsHandlers = new HashMap<>();
        runningNotificationsRunnables = new HashMap<>();
        Notification notification;
        notification = new Notification.Builder(this, "FOREGROUND_SERVICE_CHANNEL")
                .setContentTitle(getText(R.string.foreground_notification_title))
                .setContentText(getText(R.string.foreground_notification_text))
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .build();

        startForeground(100, notification);

        return START_NOT_STICKY;
    }

    public void createNewNotificationTask(String id, int interval, Context context) {
        Handler handler = new Handler();
        Runnable runnable;

        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                NotificationsTemplates.QuestionNotificationTemplate(context, id);

                handler.postDelayed(this,  interval * 1000);
            }
        }, interval * 1000);

        runningNotificationsHandlers.put(id, handler);
        runningNotificationsRunnables.put(id, runnable);
    }

    public void removeNotificationTask(String id) {
        Handler currentHandler = runningNotificationsHandlers.get(id);
        Runnable currentRunnable = runningNotificationsRunnables.get(id);

        currentHandler.removeCallbacks(currentRunnable);
    }
}