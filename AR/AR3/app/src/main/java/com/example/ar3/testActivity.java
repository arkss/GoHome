package com.example.ar3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.Pose;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import uk.co.appoly.arcorelocation.LocationMarker;
import uk.co.appoly.arcorelocation.LocationScene;

public class testActivity extends AppCompatActivity  implements SensorEventListener {

    private LocationScene locationScene;
    private CameraManager cameraManager;

    //GPS 표시를 위한 변수, 상수
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;

    private String cameraId;

    private Plane prevPlane;

    private double currentDistance;

    private Node targetNode;
    private ImageView mPointer;
    private boolean flashOn;
    private boolean isGPSEnabled;
    private boolean isNetEnabled;
    private boolean hasFinishedLoading = false;
    private boolean hasFinishedAnchor = false;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;

    private ModelRenderable arrowRenderable;

    private ArFragment arFragment;
    private ArSceneView arSceneView;

    private AnchorNode prevAnchorNode;
    private Pose myPose, anchorPose, tmpPose;

    private float azimuthinDegress;

    private TextView tv1, tv2, tv3, tv4;

    private Location myLocation = new Location("myLoc");
    private Location targetLocation;

    private boolean findFirstAzimuth = false;
    private float firstAzimuth;

    private Boolean Tracking = false;
    //위도 경도 형식으로 받아온 배열값
    private double[][] gpsNodePoint = {
            {37.586488, 127.054158} // 전농파출소 위도, 경도
            //{ 37.583484, 127.054825 } // 정문 세븐일레븐
            //{ 37.4219983, -127.084}
    };
    //나침반을 위한 변수들
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;

    //권한 체크
    private void checkPermission() {
        //권한 얻기 - GPS 1
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        //권한 얻기 - GPS 2
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        //권한 얻기 - GPS 3
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) !=
                PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.INTERNET)) {
            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
            }
            else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }
    }

    //LocationManager 선언
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

            LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            myLocation = location;
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        mPointer = (ImageView)findViewById(R.id.pointer);
        double lat = Double.valueOf(intent.getStringExtra("lat"));
        double lng = Double.valueOf(intent.getStringExtra("lng"));

        gpsNodePoint[0][0] = lat;
        gpsNodePoint[0][1] = lng;

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        tv1 = (TextView) findViewById(R.id.myPose);
        tv2 = (TextView) findViewById(R.id.anchorPose);
        tv3 = (TextView) findViewById(R.id.text3);
        tv4 = (TextView) findViewById(R.id.text4);

        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        CompletableFuture<ModelRenderable> goal = ModelRenderable.builder()
                .setSource(this, Uri.parse("circle.sfb"))
                .build();

        CompletableFuture.allOf(goal)
                .handle((notUsed, throwable) -> {
                    if(throwable != null) {
                        return null;
                    }
                    try {
                        arrowRenderable = goal.get();
                        hasFinishedLoading = true;
                    } catch(InterruptedException | ExecutionException e) {
                        e.getStackTrace();
                    }
                    return null;
                });
        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if(event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if(mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            azimuthinDegress  = (int) ( Math.toDegrees( SensorManager.getOrientation( mR, mOrientation)[0] ) + 360 ) % 360;
            tv2.setText("현재 방위각 : " + Float.toString(azimuthinDegress) + " degrees");
            RotateAnimation ra = new RotateAnimation(
                    -mCurrentDegree,
                    -azimuthinDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );
            ra.setDuration(250);
            ra.setFillAfter(true);
            mPointer.startAnimation(ra);
            mCurrentDegree = -azimuthinDegress;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void onUpdate(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        arSceneView = arFragment.getArSceneView();

        //target 위치에 모델 세우기
        targetLocation = getNextLocation(gpsNodePoint);
        if(locationScene == null) {
            targetNode = makeModelByGPS(targetLocation);
        }
        //내 현재 위치 구하기
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        checkPermission();

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,1, locationListener);

        currentDistance = getDistance(myLocation, targetLocation);

        tv4.setText("남은 거리 : "+Math.round(currentDistance*100)/100.0+" m");

        if(frame == null) {
            return;
        }

        if(locationScene != null) {
            locationScene.processFrame(frame);
        }

        if(frame.getCamera().getTrackingState() != TrackingState.TRACKING) {
            return;
        }

        tv3.setText("XXX");
        //트래킹에 성공할때까지 돌기
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
        for (Plane plane : planes)
        {
            if(plane.getTrackingState() == TrackingState.TRACKING)
            {
                tv3.setText("찾음");
                //센서를 통해서 내 위치 계산후 방위각 계산
                myPose = frame.getAndroidSensorPose();
                float angle = (float) gpsToDegree(myLocation, targetLocation);
                //tv1.setText(Float.toString(angle));


                if(prevAnchorNode != null) {
                    prevAnchorNode.getAnchor().detach();
                }
                Pose tPose = plane.getCenterPose();
                tmpPose = myPoseToNewPose(myPose, tPose, angle);
                Anchor anchor = plane.createAnchor(tmpPose);
                prevAnchorNode = makeArrow(anchor, angle, tmpPose);
                 if (!hasFinishedAnchor) {
                    hasFinishedAnchor = true;
                }
            }
        }
    }

    private Quaternion moveToQuaternion(Location myLocation, Location targetLocation) {
        float angle = (float)gpsToDegree(myLocation, targetLocation);
        Quaternion quaternion = new Quaternion();
        quaternion.set(new Vector3(0,0,-1), angle);

        return quaternion;
    }

    //Location에 따라 GPS 상에 모델링된 이미지를 띄움
    private Node makeModelByGPS(Location location) {
        arSceneView = arFragment.getArSceneView();
        Node node = makeArrow2();

        //Vector3 tmpVector3 = node.getWorldPosition();

        //tv2.setText(tmpVector3.toString());

        locationScene = new LocationScene(this, arSceneView);
        locationScene.mLocationMarkers.add(
                new LocationMarker(
                        location.getLongitude(), location.getLatitude(), node
                )
        );
        //tv2.setText();
        return node;
    }

    //에러 시 n초 후 종료
    private void delayedFlash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },3500);
    }

    //후면카메라 플래시 켜기
    private void flashlight() {
        if(cameraId == null) {
            try {
                for (String id : cameraManager.getCameraIdList()) {
                    CameraCharacteristics c= cameraManager.getCameraCharacteristics(id);
                    Boolean flashAvailable = c.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                    Integer lens = c.get(CameraCharacteristics.LENS_FACING);

                    if (flashAvailable != null && flashAvailable && lens != null && lens == CameraCharacteristics.LENS_FACING_BACK) {
                        cameraId = id;
                        break;
                    }
                }
            }
            catch(CameraAccessException e) {
                cameraId = null;
                e.printStackTrace();
                return;
            }
        }

        flashOn = !flashOn;
        tv2.setText(Boolean.toString(flashOn));
        try {
            cameraManager.setTorchMode(cameraId, flashOn);
        }
        catch(CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //돌아가기 버튼
    public void onClick2(View view)
    {
        try {
            cameraManager.setTorchMode("0", false);
        }
        catch(CameraAccessException e) {
            e.printStackTrace();
            finish();
        }
        finish();
    }

    public void flashClick(View view) {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(getApplicationContext(), "No camera\n",Toast.LENGTH_LONG).show();

            delayedFlash();
            return;
        }
        flashlight();
    }

    private Location getNextLocation(double[][] gpsNodePoint) {
        Location location = new Location("nextLoc");

        location.setLatitude(gpsNodePoint[0][0]);
        location.setLongitude(gpsNodePoint[0][1]);

        return location;
    }

    //내 위치에서 원하는 위치로 갈 때 회전각 계산
    private double gpsToDegree(Location myLocation, Location targetLocation) {

        //내 위치 위도 경도를 라디안으로 변환
        double myLat = myLocation.getLatitude() * (3.141592 / 180);
        double myLng = myLocation.getLongitude() * (3.141592 / 180);

        //타겟 위치 위도 경도를 라디안으로 변환
        double targetLat = targetLocation.getLatitude() * (3.141592 / 180);
        double targetLng = targetLocation.getLongitude() * (3.141592 / 180);

        //라디안 거리 계산
        double radian_distance = 0;
        radian_distance = Math.acos(
                Math.sin(myLat) * Math.sin(targetLat)
                        + Math.cos(myLat) * Math.cos(targetLat) * Math.cos(myLng - targetLng));

        double radian_bearing = Math.acos((Math.sin(targetLat) - Math.sin(myLat) * Math.cos(radian_distance)) / (0.00001+ (Math.cos(myLat) * Math.sin(radian_distance))));

        double true_bearing = 0;

        if(Math.sin(targetLng - myLng) < 0)
        {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        }
        else
        {
            true_bearing = radian_bearing * (180 / 3.141592);
        }

        return true_bearing;
    }

    //내 위치를 화살표가 잘보이는 위치로 변경
    private Pose myPoseToNewPose(Pose myPose, Pose tPose, float angle) {
        if(myPose == null) {
            myPose = arFragment.getArSceneView().getArFrame().getAndroidSensorPose();
        }
        float[] tr = {myPose.tx(), tPose.ty(), myPose.tz()};
        float[] rt = {tPose.qx(), tPose.qy(), tPose.qz(), tPose.qw()};

        Pose tmpPose = new Pose(tr, rt);

        return tmpPose;
    }

    //두 위도 경도 주어지면 거리로 변환
    private double getDistance(Location myLocation, Location targetLocation){
        double lat1 = myLocation.getLatitude();
        double lat2 = targetLocation.getLatitude();

        double lng1 = myLocation.getLongitude();
        double lng2 = targetLocation.getLongitude();

        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lng1);

        Location locationB = new Location("point B");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lng2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    //이미지 만들기 - 방향 표시 화살표
    private AnchorNode makeArrow(Anchor anchor, float angle, Pose tPose) {
        AnchorNode anchorNode = new AnchorNode(anchor);

        //anchorNode.setWorldPosition(new Vector3(0,0,-1));
        //anchorNode.setWorldRotation(moveToQuaternion(myLocation,targetLocation));

        ModelRenderable.builder()
                .setSource(this, Uri.parse("arrow4.sfb"))
                .build()
                .thenAccept(modelRenderable -> addToModelScene(anchor, anchorNode, modelRenderable, angle, tPose))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .show();
                    return null;
                });
        return anchorNode;
    }

    //이미지 만들기 - GPS 상에 띄울 위치 표시
    private Node makeArrow2() {
        Node node = new Node();
        node.setRenderable(arrowRenderable);
        Context c = this;

        return node;
    }

    //찾은 앵커에 3D 모델을 만듦
    private void addToModelScene(Anchor anchor, AnchorNode anchorNode, ModelRenderable modelRenderable, float angle, Pose tPose) {
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);


        tv1.setText(Float.toString(angle));
        transformableNode.setRenderable(modelRenderable);
        transformableNode.setLocalPosition(new Vector3(0,0,0));
        //transformableNode.setLocalRotation(Quaternion.axisAngle(new Vector3(0,1,0),0f));
        transformableNode.setWorldRotation(Quaternion.axisAngle(new Vector3(0,1,0), angle));
        float[] tmp = myPose.getTranslation();
        //Vector3 vector3 = new Vector3(tmp[0], 0, tmp[2]);
        //transformableNode.setLocalPosition(vector3);
        //transformableNode.setWorldPosition(mVector3);
        arFragment.getArSceneView().getScene().addChild(anchorNode);

        transformableNode.select();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }
}
