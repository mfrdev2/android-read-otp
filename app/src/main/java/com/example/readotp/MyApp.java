package com.example.readotp;

import android.app.Application;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

public class MyApp extends Application {
    public static MyApp instance;
    private final MediatorLiveData<Intent> otpIntent = new MediatorLiveData<>();

    public void setOtpIntent(Intent otpIntent) {
        this.otpIntent.postValue(otpIntent);
    }

    public LiveData<Intent> getOtpIntent() {
        return otpIntent;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
