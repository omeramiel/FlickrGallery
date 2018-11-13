package omeram.com.flickrgallery.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import omeram.com.flickrgallery.vo.SearchFlickr;

@Database(entities = {SearchFlickr.class}, version = 1)
public abstract class FlickrDb extends RoomDatabase {

    abstract public SearchFlickrDao searchFlickrDao();

}
