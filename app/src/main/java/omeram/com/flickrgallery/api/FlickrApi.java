package omeram.com.flickrgallery.api;


import omeram.com.flickrgallery.model.FlickrResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface FlickrApi {

    @GET("?method=flickr.photos.getRecent&nojsoncallback=1&format=json&extras=url_s")
    Call<FlickrResponse> getRecent(@Query("api_key") String apiKey, @Query("page") int page, @Query("per_page") int perPage);

    @GET("?method=flickr.photos.search&nojsoncallback=1&format=json&extras=url_s")
    Call<FlickrResponse> searchPhoto(@Query("api_key") String apiKey, @Query("page") int page, @Query("per_page") int perPage, @Query("text") String text);

}
