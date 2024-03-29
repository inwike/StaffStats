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

import androidx.appcompat.app.AlertDialog;

import java.util.List;

import ru.gamingcore.staffstats.json.Auth;
import ru.gamingcore.staffstats.json.Avail;
import ru.gamingcore.staffstats.json.Detail;
import ru.gamingcore.staffstats.json.Emp_data;
import ru.gamingcore.staffstats.json.Emp_rating;
import ru.gamingcore.staffstats.network.ServerWork;

public class MyService extends Service {
    private static final String TAG = "INWIKE";

    private final LocalBinder localBinder = new LocalBinder();

    public Emp_data emp_data = new Emp_data();
    public Emp_rating emp_rating = new Emp_rating();
    public ServerWork serverWork = new ServerWork();
    private LocationManager locationManager;
    private MyLocationListener locationListener;
    private EventListener eventListener;
    private List<Detail> details;
    private List<Avail> avails;
    private ServerWork.Listener listener = new ServerWork.Listener() {

        @Override
        public void onExec_data(Emp_data emp_data) {
            MyService.this.emp_data = emp_data;
            if (eventListener != null) {
                eventListener.onFinish();
            }
        }

        @Override
        public void onEmp_rating(Emp_rating emp_rating) {
            MyService.this.emp_rating = emp_rating;
            if (eventListener != null) {
                eventListener.onUpdate(emp_rating);
            }
        }

        @Override
        public void onDetails(List<Detail> details) {
            MyService.this.details = details;
            if (eventListener != null) {
                eventListener.onDetails(details);
            }
        }

        @Override
        public void onAvails(List<Avail> avails) {
            MyService.this.avails = avails;
            if (eventListener != null) {
                eventListener.updateAvails(avails);
            }
        }

        @Override
        public void onAuth(Auth uid) {
            if(!uid.fined) {
                if (eventListener != null) {
                    eventListener.onAuthError();
                }
            } else {
                serverWork.current_uid = uid.emp_uid;
                if (eventListener != null) {
                    eventListener.onAuth();
                }
            }
        }

        @Override
        public void onUpload() {
            if (eventListener != null) {
                eventListener.onUpload();
            }
        }

        @Override
        public void onError() {
            if (eventListener != null) {
                eventListener.onError();
            }
        }
    };


    public MyService() {
        serverWork.setListener(listener);
    }

    public void setEventListener(EventListener eventListener) {
        this.eventListener = eventListener;
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

    public interface EventListener {
        void onError();

        void onUpload();

        void onAuth();

        void onAuthError();

        void onUpdate(Emp_rating emp_rating);

        void onDetails(List<Detail> details);

        void updateAvails(List<Avail> avails);

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