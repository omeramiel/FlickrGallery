package omeram.com.flickrgallery.workers;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.io.IOException;

import javax.inject.Inject;

import androidx.work.Worker;
import omeram.com.flickrgallery.BuildConfig;
import omeram.com.flickrgallery.R;
import omeram.com.flickrgallery.api.FlickrApi;
import omeram.com.flickrgallery.db.FlickrDb;
import omeram.com.flickrgallery.di.AppInjector;
import omeram.com.flickrgallery.model.FlickrResponse;
import omeram.com.flickrgallery.model.PhotosInfo;
import omeram.com.flickrgallery.vo.SearchFlickr;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static omeram.com.flickrgallery.util.Constants.KEY_PHOTO_QUERY;
import static omeram.com.flickrgallery.util.Constants.KEY_PREVIOUS_TOTAL;

public class SearchQueryWorker extends Worker {

    @Inject
    FlickrDb db;

    @Inject
    FlickrApi flickrApi;

    @NonNull
    @Override
    public Worker.WorkerResult doWork() {
        AppInjector.getApplicationComponent().inject(this);
        String query = getInputData().getString(KEY_PHOTO_QUERY, "");
        int previousTotal = getInputData().getInt(KEY_PREVIOUS_TOTAL, 0);
        return searchPhotos(query, previousTotal);
    }

    @WorkerThread
    private WorkerResult searchPhotos(@NonNull String query, int previousTotal) {
        Timber.d("do work. query:" + query + " previousTotal: " + previousTotal);
        Call<FlickrResponse> request = flickrApi.searchPhoto(BuildConfig.FLICKR_API_KEY, 1, 1, query);
        Response<FlickrResponse> response;
        try {
            response = request.execute();
            if (response != null) {
                PhotosInfo photosInfo = response.body().getPhotosInfo();
                if (photosInfo.getTotal() > previousTotal) {
                    int totalNew = photosInfo.getTotal() - previousTotal;
                    String message = getApplicationContext().getString(R.string.new_photos_notification_body) + totalNew;
                    WorkerUtils.makeStatusNotification(message, getApplicationContext());
                }
                replaceSearchQuery(query, photosInfo.getTotal());
            } else {
                return WorkerResult.RETRY;
            }
        } catch (IOException ex) {
            return WorkerResult.RETRY;
        }
        return WorkerResult.SUCCESS;
    }

    @WorkerThread
    private void replaceSearchQuery(String query, int total) {
        try {
            db.beginTransaction();
            db.searchFlickrDao().deleteAll();
            db.searchFlickrDao().insert(new SearchFlickr(query, total));
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
