package com.uos.gohome;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class testActivity extends AppCompatActivity implements SensorEventListener {

    //UI 변수
    private TextView tv1, tv2, tv3;

    //센서 변수
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float azimuthinDegress; // y축 각도
    private float[] noAccel = {0, 3, 3};
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];

    //AR 변수
    private ModelRenderable arrowRenderable;
    private ArFragment arFragment;
    private ArSceneView arSceneView;
    private AnchorNode prevAnchorNode;
    private Node node = new Node();

    //GPS 변수
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;

    private Location myLocation = new Location("myLoc");
    private Location targetLocation, lastLocation;

    private int count = 0; // count 변수

    //위도 경도 형식으로 받아오는 배열값

    ArrayList<double[]> gpsNodePointArrayList = new ArrayList<>();
    private double[][] gpsNodePoint = {
            //{37.586488, 127.054158} // 전농파출소 위도, 경도
            //{ 37.583484, 127.054825 } // 정문 세븐일레븐
            {37.583634610676576, 127.05422474709316},
            {37.583634610676576, 127.05422474709316}
    };

    //권한 체크
    private void checkPermission() {
        try {
            //권한 얻기 - GPS
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS
                    );
                }
            }
        } catch (Exception e) {
            Log.e("PERMISSION DENIED", e.getMessage());
        }
    }

    //LocationManager 선언
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
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

        //텍스트뷰 초기화
        tv1 = (TextView) findViewById(R.id.myPose);
        tv2 = (TextView) findViewById(R.id.anchorPose);
        tv3 = (TextView) findViewById(R.id.text3);

        //Intent 값 받아오기
        Intent intent = getIntent();
        ArrayList<double[]> gpsNodePointArrayList2 = (ArrayList<double[]>) intent.getSerializableExtra("points");

        //GPS 정보 초기화
        if (gpsNodePointArrayList2 != null) {
            //intent 잘 받아오면 넣기
            tv1.setText("AR 경로 안내");
            gpsNodePointArrayList = gpsNodePointArrayList2;
        } else {
            //아닐 경우 기본 값으로 설정
            tv1.setText("AR 경로 탐색");
            gpsNodePointArrayList.addAll(Arrays.asList(gpsNodePoint));
        }

        //센서 초기화
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //체크포인트 AR 이미지 초기화
        CompletableFuture<ModelRenderable> goal = ModelRenderable.builder()
                .setSource(this, Uri.parse("circle.sfb"))
                .build();

        CompletableFuture.allOf(goal)
                .handle((notUsed, throwable) ->
                {
                    if (throwable != null) {
                        return null;
                    }
                    try {
                        arrowRenderable = goal.get();
                    } catch (InterruptedException | ExecutionException e) {
                        e.getStackTrace();
                    }
                    return null;
                });

        //AR 화면 실행
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
    }

    //가속도, 자기장 센서 값 받아오기
    @Override
    public void onSensorChanged(SensorEvent event) {
        //두 센서값을 다 받아왔을 때 값 계산
        if (event.sensor == mAccelerometer) {
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }

        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, noAccel, mLastMagnetometer);
            //azimuth 가 너무 갑자기 변하면
            azimuthinDegress = (int) (Math.toDegrees(SensorManager.getOrientation(mR, mOrientation)[0]) + 360) % 360;
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
        targetLocation = getNextLocation(gpsNodePointArrayList.get(0)[0], gpsNodePointArrayList.get(0)[1]);
        lastLocation = getNextLocation(gpsNodePointArrayList.get(gpsNodePointArrayList.size() - 1)[0], gpsNodePointArrayList.get(gpsNodePointArrayList.size() - 1)[1]);

        //target 위치와 현재 위치 간 각도 및 거리 계산
        float angle = (float) gpsToDegree(myLocation, targetLocation);
        double currentDistance = getDistance(myLocation, getNextLocation(gpsNodePointArrayList.get(0)[0], gpsNodePointArrayList.get(0)[1]));
        double lastDistance = getDistance(myLocation, lastLocation);

        tv2.setText("체크포인트 : " + gpsNodePointArrayList.size() + " 개");
        tv3.setText("남은 거리 : " + Math.round(lastDistance * 100) / 100.0 + " m");

        //체크포인트 표시
        if (Math.abs(angle - azimuthinDegress) <= 10 && currentDistance <= 5) {
            node.setParent(arSceneView.getScene());
            node.setRenderable(arrowRenderable);
            Ray ray = camera.screenPointToRay(1000 / 2f, 500);

            Vector3 newPosition = ray.getPoint((float) (currentDistance / 5));
            node.setLocalPosition(newPosition);
        } else {
            node.setRenderable(null);
        }

        //내 현재 위치 myLocation 구하기
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkPermission();
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);
        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, locationListener);

        //일정 거리 이상 가까이 오면 다음 체크포인트로
        if (currentDistance <= 4) {
            gpsNodePointArrayList.remove(0);
        }

        double tmp2 = getDistance(targetLocation, lastLocation);
        if (tmp2 > lastDistance) {
            gpsNodePointArrayList.remove(0);
        }

        //남은 체크포인트 중 지나친 체크포인트 체크
        try {
            if (gpsNodePointArrayList.size() > 0) {
                for (int i = 0; i < gpsNodePointArrayList.size(); i++) {
                    double tmp = getDistance(myLocation, getNextLocation(gpsNodePointArrayList.get(i)[0], gpsNodePointArrayList.get(i)[1]));
                    //다음 점이 더 가깝고 일정 거리 이하라면
                    if (currentDistance > tmp) {
                        for (int j = 0; j <= i; j++) {
                            gpsNodePointArrayList.remove(j);
                        }
                        break;
                    }
                }
            } else {
                Toast.makeText(this, "경로 탐색 완료!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            finish();
            Toast.makeText(this, "경로 탐색 종료", Toast.LENGTH_SHORT).show();
        }

        //평면 지속적으로 탐색
        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
        Pose planePose, tmpPose;
        try {
            for (Plane plane : planes) {
                if (plane.getTrackingState() == TrackingState.TRACKING) {
                    if (count-- <= 0) {
                        //센서를 통해서 평면 위치 계산후 방위각 계산
                        planePose = plane.getCenterPose();
                        tmpPose = myPoseToNewPose(planePose);

                        //평면에 내가 바라보는 방향으로 Anchor 생성
                        Anchor anchor = plane.createAnchor(tmpPose);
                        if (prevAnchorNode != null) {
                            prevAnchorNode.getAnchor().detach();
                        }

                        //AnchorNode에 Model을 만듦
                        prevAnchorNode = makeArrow(anchor, angle);
                        count = 50;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Error is detected : ", e.getMessage());
        }
    }

    //돌아가기 버튼
    public void onClick2(View view) {
        finish();
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
        double radian_distance = Math.acos(
                Math.sin(myLat) * Math.sin(targetLat)
                        + Math.cos(myLat) * Math.cos(targetLat) * Math.cos(myLng - targetLng));

        //라디안 거리 => 라디안 각 => 각도 변환
        double radian_bearing = Math.acos((Math.sin(targetLat) - Math.sin(myLat) * Math.cos(radian_distance)) / (0.00001 + (Math.cos(myLat) * Math.sin(radian_distance))));
        double true_bearing = radian_bearing * (180 / 3.141592);
        if (Math.sin(targetLng - myLng) < 0) {
            true_bearing = 360 - true_bearing;
        }

        return true_bearing;
    }

    //내 위치를 화살표가 잘보이는 위치로 변경
    private Pose myPoseToNewPose(Pose planePose) {
        //안드로이드 화면이 바라보는 방향의 Pose 추출
        Pose dPose = arFragment.getArSceneView().getArFrame().getCamera().getDisplayOrientedPose();

        //평면의 Pose와 화면 계산
        float[] tmpVec = {dPose.tx(), planePose.ty(), dPose.tz()};
        return Pose.makeTranslation(tmpVec);
    }

    //두 위도 경도 주어지면 거리로 변환
    private double getDistance(Location myLocation, Location targetLocation) {
        //위도 추출
        double lat1 = myLocation.getLatitude();
        double lat2 = targetLocation.getLatitude();

        //경도 추출
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
                .exceptionally(throwable ->
                {
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
        float rotateAngle = (-azimuthinDegress + angle) % 360;

        //tv2.setText(Float.toString(angle)+ " and "+Float.toString(azimuthinDegress)+" and "+Float.toString(rotateAngle));

        Quaternion quaternion1 = Quaternion.axisAngle(new Vector3(0, -1, 0), rotateAngle);
        transformableNode.setWorldRotation(quaternion1);

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