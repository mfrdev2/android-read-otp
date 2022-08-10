package com.example.readotp;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readotp.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private MySmsReceiver mySmsReceiver;
    private TextInputEditText etOTP;


    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();


        MyApp.instance.getOtpIntent().observe(this, data -> {
            if (Objects.nonNull(data)) {
                mResultLauncher.launch(data); //case success
            }
        });

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        etOTP = binding.etOTP;
        requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS});
        startSmartUserConsent();

    }

    private void startSmartUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }


    ActivityResultLauncher<Intent> mResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        Log.d(TAG, "mResultLauncher successfully execute");
        if (result.getResultCode() == Activity.RESULT_OK) {
            Log.d(TAG, "mResultLauncher RESULT_OK");
            // Here, no request code
            Intent data = result.getData();
            if (data != null) {
                Log.d(TAG, "mResultLauncher INTENT DATA IS NOT NULL");
                String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                getOtpFromMessage(message);
            }
        }
    });

    private void getOtpFromMessage(String message) {
        Log.d(TAG, "MESSAGE::" + message);
        if (Objects.isNull(message)) {
            return;
        }

        Pattern otpPattern = Pattern.compile("(|^)\\d{6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()) {
            etOTP.setText(matcher.group(0));
        }


    }

    private ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), (Map<String, Boolean> isGranted) -> {
                if (!isGranted.isEmpty()) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                }
            });

}