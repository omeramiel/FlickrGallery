package omeram.com.flickrgallery.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotosInfo {

    @SerializedName("photo")
    private List<Photo> photos;
    @SerializedName("page")
    private int page;
    @SerializedName("pages")
    private int totalPages;
    @SerializedName("perpage")
    private int perPage;
    @SerializedName("total")
    private int total;

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPerPage() {
        return perPage;
    }

    public void setPerPage(int perPage) {
        this.perPage = perPage;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "PhotosInfo{" +
                "mPhotosInfo=" + photos +
                ", page=" + page +
                ", totalPages=" + totalPages +
                ", perPage=" + perPage +
                ", total=" + total +
                '}';
    }
}