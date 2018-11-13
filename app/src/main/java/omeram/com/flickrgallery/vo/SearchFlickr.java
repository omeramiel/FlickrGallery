package omeram.com.flickrgallery.vo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class SearchFlickr {

    @NonNull
    @PrimaryKey
    private String query;
    private int totalFound;

    public SearchFlickr(@NonNull String query, int totalFound) {
        this.query = query;
        this.totalFound = totalFound;
    }

    @NonNull
    public String getQuery() {
        return query;
    }

    public int getTotalFound() {
        return totalFound;
    }

    public void setTotalFound(int totalFound) {
        this.totalFound = totalFound;
    }

    @Override
    public String toString() {
        return "SearchFlickr{" +
                "query='" + query + '\'' +
                ", totalFound=" + totalFound +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchFlickr that = (SearchFlickr) o;
        return query.equals(that.query);
    }

}
