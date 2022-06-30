package com.bluescript.youknowit;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

public class ManageNotifications {
    public static Intent serviceIntent = null;
    static NotificationsService notificationsService;

    private static ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            NotificationsService.NotificationsServiceBinder binder = (NotificationsService.NotificationsServiceBinder) service;
            notificationsService = binder.getService();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    public static void createAndBindService(Context context) {
        serviceIntent = new Intent(context, NotificationsService.class);
        context.startService(serviceIntent);
        context.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE);
    }

    public static void startNotificationTask(Context context, String id, int interval) {
        notificationsService.createNewNotificationTask(id, interval, context);
    }

    public static void stopNotificationTask(String id) {
        notificationsService.removeNotificationTask(id);
    }
}
