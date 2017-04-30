package com.odoo.core.support;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 26/11/15
 */
public abstract class OdooCompatActivity extends AppCompatActivity {
    public static final String TAG = OdooCompatActivity.class.getSimpleName();
    private DevicePermissionResultListener mDevicePermissionResultListener = null;

    private static final String[] PERMISSIONS_REQUIRED = {
            /* These don't seem to be necessary on SDK 23 (Android Marshmallow 6.0)
             * though they were necessary, in AndroidManifest.xml, on SDK 21 (Android Lollipop 5.1).
             * even though they are documented as not being needed for getExternalCacheDir on
             * SDK versions >= 18.
             */
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_SYNC_SETTINGS,
            Manifest.permission.WRITE_SYNC_SETTINGS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCOUNT_MANAGER,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_SYNC_SETTINGS,
            Manifest.permission.READ_SYNC_STATS,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.MANAGE_DOCUMENTS,

            //This is needed on SDK <= 22, for instance for AccountManager.getAccountsByType().
            //Presumably it is not defined in android.Manifest because we don't need
            //to request it at runtime because requesting at runtime is only possible
            //since SDK 23.
            Manifest.permission.GET_ACCOUNTS,

            //This is needed on SDK <= 22, for instance for AccountManager.getAuthToken().
            //Presumably it is not defined in android.Manifest because we don't need
            //to request it at runtime because requesting at runtime is only possible
            //since SDK 23.
            //Manifest.permission.USE_CREDENTIALS,

            //This is needed on SDK <= 22, for instance for AccountManager.setAuthToken(),
            //addAccount() and removeAccount().
            //Presumably it is not defined in android.Manifest because we don't need
            //to request it at runtime because requesting at runtime is only possible
            //since SDK 23.
            //Manifest.permission.MANAGE_ACCOUNTS,

            //This is needed on SDK <= 22, for instance for AccountManager.addAccountExplicitly()
            //and getUserData().
            //Presumably it is not defined in android.Manifest because we don't need
            //to request it at runtime because requesting at runtime is only possible
            //since SDK 23.
            //Manifest.permission.AUTHENTICATE_ACCOUNTS,

    };
    private static final int PERMISSION_REQUEST_CODE = 1;

    // API23+ Permission model helper methods
    public void setOnDevicePermissionResultListener(DevicePermissionResultListener callback) {
        mDevicePermissionResultListener = callback;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        checkPermissions();
        if (mDevicePermissionResultListener != null) {
            mDevicePermissionResultListener.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public interface DevicePermissionResultListener {
        void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);
    }

    private void checkPermissions() {
        //Check for all the permissions, because we need them all.
        //TODO: Get the list of permissions from AndroidManifest.xml ?
        final ArrayList<String> permissionsMissing = new ArrayList<>();
        for (final String permission : PERMISSIONS_REQUIRED) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsMissing.add(permission);
            }
        }

        if (!permissionsMissing.isEmpty()) {
            final String[] array = new String[permissionsMissing.size()];
            permissionsMissing.toArray(array);
            Log.i("Odoo Permission: ","ClassifyActivity.checkPermissions(): requesting permissions because checkSelfPermission() failed for permissions: " + permissionsMissing);

            //We will get the result asynchronously in onRequestPermissionsResult().
            ActivityCompat.requestPermissions(this, array, PERMISSION_REQUEST_CODE);
        }
    }
}
