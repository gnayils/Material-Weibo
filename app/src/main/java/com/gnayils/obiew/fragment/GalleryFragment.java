package com.gnayils.obiew.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.gnayils.obiew.R;
import com.gnayils.obiew.view.GiFHintImageView;
import com.gnayils.obiew.view.PictureCardView;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.gnayils.obiew.util.ViewUtils.dp2px;

public class GalleryFragment extends Fragment  {

    @Bind(R.id.recycler_view)
    protected RecyclerView recyclerView;

    private RecyclerViewAdapter recyclerViewAdapter;

    private Cursor photoCursor;

    private OnPhotoClickListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoClickListener) {
            listener = (OnPhotoClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnPhotoClickListener");
        }
        photoCursor = getActivity().managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                null, null, MediaStore.Images.Media._ID +" DESC");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerViewAdapter = new RecyclerViewAdapter();
        recyclerView.setAdapter(recyclerViewAdapter);
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(photoCursor != null) {
            photoCursor.close();
        }
        listener = null;
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<ImageCardViewHolder> {

        @Override
        public ImageCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PictureCardView pictureCardView = new PictureCardView(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(dp2px(pictureCardView.getContext(), 2), dp2px(pictureCardView.getContext(), 4), dp2px(pictureCardView.getContext(), 2), dp2px(pictureCardView.getContext(), 0));
            pictureCardView.setLayoutParams(layoutParams);
            return new ImageCardViewHolder(pictureCardView);
        }

        @Override
        public void onBindViewHolder(ImageCardViewHolder holder, int position) {
            photoCursor.moveToPosition(position);
            final int id = photoCursor.getInt(photoCursor.getColumnIndex(MediaStore.Images.Media._ID));
            final String data = photoCursor.getString(photoCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            Glide.with(GalleryFragment.this).load(data).asBitmap().into(holder.imageView);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPhotoClick(id, data);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return photoCursor.getCount();
        }

    }

    static class ImageCardViewHolder extends RecyclerView.ViewHolder {

        GiFHintImageView imageView;

        ImageCardViewHolder(PictureCardView pictureCardView) {
            super(pictureCardView);
            imageView = pictureCardView.imageView;
        }
    }

    public interface OnPhotoClickListener {

        void onPhotoClick(int id, String data);

    }

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        return fragment;
    }
}
