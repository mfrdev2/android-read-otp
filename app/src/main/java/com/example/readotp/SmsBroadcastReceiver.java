package com.example.readotp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

import java.util.Objects;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "SmsBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() == SmsRetriever.SMS_RETRIEVED_ACTION) {

            Bundle extras = intent.getExtras();

            Status smsRetreiverStatus = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

            switch (smsRetreiverStatus.getStatusCode()) {

                case CommonStatusCodes
                        .SUCCESS:
                    Intent messageIntent = extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT);
                    Log.d(TAG, "Message Intent:: " + messageIntent);

                    if (Objects.isNull(messageIntent)) {
                        return;
                    }
                    MyApp.instance.setOtpIntent(messageIntent);
                    break;
                case CommonStatusCodes.TIMEOUT:
                    MyApp.instance.setOtpIntent(null);
                    break;
            }
        }
    }
}
