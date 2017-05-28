package com.gnayils.obiew.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.util.ViewUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GalleryFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int GRID_COLUMNS = 4;
    private static final int GRID_ITEM_SPACING = 4;

    @Bind(R.id.grid_view)
    protected GridView gridView;

    private GalleryAdapter galleryAdapter;

    private OnPhotoClickListener listener;

    public GalleryFragment() {
    }

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        ButterKnife.bind(this, rootView);
        final int gridItemSpacing = ViewUtils.dp2px(container.getContext(), GRID_ITEM_SPACING);
        galleryAdapter = new GalleryAdapter(container.getContext(), null, 0);
        gridView.setAdapter(galleryAdapter);
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int columnWidth = (gridView.getWidth() -  gridItemSpacing * (2 + GRID_COLUMNS - 1)) / GRID_COLUMNS;
                        if(columnWidth > 0) {
                            gridView.setColumnWidth(columnWidth);
                            gridView.setHorizontalSpacing(gridItemSpacing);
                            gridView.setVerticalSpacing(gridItemSpacing);
                            gridView.setPadding(gridItemSpacing, 0, gridItemSpacing, 0);
                            galleryAdapter.setItemHeight(columnWidth);
                            gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listener != null) {
                    Cursor cursor = galleryAdapter.getCursor();
                    if(cursor != null) {
                        cursor.moveToPosition(position);
                        int _id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        listener.onPhotoClick(_id, data);
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPhotoClickListener) {
            listener = (OnPhotoClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnPhotoClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnPhotoClickListener {

        void onPhotoClick(int id, String data);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                null, null, MediaStore.Images.Media._ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(galleryAdapter != null) {
            galleryAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        galleryAdapter.swapCursor(null);
    }

    private class GalleryAdapter extends CursorAdapter {

        private int itemHeight = 0;
        private GridView.LayoutParams itemLayoutParams;

        public GalleryAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            itemLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(itemLayoutParams);
            return imageView;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            if(view instanceof ImageView) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                loadBitmap((ImageView) view, id);
            }
        }

        public void setItemHeight(int height) {
            if (height == itemHeight) {
                return;
            }
            itemHeight = height;
            itemLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
            notifyDataSetChanged();
        }

        private void loadBitmap(final ImageView imageView, int id) {
            new AsyncTask<Integer, Void, Bitmap>(){

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    imageView.setImageBitmap(null);
                }

                @Override
                protected Bitmap doInBackground(Integer... params) {
                    int id = params[0];
                    return MediaStore.Images.Thumbnails.getThumbnail(getActivity().getApplicationContext().getContentResolver(),
                            id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    imageView.setImageBitmap(bitmap);
                }

            }.execute(id);
        }
    }
}
