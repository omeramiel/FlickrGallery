package omeram.com.flickrgallery.repository;

import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;

import omeram.com.flickrgallery.api.FlickrApi;
import omeram.com.flickrgallery.model.Photo;

public class SearchDataFactory extends DataSource.Factory<Integer, Photo> {

    private final FlickrApi flickrApi;
    private MutableLiveData<SearchDataSource> dataSourceLiveData = new MutableLiveData<>();
    private String inputText;

    public SearchDataFactory(FlickrApi flickrApi) {
        this.flickrApi = flickrApi;
    }

    @Override
    public DataSource<Integer, Photo> create() {
        SearchDataSource dataSource = new SearchDataSource(flickrApi, inputText);
        dataSourceLiveData.postValue(dataSource);
        return dataSource;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }
}