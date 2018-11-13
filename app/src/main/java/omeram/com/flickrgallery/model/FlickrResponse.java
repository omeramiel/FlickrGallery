package omeram.com.flickrgallery.model;

import com.google.gson.annotations.SerializedName;

public class FlickrResponse {

    @SerializedName("photos")
    private PhotosInfo photosInfo;

    @SerializedName("stat")
    private String status;

    public PhotosInfo getPhotosInfo() {
        return photosInfo;
    }
}
