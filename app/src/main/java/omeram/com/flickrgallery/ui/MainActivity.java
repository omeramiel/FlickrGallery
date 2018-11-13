package omeram.com.flickrgallery.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import omeram.com.flickrgallery.R;
import omeram.com.flickrgallery.model.Photo;

public class MainActivity extends AppCompatActivity implements GalleryFragment.OnListFragmentInteractionListener {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            navigateToGallery();
        }
    }

    public void navigateToGallery() {
        GalleryFragment searchFragment = GalleryFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.container, searchFragment)
                .commitAllowingStateLoss();
    }

//    public void navigateToPhoto() {
//        GalleryFragment searchFragment = GalleryFragment.newInstance(3);
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, searchFragment)
//                .commitAllowingStateLoss();
//    }

    @Override
    public void onListFragmentInteraction(Photo photo) {
    }

}
