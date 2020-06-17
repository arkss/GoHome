package com.example.gohome;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gohome.retrofit2.RetrofitClientInstance;
import com.example.gohome.retrofit2.RetrofitService;
import com.example.gohome.retrofit2.SignupData;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {
    private EditText editId, editPw, editPwConfirm, editName;
    private Button completeBtn;

    private String id, pw, pw_confirm, name;
    private static final String signupTag = "SIGNUP";

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_signup);

        // TODO: IMPLEMENT SIGN UP USING RETROFIT

        // toolbar 생성
        Toolbar toolbar = (Toolbar)findViewById(R.id.signup_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // title 제거
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 생성

        editId = (EditText)findViewById(R.id.signup_id);
        editPw = (EditText)findViewById(R.id.signup_password);
        editPwConfirm = (EditText)findViewById(R.id.signup_confirm_password);
        editName = (EditText)findViewById(R.id.signup_name);
        completeBtn = (Button)findViewById(R.id.signup_complete);

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw = editPw.getText().toString().trim();
                pw_confirm = editPwConfirm.getText().toString().trim();
                id = editId.getText().toString().trim();
                name = editName.getText().toString().trim();

                if(!pw.equals(pw_confirm) || pw.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "비밀번호 확인이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    // TODO: HIGHLIGHTING PASSWORD TEXT
                }
                else {
                    // SingupData로 parsing
                    SignupData data = new SignupData(id, pw, "temp_email@uos.ac.kr", name);

                    // 서버로 데이터 전송
                    Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<JsonObject> request = service.signup(data);

                    // 비동기 처리
                    request.enqueue(new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if(response.isSuccessful()) {
                                Log.d(signupTag, "onResponse");
                                JsonObject json = response.body();
                                Log.d(signupTag, json.get("response").getAsString());
                                if(json.get("response").getAsString().equals("success")) {
                                    // success
                                    Log.d(signupTag, "success: "+json.toString());
                                    Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else if(json.get("response").getAsString().equals("error")) {
                                    // error
                                    Log.d(signupTag, "response: error");
                                    Log.d(signupTag, json.toString());
                                }
                            }
                            else {
                                Log.d(signupTag,"response is not successful");
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.d(signupTag,"communicate failed, msg:"+t.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            // 툴바의 뒤로가기 버튼을 눌렀을 때
            case android.R.id.home :
                finish();
                return true;
            default :

        }
        return super.onOptionsItemSelected(item);
    }

}
