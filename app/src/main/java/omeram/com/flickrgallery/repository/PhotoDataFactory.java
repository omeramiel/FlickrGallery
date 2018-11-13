package omeram.com.flickrgallery.repository;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import omeram.com.flickrgallery.api.FlickrApi;
import omeram.com.flickrgallery.model.Photo;

public class PhotoDataFactory extends DataSource.Factory<Integer, Photo> {

    private final FlickrApi flickrApi;
    public MutableLiveData<PhotoDataSource> dataSourceLiveData = new MutableLiveData<>();

    public PhotoDataFactory(FlickrApi flickrApi) {
        this.flickrApi = flickrApi;
    }

    @Override
    public DataSource<Integer, Photo> create() {
        PhotoDataSource dataSource = new PhotoDataSource(flickrApi);
        dataSourceLiveData.postValue(dataSource);
        return dataSource;
    }
}