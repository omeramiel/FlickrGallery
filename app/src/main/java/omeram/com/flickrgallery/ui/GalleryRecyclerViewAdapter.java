package omeram.com.flickrgallery.ui;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import omeram.com.flickrgallery.R;
import omeram.com.flickrgallery.model.Photo;
import omeram.com.flickrgallery.ui.GalleryFragment.OnListFragmentInteractionListener;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Photo} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class GalleryRecyclerViewAdapter extends PagedListAdapter<Photo, GalleryRecyclerViewAdapter.PhotoViewHolder> {

    private Context mContext;
    private final OnListFragmentInteractionListener mListener;

    GalleryRecyclerViewAdapter(Context context, OnListFragmentInteractionListener listener) {
        super(DIFF_CALLBACK);
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_photo, parent, false);
        return new PhotoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PhotoViewHolder holder, int position) {
        Photo photo = getItem(position);
        if (photo != null) {
            holder.bindTo(photo);
        } else {
            holder.clear();
        }
    }

    private static final DiffUtil.ItemCallback<Photo> DIFF_CALLBACK = new DiffUtil.ItemCallback<Photo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Photo oldPhoto, @NonNull Photo newPhoto) {
            return oldPhoto.getId().equals(newPhoto.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Photo oldPhoto, @NonNull Photo newPhoto) {
            return oldPhoto.equals(newPhoto);
        }
    };

    class PhotoViewHolder extends RecyclerView.ViewHolder {

        final ImageView mPhotoImage;

        PhotoViewHolder(View view) {
            super(view);
            mPhotoImage = view.findViewById(R.id.thumbnail);
        }

        void bindTo(Photo photo) {
            Glide.with(mContext)
                    .load(photo.getUrl())
                    .thumbnail(0.5f)
                    .into(mPhotoImage);

            itemView.setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(photo);
                }
            });
        }

        void clear() {
            mPhotoImage.setImageURI(null);
        }
    }
}
