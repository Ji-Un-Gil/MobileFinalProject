package com.example.application;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

public class RequestPermissionsUtil {

    private final Context context;
    private final int REQUEST_LOCATION = 1;

    // 위치 권한 SDK 버전 29 이상
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private final String[] permissionsLocationUpApi29Impl = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    // 위치 권한 SDK 버전 29 이하
    @TargetApi(Build.VERSION_CODES.P)
    private final String[] permissionsLocationDownApi29Impl = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public RequestPermissionsUtil(Context context) {
        this.context = context;
    }

    public void requestLocation() {
        if (Build.VERSION.SDK_INT >= 29) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permissionsLocationUpApi29Impl[0]
            ) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(
                    context,
                    permissionsLocationUpApi29Impl[1]
            ) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(
                    context,
                    permissionsLocationUpApi29Impl[2]
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        permissionsLocationUpApi29Impl,
                        REQUEST_LOCATION
                );
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permissionsLocationDownApi29Impl[0]
            ) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(
                    context,
                    permissionsLocationDownApi29Impl[1]
            ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        permissionsLocationDownApi29Impl,
                        REQUEST_LOCATION
                );
            }
        }
    }
}
