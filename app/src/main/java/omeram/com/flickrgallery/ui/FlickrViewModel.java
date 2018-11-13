package omeram.com.flickrgallery.ui;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import omeram.com.flickrgallery.di.AppInjector;
import omeram.com.flickrgallery.model.DataLoadState;
import omeram.com.flickrgallery.model.Photo;
import omeram.com.flickrgallery.repository.FlickrRepository;
import timber.log.Timber;

public class FlickrViewModel extends AndroidViewModel {

    @Inject
    FlickrRepository mRepository;

    private MediatorLiveData<PagedList<Photo>> mPhotosLiveData = new MediatorLiveData<>();
    private MutableLiveData<String> mSearchQueryLiveData = new MutableLiveData<>();

    @Inject
    public FlickrViewModel(@NonNull Application application) {
        super(application);
        AppInjector.getApplicationComponent().inject(this);
        mRepository.loadLastSearch().observeForever(query -> mSearchQueryLiveData.setValue(query));
    }

    public void setSearchQuery(String query, boolean background) {
        Timber.d("setSearchQuery " + query + "background " + background);
        LiveData<PagedList<Photo>> photoSource;
        if (query == null || query.isEmpty()) {
            photoSource = mRepository.getRecentPhotosLiveData();
        } else {
            photoSource = mRepository.searchPhotosLiveData(query, background);
        }
        mPhotosLiveData.addSource(photoSource, photos -> {
            mPhotosLiveData.removeSource(photoSource);
            mPhotosLiveData.setValue(photos);
        });
    }

    public LiveData<String> getSearchQueryLiveData() {
        return mSearchQueryLiveData;
    }

    public LiveData<PagedList<Photo>> getPhotosLiveData() {
        return mPhotosLiveData;
    }

    public LiveData<DataLoadState> dataLoadStatus() {
        return mRepository.getDataLoadStatus();
    }

}
