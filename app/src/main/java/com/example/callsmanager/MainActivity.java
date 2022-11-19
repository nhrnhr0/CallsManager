package com.example.callsmanager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private  static final String SHARED_PREFS_URL = "sharedPrefsSeverUrl";

    public EditText serverEditText;
    public Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CALL_LOG,
            }, REQUEST_PERMISSIONS_REQUEST_CODE);
        }

        serverEditText = (EditText) findViewById(R.id.server_url);
        saveButton = (Button) findViewById(R.id.save_button);

        // set the server url from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_URL, MODE_PRIVATE);
        String initServerUrl = sharedPreferences.getString(SHARED_PREFS_URL, "");
        serverEditText.setText(initServerUrl);



        saveButton.setOnClickListener(v -> {
            String serverUrl = serverEditText.getText().toString();
            if (serverUrl.isEmpty()) {
                Toast.makeText(this, "Server URL is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(this, "Server URL is " + serverUrl, Toast.LENGTH_SHORT).show();
            SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS_URL, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(SHARED_PREFS_URL, serverUrl);
            editor.apply();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] != PERMISSION_GRANTED) {
                    Toast.makeText(this, "This app requires permissions to continue!", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;

            default:
                throw new RuntimeException("Unknown request code");
        }
    }
}