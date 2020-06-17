<<<<<<< HEAD:FrontEnd/GoHome/app/src/main/java/com/uos/gohome/retrofit2/retrofit2/RetrofitClientInstance.java
package com.example.gohome.retrofit2;
=======
package com.uos.gohome.retrofit2;
>>>>>>> client/merge-AR:FrontEnd/GoHome/app/src/main/java/com/uos/gohome/retrofit2/RetrofitClientInstance.java

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    private static Retrofit retrofit;
<<<<<<< HEAD:FrontEnd/GoHome/app/src/main/java/com/uos/gohome/retrofit2/retrofit2/RetrofitClientInstance.java
    private static final String BASE_URL = "http://ec2-15-164-213-121.ap-northeast-2.compute.amazonaws.com:8000/";
=======
    private static final String BASE_URL = "http://ec2-3-34-172-59.ap-northeast-2.compute.amazonaws.com:8000/";
>>>>>>> client/merge-AR:FrontEnd/GoHome/app/src/main/java/com/uos/gohome/retrofit2/RetrofitClientInstance.java

    public static Retrofit getRetrofitInstance() {
        if(retrofit == null) {
            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
