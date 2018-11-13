package omeram.com.flickrgallery.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import androidx.work.WorkManager;
import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import omeram.com.flickrgallery.api.FlickrApi;
import omeram.com.flickrgallery.db.FlickrDb;
import omeram.com.flickrgallery.db.SearchFlickrDao;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
class AppModule {

    @Singleton
    @Provides
    FlickrApi provideFlickrApi(OkHttpClient okHttpClient) {
        return new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/services/rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(FlickrApi.class);
    }

    @Singleton
    @Provides
    public WorkManager workManager() {
        return WorkManager.getInstance();
    }

    @Singleton
    @Provides
    public OkHttpClient okHttpClient(HttpLoggingInterceptor loggingInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
    }

    @Singleton
    @Provides
    public HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Timber.d(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    @Singleton
    @Provides
    FlickrDb provideDb(Application app) {
        return Room.databaseBuilder(app, FlickrDb.class,"flickr.db").build();
    }

    @Singleton
    @Provides
    SearchFlickrDao providePhotoDao(FlickrDb db) {
        return db.searchFlickrDao();
    }

}
