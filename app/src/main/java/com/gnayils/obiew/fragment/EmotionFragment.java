package com.gnayils.obiew.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.gnayils.obiew.R;
import com.gnayils.obiew.weibo.EmotionDB;

import java.util.List;

public class EmotionFragment extends Fragment {

    private static final int GRID_COLUMNS = 6;
    private static final int GRID_ITEM_SPACING = 4;

    private OnEmotionClickListener listener;

    public static EmotionFragment newInstance() {
        EmotionFragment fragment = new EmotionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_emotion, container, false);
        final GridView gridView = (GridView) rootView.findViewById(R.id.grid_view);
        final EmotionAdapter emotionAdapter = new EmotionAdapter();
        gridView.setAdapter(emotionAdapter);
        gridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int columnWidth = (gridView.getWidth() - GRID_ITEM_SPACING * (2 + GRID_COLUMNS - 1)) / GRID_COLUMNS;
                        if(columnWidth > 0) {
                            gridView.setColumnWidth(columnWidth);
                            gridView.setHorizontalSpacing(GRID_ITEM_SPACING);
                            gridView.setVerticalSpacing(GRID_ITEM_SPACING);
                            gridView.setPadding(GRID_ITEM_SPACING, GRID_ITEM_SPACING, GRID_ITEM_SPACING, GRID_ITEM_SPACING);
                            emotionAdapter.setItemHeight(columnWidth);
                            gridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }

                    }
                });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listener != null) {
                    String phrase = (String) emotionAdapter.getItem(position);
                    listener.onEmotionClick(phrase);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnEmotionClickListener) {
            listener = (OnEmotionClickListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnEmotionClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface OnEmotionClickListener {

        void onEmotionClick(String phrase);

    }


    private class EmotionAdapter extends BaseAdapter {

        private int mItemHeight = 0;
        private GridView.LayoutParams itemLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        private List<String> phraseList;

        private float emotionOriginalSize;

        public EmotionAdapter() {
            super();
            phraseList = EmotionDB.getAllPhrase();
            emotionOriginalSize = getContext().getResources().getDimension(R.dimen.emotion_original_size);
        }

        @Override
        public int getCount() {
            return phraseList.size();
        }

        @Override
        public Object getItem(int position) {
            return phraseList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(container.getContext());
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                imageView.setLayoutParams(itemLayoutParams);
            } else {
                imageView = (ImageView) convertView;
            }
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(itemLayoutParams);
            }
            Bitmap bitmap = EmotionDB.get(phraseList.get(position), emotionOriginalSize);
            if(bitmap == null) {
                imageView.setImageDrawable(new ColorDrawable(Color.RED));
            } else {
                imageView.setImageBitmap(bitmap);
            }
            return imageView;
        }

        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            itemLayoutParams = new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }
    }
}
