package com.example.gohome;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gohome.retrofit2.LoginData;
import com.example.gohome.retrofit2.RetrofitClientInstance;
import com.example.gohome.retrofit2.RetrofitService;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);

        Button signInBtn = (Button)findViewById(R.id.login_signIn);
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
//                // parse LoginData
//                EditText etId = (EditText)findViewById(R.id.login_id);
//                EditText etPw = (EditText)findViewById(R.id.login_password);
//                String id = etId.getText().toString().trim();
//                String pw = etPw.getText().toString().trim();
//                LoginData data = new LoginData(id, pw);
//
//                final Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
//                final RetrofitService service = retrofit.create(RetrofitService.class);
//                Call<JsonObject> request = service.login(data);
//
//                // 비동기 처리
//                request.enqueue(new Callback<JsonObject>() {
//                    private static final String loginTag = "LOGIN";
//
//                    @Override
//                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                        if(response.isSuccessful()) {
//                            Log.d(loginTag, "login-response successful");
//                            JsonObject json = response.body();
//
//                            // get token
//                            String token = "JWT "+json.get("token").getAsString();
//                            Log.d(loginTag, "TOKEN: "+token);
//
//                            // login confirm, token값 확인하여 로그인
//                            Call<JsonObject> request = service.loginConfirm(token);
//                            request.enqueue(new Callback<JsonObject>() {
//                                @Override
//                                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                                    if(response.isSuccessful()) {
//                                        Log.d(loginTag, "token-response successful");
//                                        JsonObject js = response.body();
//                                        if(js.get("message").getAsString().equals("로그인 성공")) {
//                                            Toast.makeText(LoginActivity.this, "login success", Toast.LENGTH_SHORT).show();
//
//                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    }
//                                    else {
//                                        Log.d(loginTag, "token-response fail");
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<JsonObject> call, Throwable t) {
//                                    Log.d(loginTag, "token-communication failed, msg: "+t.getMessage());
//                                }
//                            });
//
//                        }
//                        else {
//                            Log.d(loginTag, "login-response failed");
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonObject> call, Throwable t) {
//                        Log.d(loginTag, "login-communication failed, msg: "+t.getMessage());
//                    }
//                });
            }
        });

        Button signUpBtn = (Button)findViewById(R.id.login_signUp);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}
