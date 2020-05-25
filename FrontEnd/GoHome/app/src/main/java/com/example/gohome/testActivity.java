package com.example.gohome;

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
import android.util.Log;
import android.view.Display;
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
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class testActivity extends AppCompatActivity  implements SensorEventListener {

    private CameraManager cameraManager;

    //GPS 표시를 위한 변수, 상수
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;

    private String cameraId;

    private float prevPitchMag = 0;
    private float prevAzimuthMag = 0;

    private int pitchIsPlus = 1;
    private float pitchDegrees;
    private double currentDistance;

    private ImageView mPointer;
    private boolean flashOn;
    private boolean isGPSEnabled;
    private boolean isNetEnabled;
    private float angle;

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyro;

    private ModelRenderable arrowRenderable;

    private ArFragment arFragment;
    private ArSceneView arSceneView;
    private AnchorNode prevAnchorNode;
    private Pose myPose, tmpPose, planePose, dPose;

    private float azimuthinDegress;

    private TextView tv1, tv2, tv3;

    private Location myLocation = new Location("myLoc");
    private Location targetLocation;

    private int currentCheckPoint = 0;

    private float[] noAccel = {0,3,3};

    //위도 경도 형식으로 받아온 배열값
    private double[][] gpsNodePoint = {
            {37.586488, 127.054158} // 전농파출소 위도, 경도
            //{ 37.583484, 127.054825 } // 정문 세븐일레븐
            //{ 37.4219983, -127.084}
    };

    ArrayList<Double> nodeDistance = new ArrayList<>();

    //나침반을 위한 변수들
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    private float mCurrentDegree = 0f;

    private android.hardware.Camera camera = null;
    private Node node = new Node();
    //권한 체크
    private void checkPermission() {
        try {
            //권한 얻기 - GPS
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
        }
        catch(Exception e) {
            Log.e("TEST ACTIVITY", e.getMessage());
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

        for(int i = 0; i < gpsNodePoint.length ; i++) {
            nodeDistance.add(1000.0);
        }

        if(currentCheckPoint >= gpsNodePoint.length) {
            return;
        }

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        tv1 = (TextView) findViewById(R.id.myPose);
        tv2 = (TextView) findViewById(R.id.anchorPose);
        tv3 = (TextView) findViewById(R.id.text3);
        cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);

        tv2.setText("다음 포인트까지 남은 거리");
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
                    } catch(InterruptedException | ExecutionException e) {
                        e.getStackTrace();
                    }
                    return null;
                });

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
    }

    //가속도, 자기장 센서 값 받아오기
    @Override
    public void onSensorChanged(SensorEvent event) {
        //두 센서값을 다 받아왔을 때 값 계산
        if(event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if(event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }

        /*
        mLastAccelerometer : 가속도 센서값 => {Azimuth 가속도, Pitch 자기장}
        mLastMagnetometer  : 자기장 센서값 => {Azimuth 가속도, Pitch 자기장}
         */
        if(mLastAccelerometerSet && mLastMagnetometerSet) {

            //SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getRotationMatrix(mR, null, noAccel, mLastMagnetometer);
            //SensorManager.getRotationMatrix(mR,null,null,mLastMagnetometer);
            if (mLastMagnetometer[1] >= 0) {
                pitchIsPlus = -1;
            }
            else {
                pitchIsPlus = 1;
            }

            //azimuth 가 너무 갑자기 변하면
            if(true) {
                azimuthinDegress = (int) (Math.toDegrees(SensorManager.getOrientation(mR, mOrientation)[0]) + 360) % 360;
            }

            //pitch가 너무 갑자기 변하면
            if(true) {
                pitchDegrees = (int) (Math.toDegrees(SensorManager.getOrientation(mR, mOrientation)[2]));
            }

            //tv1.setText(Float.toString(azimuthinDegress) + " and " + Float.toString(pitchDegrees) + " and " + Float.toString(mLastMagnetometer[0]) + " and " + Float.toString( mLastMagnetometer[1]) );

            prevAzimuthMag = mLastMagnetometer[0];
            prevPitchMag = mLastMagnetometer[1];

            //나침반 그림 방향 회전
            RotateAnimation ra = new RotateAnimation(
                    -mCurrentDegree,
                    -azimuthinDegress,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );

            ra.setDuration(1000);
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
        Camera camera = arSceneView.getScene().getCamera();

        //target 위치에 모델 세우기
        targetLocation = getNextLocation(gpsNodePoint[currentCheckPoint][0], gpsNodePoint[currentCheckPoint][1]);

        angle = (float) gpsToDegree(myLocation, targetLocation);

        //체크포인트 표시
        if(Math.abs(angle - azimuthinDegress) <= 5 && currentDistance <= 100) {
            node.setParent(arSceneView.getScene());
            node.setRenderable(arrowRenderable);
            Ray ray = camera.screenPointToRay(1000/2f , 1920/2f);

            Vector3 newPosition = ray.getPoint((float)(currentDistance/20));

            node.setLocalPosition(newPosition);
        }

        //내 현재 위치 구하기, 다음 체크포인트까지의 거리 계산
        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        checkPermission();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,1, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,1, locationListener);

        //현재 위치 기반으로 다음 체크포인트까지 남은 위치 계산
        currentDistance = getDistance(myLocation, getNextLocation(gpsNodePoint[currentCheckPoint][0], gpsNodePoint[currentCheckPoint][1]));
        tv3.setText(Math.round(currentDistance*100)/100.0+" m");

        //for문 돌면서 체크포인트를 뛰어넘었는지 계산
        for(int i = 0 ; i<gpsNodePoint.length; i++) {
            nodeDistance.set(i, getDistance(myLocation,getNextLocation(gpsNodePoint[i][0], gpsNodePoint[i][1])));

            //특정 점이 현재 점보다 다음 체크포인트이고, 그 점이 오히려 더 가까워졌다면
            if(nodeDistance.get(i) <= currentDistance && i > currentCheckPoint) {
                currentCheckPoint = i;
            }
        }

        //평면 지속적으로 탐색
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
        for (Plane plane : planes) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {
                try {

                    //센서를 통해서 내 위치 계산후 방위각 계산
                    myPose = frame.getAndroidSensorPose();
                    planePose = plane.getCenterPose();
                    tmpPose = myPoseToNewPose(myPose, planePose);

                    //평면에 내가 바라보는 방향으로 Anchor 생성
                    Anchor anchor = plane.createAnchor(tmpPose);
                    if (prevAnchorNode != null) {
                        prevAnchorNode.getAnchor().detach();
                    }

                    //AnchorNode에 Model을 세움
                    prevAnchorNode = makeArrow(anchor, angle);
                }
                catch (Exception e) {
                    Log.e("Error is detected : ",e.getMessage());
                    break;
                }
            }
        }
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
            cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
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
        /*
        try {
            cameraManager.setTorchMode(cameraId, false);
        }
        catch(CameraAccessException e) {
            e.printStackTrace();
            finish();
        }
        */
        finish();
    }

    //손전등 켜기 버튼
    public void flashClick(View view) {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(getApplicationContext(), "No camera\n",Toast.LENGTH_LONG).show();
            delayedFlash();
            return;
        }
        flashlight();
    }

    //위도 경도에 따른 Location 값으로 변환
    private Location getNextLocation(double lat, double lng) {
        Location location = new Location("nextLoc");

        location.setLatitude(lat);
        location.setLongitude(lng);

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
        if(Math.sin(targetLng - myLng) < 0) {
            true_bearing = radian_bearing * (180 / 3.141592);
            true_bearing = 360 - true_bearing;
        }
        else {
            true_bearing = radian_bearing * (180 / 3.141592);
        }
        return true_bearing;
    }

    //내 위치를 화살표가 잘보이는 위치로 변경
    private Pose myPoseToNewPose(Pose myPose, Pose tPose) {
        if(myPose == null) {
            myPose = arFragment.getArSceneView().getArFrame().getAndroidSensorPose();
        }
        dPose = arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose();
        float[] tr = {dPose.tx(), tPose.ty(), dPose.tz()};
        float [] rt = {dPose.qx(), dPose.qy(), dPose.qz(), dPose.qw()};

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
    private AnchorNode makeArrow(Anchor anchor, float angle) {
        AnchorNode anchorNode = new AnchorNode(anchor);

        ModelRenderable.builder()
                .setSource(this, Uri.parse("arrow4.sfb"))
                .build()
                .thenAccept(modelRenderable -> addToModelScene(anchor, anchorNode, modelRenderable, angle))
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(throwable.getMessage())
                            .show();
                    return null;
                });
        return anchorNode;
    }

    //찾은 앵커에 3D 모델을 만듦
    private void addToModelScene(Anchor anchor, AnchorNode anchorNode, ModelRenderable modelRenderable, float angle) {
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());

        //tv2.setText("목표 방위각 : "+ Float.toString(angle));
        Vector3 tmpVector1 = new Vector3(0, planePose.ty(),0);
        Vector3 tmpVector2 = new Vector3(0, dPose.ty(), 0);

        Quaternion quaternion4 = Quaternion.rotationBetweenVectors(tmpVector2,tmpVector1);
        Quaternion quaternion1 = Quaternion.axisAngle(new Vector3(0,-1,0),360-azimuthinDegress + angle);
        //Quaternion quaternion2 = Quaternion.axisAngle(new Vector3(1,0,0), pitchDegrees * -pitchIsPlus + 20);
        Quaternion quaternion2 = Quaternion.axisAngle(new Vector3(-1,0,0),pitchIsPlus*20);
        Quaternion quaternion3 = Quaternion.multiply(quaternion1, quaternion2);

        transformableNode.setWorldRotation(quaternion3);
        Vector3 tmpVector3 = transformableNode.getWorldPosition();
        tmpVector3.y = planePose.ty();
        transformableNode.setWorldPosition(tmpVector3);

        transformableNode.setParent(anchorNode);
        transformableNode.select();
        transformableNode.setRenderable(modelRenderable);

        arFragment.getArSceneView().getScene().addChild(anchorNode);
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