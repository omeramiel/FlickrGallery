package omeram.com.flickrgallery.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.arch.paging.PagedList.Config.Builder;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.work.Data;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import omeram.com.flickrgallery.AppExecutors;
import omeram.com.flickrgallery.api.FlickrApi;
import omeram.com.flickrgallery.db.FlickrDb;
import omeram.com.flickrgallery.db.SearchFlickrDao;
import omeram.com.flickrgallery.model.DataLoadState;
import omeram.com.flickrgallery.model.Photo;
import omeram.com.flickrgallery.util.Constants;
import omeram.com.flickrgallery.workers.SearchQueryWorker;
import omeram.com.flickrgallery.vo.SearchFlickr;
import timber.log.Timber;

import static android.arch.lifecycle.Transformations.switchMap;
import static omeram.com.flickrgallery.util.Constants.KEY_PHOTO_QUERY;
import static omeram.com.flickrgallery.util.Constants.KEY_PREVIOUS_TOTAL;

/**
 * Repository that handles Flickr photos.
 */
@Singleton
public class FlickrRepository {

    private final FlickrDb db;
    private final SearchFlickrDao searchFlickrDao;
    private final AppExecutors appExecutors;
    private final WorkManager workManager;

    private LiveData<PagedList<Photo>> photos;
    private PhotoDataFactory photoDataFactory;
    private SearchDataFactory searchDataFactory;

    private final MediatorLiveData<String> result = new MediatorLiveData<>();


    @Inject
    FlickrRepository(FlickrDb db, AppExecutors appExecutors, SearchFlickrDao searchFlickrDao, FlickrApi api, WorkManager workManager) {
        this.db = db;
        this.appExecutors = appExecutors;
        this.searchFlickrDao = searchFlickrDao;
        this.photoDataFactory = new PhotoDataFactory(api);
        this.searchDataFactory = new SearchDataFactory(api);
        this.workManager = workManager;

        LiveData<List<SearchFlickr>> dbSource = searchFlickrDao.loadSearch();
        result.addSource(dbSource, data -> {
            if (data != null && !data.isEmpty()) {
                setValue(data.get(0).getQuery());
            } else {
                setValue(null);
            }
        });
    }

    @MainThread
    private void setValue(String newValue) {
        if (result.getValue() != null && !result.getValue().equals(newValue)) {
            result.setValue(newValue);
        } else {
            result.setValue(null);
        }
    }

    @MainThread
    public LiveData<DataLoadState> getDataLoadStatus() {
        return switchMap(photoDataFactory.dataSourceLiveData,
                dataSource -> dataSource.loadState);
    }

    @MainThread
    public LiveData<PagedList<Photo>> getRecentPhotosLiveData() {
        Timber.d("getRecentPhotosLiveData ");
        PagedList.Config config = new Builder()
                .setInitialLoadSizeHint(50)
                .setPageSize(50)
                .build();

        photos = new LivePagedListBuilder(photoDataFactory, config)
                .setInitialLoadKey(1)
                .setFetchExecutor(appExecutors.networkIO())
                .build();
        return photos;
    }

    @MainThread
    public LiveData<PagedList<Photo>> searchPhotosLiveData(@NonNull String query, boolean background) {
        Timber.d("searchPhotosLiveData " + query + "background " + background);
        PagedList.Config config = new Builder()
                .setInitialLoadSizeHint(50)
                .setPageSize(50)
                .build();
        searchDataFactory.setInputText(query);
        photos = new LivePagedListBuilder(searchDataFactory, config)
                .setInitialLoadKey(1)
                .setFetchExecutor(appExecutors.networkIO())
                .build();
        searchPhoto(query, background);
        return photos;
    }

    public LiveData<String> loadLastSearch() {
        return result;
    }

    private void searchPhoto(String query, boolean background) {
        Timber.d("searchPhoto query " + query);
        LiveData<List<SearchFlickr>> dbSource = searchFlickrDao.loadSearch();
        result.addSource(dbSource, search -> {
            result.removeSource(dbSource);
            int total = replaceSearchQuery(search, query);
            if (background) {
                PeriodicWorkRequest.Builder searchQueryWorker = new PeriodicWorkRequest.Builder(SearchQueryWorker.class, 15, TimeUnit.MINUTES);
                Data data = createInputDataForQuery(query, total);
                PeriodicWorkRequest workRequest = searchQueryWorker
                        .addTag(Constants.TAG_SEARCH_WORK)
                        .setInputData(data)
                        .build();
                workManager.enqueue(workRequest);
            } else {
                workManager.cancelAllWorkByTag(Constants.TAG_SEARCH_WORK);
            }
        });
    }

    @WorkerThread
    private int replaceSearchQuery(List<SearchFlickr> searchFlickrs, String query) {
        int total = 0;
        if (searchFlickrs != null && !searchFlickrs.isEmpty() && searchFlickrs.get(0).getQuery().equals(query)) {
            Timber.d("updateSearchQuery " + searchFlickrs.toString());
            total = searchFlickrs.get(0).getTotalFound();
        } else {
            Timber.d("replaceSearchQuery " + query);
            int finalTotal = total;
            appExecutors.diskIO().execute(() -> {
                db.searchFlickrDao().deleteAll();
                db.searchFlickrDao().insert(new SearchFlickr(query, finalTotal));
            });
        }
        return total;
    }

    private Data createInputDataForQuery(String query, int total) {
        Data.Builder builder = new Data.Builder();
        if (query != null) {
            builder.putString(KEY_PHOTO_QUERY, query);
            builder.putInt(KEY_PREVIOUS_TOTAL, total);
        }
        return builder.build();
    }

}
