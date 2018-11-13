package omeram.com.flickrgallery.ui;

import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import omeram.com.flickrgallery.R;
import omeram.com.flickrgallery.model.Photo;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class GalleryFragment extends Fragment {

    private FlickrViewModel flickrViewModel;
    private OnListFragmentInteractionListener mListener;
    private GalleryRecyclerViewAdapter mGalleryAdapter;
    private SearchView searchView;
    private boolean mBackground = false;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public GalleryFragment() {
    }

    public static GalleryFragment newInstance() {
        return new GalleryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            mGalleryAdapter = new GalleryRecyclerViewAdapter(getContext(), mListener);
            flickrViewModel.getPhotosLiveData().observe(this, photos -> mGalleryAdapter.submitList(photos));
            recyclerView.setAdapter(mGalleryAdapter);
            recyclerView.setHasFixedSize(true);
            GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options_menu, menu);

        SearchManager searchManager =
                (SearchManager) ((MainActivity) mListener).getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchManager != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(((MainActivity) mListener).getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                flickrViewModel.setSearchQuery(query, mBackground);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return true;
            }
        });

        flickrViewModel.getSearchQueryLiveData().observe(this, query -> searchView.setQuery(query, false));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.toggle_bg:
                mBackground = !mBackground;
                flickrViewModel.setSearchQuery(searchView.getQuery().toString(), mBackground);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
            flickrViewModel = ViewModelProviders.of((MainActivity) mListener).get(FlickrViewModel.class);
            flickrViewModel.setSearchQuery(null, true);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Photo photo);
    }
}
