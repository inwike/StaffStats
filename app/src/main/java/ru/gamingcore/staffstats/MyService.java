package ru.gamingcore.staffstats;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import ru.gamingcore.staffstats.json.Allow_scan;
import ru.gamingcore.staffstats.json.Emp_data;
import ru.gamingcore.staffstats.json.List_violation;
import ru.gamingcore.staffstats.network.ServerWork;

public class MyService extends Service {
    private static final String TAG = "INWIKE";

    private final LocalBinder localBinder = new LocalBinder();

    public Emp_data emp_data = new Emp_data();
    public Allow_scan allow_scan = new Allow_scan();
    public ServerWork serverWork = new ServerWork();
    private List_violation list_violation = new List_violation();
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private ErrorListener errorListener;
    private ServerWork.Listener listener = new ServerWork.Listener() {

        @Override
        public void onExec_data(Emp_data emp_data) {
            MyService.this.emp_data = emp_data;
            if (errorListener != null) {
                errorListener.onFinish();
            }
        }

        @Override
        public void onAllow_scan(Allow_scan allow_scan) {
           /* Intent intent = new Intent(getApplicationContext(), ScanActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyService.this.allow_scan = allow_scan;
            startActivity(intent);*/
        }

        @Override
        public void onList_violation(List_violation list_violation) {
            MyService.this.list_violation = list_violation;
        }

        @Override
        public void onUpload() {
            if (errorListener != null) {
                errorListener.onUpload();
            }
        }

        @Override
        public void onError() {
            Log.e(TAG, "onError");
            if (errorListener != null) {
                errorListener.onError();
            }
        }
    };


    public MyService() {
        serverWork.setListener(listener);
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return localBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /* Location */
    @SuppressLint("MissingPermission")
    public void initLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {

            boolean gps_enabled = false;
            boolean network_enabled = false;

            try {
                gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception ignored) {
            }

            try {
                network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ignored) {
            }

            if (!gps_enabled && !network_enabled) {
                // notify user
                new AlertDialog.Builder(this)
                        .setMessage(R.string.gps_network_not_enabled)
                        .setNegativeButton(R.string.Cancel, null)
                        .setPositiveButton(R.string.open_location_settings, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        }).show();
            }

            locationListener = new MyLocationListener();
            locationManager.requestLocationUpdates(LocationManager
                    .NETWORK_PROVIDER, 50000, 500, locationListener);
        }
    }

    public interface ErrorListener {
        void onError();
        void onUpload();
        void onFinish();
    }

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //jsonData.current = location;
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}