//package com.example.gohome.retrofit2;
//
//import android.os.Bundle;
//import android.widget.TextView;
//
//import com.example.gohome.R;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.List;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//
//// 테스트를 위한 액티비티
//public class RetrofitActivity extends AppCompatActivity {
//
//    private TextView userId, id, title, body;
//
//    @Override
//    protected void onCreate(Bundle saveInstanceState) {
//        super.onCreate(saveInstanceState);
//        setContentView(R.layout.activity_retrofit);
//
//        userId = (TextView)findViewById(R.id.retrofit_user_id);
//        id = (TextView)findViewById(R.id.retrofit_id);
//        title = (TextView)findViewById(R.id.retrofit_title);
//        body = (TextView)findViewById(R.id.retrofit_body);
//
//        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
//        RetrofitService service = retrofit.create(RetrofitService.class);
//
//        request.enqueue(new Callback<List<RetrofitData>>() {
//            @Override
//            public void onResponse(Call<List<RetrofitData>> call, Response<List<RetrofitData>> response) {
//                if(response.isSuccessful()) {
//                    // 통신 성공
//                    List<RetrofitData> posts = response.body();
//                    userId.setText(posts.get(0).getUserId().toString());
//                    id.setText(posts.get(0).getId().toString());
//                    title.setText(posts.get(0).getTitle());
//                    body.setText(posts.get(0).getBody());
//                }
//                else {
//                    // error
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<RetrofitData>> call, Throwable t) {
//                // error
//            }
//        });
//    }
//}
