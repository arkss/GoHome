package com.example.gohome;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SignUpActivity extends AppCompatActivity {
    private EditText editId, editPw, editPwConfirm, editName;
    private Button completeBtn;

    private String id, pw, pw_confirm, name;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_signup);

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
                }
                else {
                    // 서버로 데이터 전송
                    Toast.makeText(SignUpActivity.this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
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
