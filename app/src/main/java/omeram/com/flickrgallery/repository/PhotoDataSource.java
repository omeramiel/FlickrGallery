package omeram.com.flickrgallery.repository;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PageKeyedDataSource;
import android.support.annotation.NonNull;

import java.io.IOException;

import omeram.com.flickrgallery.BuildConfig;
import omeram.com.flickrgallery.api.FlickrApi;
import omeram.com.flickrgallery.model.DataLoadState;
import omeram.com.flickrgallery.model.FlickrResponse;
import omeram.com.flickrgallery.model.Photo;
import omeram.com.flickrgallery.model.PhotosInfo;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

public class PhotoDataSource extends PageKeyedDataSource<Integer, Photo> {

    private FlickrApi flickrApi;

    public final MutableLiveData<DataLoadState> loadState;

    PhotoDataSource(FlickrApi flickrApi) {
        this.flickrApi = flickrApi;
        loadState = new MutableLiveData<>();
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Integer> params, @NonNull LoadInitialCallback<Integer, Photo> callback) {
        Timber.d("loadInitial page 1 per_page " + params.requestedLoadSize);
        loadState.postValue(DataLoadState.LOADING);
        Call<FlickrResponse> request = flickrApi.getRecent(BuildConfig.FLICKR_API_KEY, 1, params.requestedLoadSize);
        Response<FlickrResponse> response;
        try {
            response = request.execute();
            if (response != null) {
                PhotosInfo photosInfo = response.body().getPhotosInfo();
                callback.onResult(photosInfo.getPhotos(), 1, 2);
            } else {
                callback.onResult(null, null, 2);
            }
            loadState.postValue(DataLoadState.LOADED);
        } catch (IOException ex) {
            loadState.postValue(DataLoadState.FAILED);
        }
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Photo> callback) {
        Timber.d("loadBefore page " + params.key + " per_page " + params.requestedLoadSize);
        loadState.postValue(DataLoadState.LOADING);
        Call<FlickrResponse> request = flickrApi.getRecent(BuildConfig.FLICKR_API_KEY, params.key, params.requestedLoadSize);
        Response<FlickrResponse> response;
        try {
            response = request.execute();
            if (response != null) {
                Integer adjacentKey = (params.key > 1) ? params.key - 1 : null;
                PhotosInfo photosInfo = response.body().getPhotosInfo();
                callback.onResult(photosInfo.getPhotos(), adjacentKey);
            } else {
                callback.onResult(null, params.key - 1);
            }
            loadState.postValue(DataLoadState.LOADED);
        } catch (IOException ex) {
            //networkState.postValue();
        }
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Photo> callback) {
        Timber.d("loadAfter page " + params.key + " per_page " + params.requestedLoadSize);
        loadState.postValue(DataLoadState.LOADING);
        Call<FlickrResponse> request = flickrApi.getRecent(BuildConfig.FLICKR_API_KEY, params.key, params.requestedLoadSize);
        Response<FlickrResponse> response;
        try {
            response = request.execute();
            if (response != null) {
                PhotosInfo photosInfo = response.body().getPhotosInfo();
                callback.onResult(photosInfo.getPhotos(), params.key + 1);
            } else {
                callback.onResult(null, params.key + 1);
            }
            loadState.postValue(DataLoadState.LOADED);
        } catch (IOException ex) {
            //networkState.postValue();
            loadState.postValue(DataLoadState.FAILED);
        }
    }
}