package milligram.adsol.com.milligram.network;

import milligram.adsol.com.milligram.model.EditBio;
import milligram.adsol.com.milligram.model.NetResponse;
import milligram.adsol.com.milligram.model.Photo;
import milligram.adsol.com.milligram.model.User;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by adityasarma on 06/02/18.
 */

public interface RetrofitInterface {


    @POST("users")
    Observable<NetResponse> register(@Body User user);

    @POST("usersauthenticate")
    Observable<NetResponse> login();

    @GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);

    @POST("editbio")
    Observable<NetResponse> editbio(@Body EditBio editBio);

    @POST("uploadphotos")
    Observable<NetResponse> uploadphoto(@Body Photo photo);

    @GET("getphotos")
    Observable<NetResponse> getPhotos();

    @GET("getsingleuserphotos/{name}")
    Observable<NetResponse> getSingleUserPhotos(@Path("name") String name);

    @GET("getsingleuserprofile/{name}")
    Observable<NetResponse> getSingleuserprofile(@Path("name") String name);

    @GET("searchUsersList/{name}")
    Observable<NetResponse> searchUsersList(@Path("name") String name);




}
