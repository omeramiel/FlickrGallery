package omeram.com.flickrgallery;

import android.app.Application;

import omeram.com.flickrgallery.di.AppInjector;
import timber.log.Timber;

public class FlickrApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        AppInjector.init(this);
    }

}
