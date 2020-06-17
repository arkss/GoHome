package com.example.ar3;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText editText1;
    private EditText editText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        editText1 = findViewById(R.id.myLat);
        editText2 = findViewById(R.id.myLng);
    }

    public void goToAR(View view)
    {
        Intent intent = new Intent(this, testActivity.class);
        intent.putExtra("lat",editText1.getText().toString());
        intent.putExtra("lng",editText2.getText().toString());
        startActivity(intent);
    }
}
