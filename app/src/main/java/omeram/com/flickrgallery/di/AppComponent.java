package omeram.com.flickrgallery.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import omeram.com.flickrgallery.FlickrApp;
import omeram.com.flickrgallery.workers.SearchQueryWorker;
import omeram.com.flickrgallery.ui.FlickrViewModel;

@Singleton
@Component(modules = {
        AppModule.class,
//        MainActivityModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
    }
    void inject(FlickrApp flickrApp);
    void inject(FlickrViewModel flickrViewModel);
    void inject(SearchQueryWorker searchQueryWorker);
}
