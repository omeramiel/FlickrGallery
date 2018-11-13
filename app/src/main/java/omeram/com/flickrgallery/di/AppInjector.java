package omeram.com.flickrgallery.di;

import omeram.com.flickrgallery.FlickrApp;

public class AppInjector {

    private static AppComponent applicationComponent;

    private AppInjector() {}

    public static void init(FlickrApp flickrApp) {
        applicationComponent = DaggerAppComponent.builder()
                .application(flickrApp)
                .build();

        applicationComponent.inject(flickrApp);
    }

    public static AppComponent getApplicationComponent() {
        return applicationComponent;
    }

}
