package ru.gamingcore.staffstats.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.gamingcore.staffstats.MyService;
import ru.gamingcore.staffstats.R;
import ru.gamingcore.staffstats.finger.AuthorizeDialog;
import ru.gamingcore.staffstats.json.Detail;
import ru.gamingcore.staffstats.json.Emp_rating;
import ru.gamingcore.staffstats.tabs.ScreenSlidePagerAdapter;

import static android.hardware.camera2.CameraCharacteristics.LENS_FACING;
import static android.hardware.camera2.CameraMetadata.LENS_FACING_FRONT;
import static ru.gamingcore.staffstats.utils.Avatar.setAvatar;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "INWIKE";
    private static final int SET_AVATAR_CODE = 777;
    private static final int sImageFormat = ImageFormat.YUV_420_888;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static int PERMISSION_REQUEST_CODE = 123456;
    private TextureView textureView;


    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
        }
    };
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener =
            new ImageReader.OnImageAvailableListener() {

                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image img = reader.acquireLatestImage();
                    try {
                        if (img == null) throw new NullPointerException("cannot be null");

                        ByteBuffer buffer = img.getPlanes()[0].getBuffer();
                        byte[] data = new byte[buffer.remaining()];
                        //TODO facccce
                        buffer.get(data);

                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    } finally {
                        if (img != null)
                            img.close();
                    }
                }

            };
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
    private ImageReader mImageReader;
    private String mCameraId;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private Handler mBackgroundHandler;
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    private HandlerThread mBackgroundThread;
    private MyService service;
    private Bitmap tmp;
    private ViewPager pager;
    private View main;
    private ScreenSlidePagerAdapter pagerAdapter;
    private TextView firstname, lastname, secondname;
    private TextView organization, department, position, type, schedule, exp;
    private ImageView photo;

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
            exp.setText(emp_rating.exp_emp);
        }

        @Override
        public void onDetail(List<Detail> details) {
            pagerAdapter.updateHelp(details);
        }

        @Override
        public void onAvails(List<String> avails) {
            pagerAdapter.updateAvails(avails);
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

    private ServiceConnection sConn = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            service = ((MyService.LocalBinder) binder).getService();
            service.setEventListener(eventListener);
            service.serverWork.execData();
            service.serverWork.empRating();
            service.serverWork.empDetails();
            service.serverWork.empAvail();

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

    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();

        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            if (texture != null) {
                texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());

                Surface surface = new Surface(texture);
                Surface mImageSurface = mImageReader.getSurface();

                List<Surface> outputSurfaces = new ArrayList<>(2);
                outputSurfaces.add(mImageSurface);
                outputSurfaces.add(surface);

                captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                captureRequestBuilder.addTarget(surface);
                captureRequestBuilder.addTarget(mImageSurface);

                cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                        if (null == cameraDevice) {
                            return;
                        }

                        cameraCaptureSessions = cameraCaptureSession;
                        updatePreview();
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    }
                }, null);
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera(int width, int height) {
        Log.e(TAG, "openCamera X");
        setUpCameraOutputs(width, height);

        try {
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }

            CameraManager manager = (CameraManager) getSystemService(CAMERA_SERVICE);
            if (manager != null)
                manager.openCamera(mCameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void setUpCameraOutputs(int width, int height) {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        if (manager != null) {
            try {
                for (String cameraId : manager.getCameraIdList()) {
                    CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                    if (characteristics.get(LENS_FACING) == null || characteristics.get(LENS_FACING) == LENS_FACING_FRONT)
                        continue;

                    StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    if (map != null) {
                        Size min = getFullScreenPreview(map.getOutputSizes(sImageFormat), width, height);

                        mImageReader = ImageReader.newInstance(min.getWidth(), min.getHeight(), sImageFormat, 2);
                        mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mBackgroundHandler);

                        imageDimension = getFullScreenPreview(map.getOutputSizes(SurfaceTexture.class),
                                width, height);
                        Log.e(TAG, "imageDimension w = " + imageDimension.getWidth()
                                + " h = " + imageDimension.getHeight());

                    }
                    mCameraId = cameraId;
                    break;
                }
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "setUpCameraOutputs X");
        }
    }

    protected void updatePreview() {
        if (null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }

        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), captureListener, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startBackgroundThread();
        System.out.println("resume");
        if (textureView.isAvailable()) {
            openCamera(textureView.getWidth(), textureView.getHeight());
            System.out.println("openCamera");
        } else {
            textureView.setSurfaceTextureListener(textureListener);
            System.out.println("textureListener");
        }
    }

    @Override
    protected void onPause() {
        stopBackgroundThread();
        super.onPause();
    }

    private Size getFullScreenPreview(Size[] outputSizes, int width, int height) {
        float difference = 0f;
        Size fullScreenSize = null;
        while (fullScreenSize == null) {
            fullScreenSize = getFullScreenPreviewValue(outputSizes, width, height, null, difference);
            difference = difference + 0.10f;
        }
        return fullScreenSize;
    }

    private Size getFullScreenPreviewValue(Size[] outputSizes, int width, int height, Size fullScreenSize, float difference) {
        List<Size> outputSizeList = Arrays.asList(outputSizes);
        // outputSizeList = sortListInDescendingOrder(outputSizeList); //Some phones available list is in ascending order
        for (int i = 0; i < outputSizeList.size(); i++) {
            int orginalWidth = outputSizeList.get(i).getWidth();
            int orginalHeight = outputSizeList.get(i).getHeight();
            float orginalRatio = (float) orginalWidth / (float) orginalHeight;
            float requiredRatio;
            if (width > height) {
                requiredRatio = ((float) width / height); //for landscape mode
                if ((outputSizeList.get(i).getWidth() > width && outputSizeList.get(i).getHeight() > height)) {
                    //if we select preview size hire than device display resolution it may fail to create capture request
                    continue;
                }
            } else {
                requiredRatio = 1 / ((float) width / height); //for portrait mode
                if ((outputSizeList.get(i).getWidth() > height && outputSizeList.get(i).getHeight() > width)) {
                    //if we select preview size hire than device display resolution it may fail to create capture request
                    continue;
                }
            }
            if ((orginalRatio >= (requiredRatio - difference) && orginalRatio < (requiredRatio + difference))) {
                fullScreenSize = outputSizeList.get(i);
                break;
            }
        }
        return fullScreenSize;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textureView = findViewById(R.id.texture);
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
        exp = findViewById(R.id.exp);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.pager);
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(2);
        pager.addOnPageChangeListener(pagerListener);
        Authorize();
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SET_AVATAR_CODE && resultCode == RESULT_OK) {
            new UpdateAsync().execute(data.getData());
        }
    }

    public void getAvatar(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select avatar"), SET_AVATAR_CODE);
    }


    class UpdateAsync extends AsyncTask<Uri, Void, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (photo != null) {
                photo.setImageResource(R.drawable.update);
            }
        }

        @Override
        protected Bitmap doInBackground(Uri... uri) {
            return setAvatar(getApplicationContext(), uri[0], 1, 100);
        }

        @Override
        protected void onPostExecute(Bitmap bp) {
            if (bp != null) {
                service.serverWork.setPhoto(bp);
                tmp = bp;
            }
        }
    }
}