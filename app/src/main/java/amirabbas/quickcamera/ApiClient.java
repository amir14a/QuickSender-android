package amirabbas.quickcamera;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class ApiClient {
    private static APIInterfaces api;

    public static APIInterfaces getInstance() {

        if (Consts.baseURL.equals("0") || api == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(40, TimeUnit.SECONDS)
                    .writeTimeout(120, TimeUnit.SECONDS)
                    .build();
            String baseURL = Consts.baseURL;
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .client(client)
                    .build();

            api = retrofit.create(APIInterfaces.class);
        }
        return api;
    }

    public static APIInterfaces getInstanceForCheck(String ip) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ip)
                .client(client)
                .build();

        return retrofit.create(APIInterfaces.class);
    }
}
