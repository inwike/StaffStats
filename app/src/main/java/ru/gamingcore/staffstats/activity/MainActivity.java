package ru.gamingcore.staffstats.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import ru.gamingcore.staffstats.MyService;
import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.finger.AuthorizeDialog;
import ru.gamingcore.staffstats.json.Emp_rating;
import ru.gamingcore.staffstats.tabs.ScreenSlidePagerAdapter;

import static ru.gamingcore.staffstats.utils.Avatar.setAvatar;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "INWIKE";

    private static int PERMISSION_REQUEST_CODE = 123456;
    private static final int SET_AVATAR_CODE = 777;


    private MyService service;
    private Bitmap tmp;

    private ViewPager pager;
    private View main;
    private ScreenSlidePagerAdapter pagerAdapter;

    private TextView firstname, lastname, secondname;
    private TextView organization, department, position,type,schedule;
    private ImageView photo;

    private ServiceConnection sConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MyService.LocalBinder) binder).getService();
            service.setEventListener(eventListener);
            service.serverWork.execData();
            service.serverWork.empRating();

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                        new String[]{
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
        setContentView(R.layout.activity_main);
        main = findViewById(R.id.main);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        secondname = findViewById(R.id.secondname);
        organization = findViewById(R.id.organization);
        department = findViewById(R.id.department);
        position = findViewById(R.id.position);
        photo = findViewById(R.id.photo);
        type = findViewById(R.id.type);
        schedule = findViewById(R.id.schedule);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(2);
        pager.addOnPageChangeListener(pagerListener);
        Authorize();
    }

    /*
    1. средний в должности
    2. мой шестигранник
    3. сравнение по компании, по времени
    4. сравнение по компании, по времени
    */


    /* Authorize to app */
    private void Authorize() {
        AuthorizeDialog dialog = new AuthorizeDialog();
        dialog.setCancelable(false);
        final Intent intent = new Intent(this, MyService.class);

        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentViewDestroyed(fm, f);
                startMain();
                fm.unregisterFragmentLifecycleCallbacks(this);
                bindService(intent, sConn, BIND_AUTO_CREATE);
            }
        }, false);

        dialog.show(getSupportFragmentManager(), "AuthorizeDialog");
    }

    public void onClick(View view) {
        /*if(view.getId() == R.id.read) {
            Intent intent = new Intent(this, QRActivity.class);
            startActivity(intent);
        } else if(view.getId() == R.id.error){
            ViolationFragment  violation = new ViolationFragment();
            violation.service = service;
            violation.show(getSupportFragmentManager(), "Violation");
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                service.initLocation();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void startMain() {
        main.setVisibility(View.VISIBLE);
        final Intent intent = new Intent(this, MyService.class);
        bindService(intent, sConn, BIND_AUTO_CREATE);
    }

    ViewPager.OnPageChangeListener pagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private MyService.EventListener eventListener = new MyService.EventListener() {
        @Override
        public void onError() {
        }

        @Override
        public void onUpload() {
            if (photo != null && tmp != null) {
                photo.setImageBitmap(tmp);
            }
        }

        @Override
        public void onUpdate(Emp_rating emp_rating) {
            pagerAdapter.updateSkills(emp_rating);
        }

        @Override
        public void onFinish() {
            firstname.setText(service.emp_data.firstname);
            lastname.setText(service.emp_data.lastname);
            secondname.setText(service.emp_data.secondname);
            organization.setText(service.emp_data.organization);
            department.setText(service.emp_data.department);
            position.setText(service.emp_data.position);
            photo.setImageBitmap(service.emp_data.photo);
            type.setText(service.emp_data.type);
            schedule.setText(service.emp_data.schedule);
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode,final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_AVATAR_CODE && resultCode == RESULT_OK) {
            new UpdateAsync().execute(data.getData());
        }
    }

    public void getAvatar(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select avatar"), SET_AVATAR_CODE );
    }


    class UpdateAsync extends AsyncTask<Uri,Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (photo != null) {
                photo.setImageResource(R.drawable.update);
            }
        }

        @Override
        protected Bitmap doInBackground(Uri... uri) {
            return setAvatar(getApplicationContext(),uri[0],1,100);
        }

        @Override
        protected void onPostExecute(Bitmap bp) {
            if(bp != null) {
                service.serverWork.setPhoto(bp);
                tmp = bp;
            }
        }
    }
}