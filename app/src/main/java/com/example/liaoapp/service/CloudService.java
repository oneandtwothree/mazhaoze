package com.example.liaoapp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CloudService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
