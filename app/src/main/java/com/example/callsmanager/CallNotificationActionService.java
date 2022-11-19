package com.example.callsmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

public class CallNotificationActionService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.cancel(intent.getExtras().getInt("notificationId", -1));
//        notificationManager.cancelAll();

        Toast.makeText(this, "Aaa Cancled !!!" + intent.getIntExtra("notificationId", -1), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Aaa Cancled !!!" + intent.getStringExtra("action"), Toast.LENGTH_SHORT).show();

        return super.onStartCommand(intent, flags, startId);
    }
}
