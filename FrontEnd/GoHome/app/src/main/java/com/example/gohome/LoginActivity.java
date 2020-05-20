package com.example.gohome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gohome.retrofit2.LoginData;
import com.example.gohome.retrofit2.RetrofitClientInstance;
import com.example.gohome.retrofit2.RetrofitService;
import com.google.gson.JsonObject;

public class LoginActivity extends AppCompatActivity {
    private static final String loginTag = "LOGIN";
    private static final int PERMISSION_REQUEST_GPS = 1002;
    private EditText etId, etPw;
    private Button signInBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);

        // GET GPS PERMISSION
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_GPS);
        }

        // init Views
        etId = (EditText)findViewById(R.id.login_id);
        etPw = (EditText)findViewById(R.id.login_password);
        signInBtn = (Button)findViewById(R.id.login_signIn);
        signUpBtn = (Button)findViewById(R.id.login_signUp);

        // TODO: LOADING ACTIVITY


        // event
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: IMPLEMENT LOGIN USING RETROFIT
                // parse LoginData
                String id = etId.getText().toString().trim();
                String pw = etPw.getText().toString().trim();
                LoginData data = new LoginData(id, pw);

                //
                final Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
                final RetrofitService service = retrofit.create(RetrofitService.class);
                Call<JsonObject> request = service.login(data);

                // 비동기 처리
                request.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if(response.isSuccessful()) {
                            Log.d(loginTag, "login-response successful");
                            JsonObject json = response.body();

                            // get token
                            String token = "JWT "+json.get("token").getAsString();
                            Log.d(loginTag, "TOKEN: "+token);

                            // login confirm, token값 확인하여 로그인
                            Call<JsonObject> request = service.loginConfirm(token);
                            request.enqueue(new Callback<JsonObject>() {
                                @Override
                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                    if(response.isSuccessful()) {
                                        Log.d(loginTag, "token-response successful");
                                        JsonObject js = response.body();
                                        if(js.get("message").getAsString().equals("로그인 성공")) {
                                            Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();

                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                    else {
                                        Log.d(loginTag, "token-response fail");
                                    }
                                }

                                @Override
                                public void onFailure(Call<JsonObject> call, Throwable t) {
                                    Log.d(loginTag, "token-communication failed, msg: "+t.getMessage());
                                }
                            });

                        }
                        else {
                            Log.d(loginTag, "login-response failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.d(loginTag, "login-communication failed, msg: "+t.getMessage());
                    }
                });

            }
        });
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int grantResults[]) {
        switch(requestCode) {
            case PERMISSION_REQUEST_GPS :
                // if request is not canceled and permission granted successfully
                if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "GPS 권한이 허가되었습니다", Toast.LENGTH_SHORT).show();
                } else {
                    // if select don't ask again
                    if(!shouldShowRequestPermissionRationale(permissions[0])) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                        builder.setTitle("권한 설정")
                                .setMessage("GoHome을 사용하기 위해서는 GPS 권한을 허가해야 합니다.")
                                .setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // 앱 권한 설정창으로 이동
                                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setData(Uri.parse("package:"+getPackageName()));
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    else {
                        // 단순히 거절을 눌렀을 경우
                        finish();
                    }
                }
                break;
        }
    }
}
