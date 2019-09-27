package ru.gamingcore.staffstats.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import ru.gamingcore.staffstats.finger.AuthorizeDialog;
import ru.gamingcore.staffstats.MyService;
import ru.gamingcore.staffstats.R;


public class MainActivity extends AppCompatActivity {
    private static final String TAG ="INWIKE";

    private static int PERMISSION_REQUEST_CODE = 123456;

    private MyService service;

    private ServiceConnection sConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MyService.LocalBinder)binder).getService();
            service.serverWork.listViolation();

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                        },
                        PERMISSION_REQUEST_CODE);
            } else {
                service.initLocation();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clear_layout);
        Authorize();
    }

    /* Authorize to app */
    private void Authorize() {
        AuthorizeDialog dialog = new AuthorizeDialog();
        dialog.setCancelable(false);
        final Intent intent = new Intent(this, MyService.class);

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                setContentView(R.layout.activity_main);

                fm.unregisterFragmentLifecycleCallbacks(this);

                bindService(intent, sConn, BIND_AUTO_CREATE);
            }
        }, false);
        dialog.show(getSupportFragmentManager(),"AuthorizeDialog");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                service.initLocation();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}