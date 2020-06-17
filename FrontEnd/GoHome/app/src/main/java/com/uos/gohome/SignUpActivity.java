package com.uos.gohome;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uos.gohome.retrofit2.RetrofitClientInstance;
import com.uos.gohome.retrofit2.RetrofitService;
import com.uos.gohome.retrofit2.SignupData;
import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUpActivity extends AppCompatActivity {
    private EditText editId, editPw, editPwConfirm, editName, editEmail, editAddress, editAddressDetail;
    private Button completeBtn;

    private String id, pw, pw_confirm, email, name, address, addressDetail;
    private static final String signupTag = "SIGNUP";

    final int addressRequestCode = 0;
    double latitude, longitude;

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
        editEmail = (EditText)findViewById(R.id.signup_email);
        editName = (EditText)findViewById(R.id.signup_name);
        editAddress = (EditText)findViewById(R.id.signup_address);
        editAddressDetail = (EditText)findViewById(R.id.signup_detail_address);
        completeBtn = (Button)findViewById(R.id.signup_complete);

        editAddress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                    startActivityForResult(new Intent(SignUpActivity.this, AddressSearch.class), addressRequestCode);
                return false;
            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw = editPw.getText().toString().trim();
                pw_confirm = editPwConfirm.getText().toString().trim();
                id = editId.getText().toString().trim();
                email = editEmail.getText().toString().trim();
                name = editName.getText().toString().trim();
                address = editAddress.getText().toString().trim();
                addressDetail = editAddressDetail.getText().toString().trim();

                if(!pw.equals(pw_confirm) || pw.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "비밀번호 확인이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
                    // TODO: HIGHLIGHTING PASSWORD TEXT
                } else if(address.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "주소가 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // SingupData로 parsing
                    SignupData data = new SignupData(id, pw, email, name, address, addressDetail, latitude, longitude);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == addressRequestCode) {
            if(resultCode == RESULT_OK) {
                address = data.getStringExtra("address");
                addressDetail = data.getStringExtra("addressDetail");
                latitude = data.getDoubleExtra("latitude", 0);
                longitude = data.getDoubleExtra("longitude", 0);
                editAddress.setText(address);
                editAddressDetail.setText(addressDetail);
            }
        }
    }
}
