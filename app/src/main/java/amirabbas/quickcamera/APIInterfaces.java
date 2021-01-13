package amirabbas.quickcamera;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIInterfaces {


    @Multipart
//    @FormUrlEncoded
    @POST("QuickCamera/aaReciveFile")
    Call<ResponseBody> Upload(
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );

    @GET("QuickCamera/aaCheck")
    Call<ResponseBody> check();


}
