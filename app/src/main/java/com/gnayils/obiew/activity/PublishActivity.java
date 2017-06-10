package com.gnayils.obiew.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gnayils.obiew.R;
import com.gnayils.obiew.fragment.EmotionFragment;
import com.gnayils.obiew.fragment.FriendshipFragment;
import com.gnayils.obiew.fragment.GalleryFragment;
import com.gnayils.obiew.util.Popup;
import com.gnayils.obiew.util.ViewUtils;
import com.gnayils.obiew.weibo.TextDecorator;
import com.gnayils.obiew.weibo.Weibo;
import com.gnayils.obiew.weibo.bean.Status;
import com.gnayils.obiew.weibo.bean.User;
import com.gnayils.obiew.weibo.service.StatusService;
import com.gnayils.obiew.weibo.service.SubscriberAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PublishActivity extends AppCompatActivity implements EmotionFragment.OnEmotionClickListener, GalleryFragment.OnPhotoClickListener, FriendshipFragment.OnFriendClickListener {

    private InputMethodManager inputMethodManager;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.edit_text_content)
    EditText contentEditText;
    @Bind(R.id.layout_selected_image)
    LinearLayout selectedImageLayout;
    @Bind(R.id.image_button_emotion)
    ImageButton emotionImageButton;
    @Bind(R.id.image_button_gallery)
    ImageButton galleryImageButton;
    @Bind(R.id.image_button_mention)
    ImageButton mentionImageButton;
    @Bind(R.id.image_button_topic)
    ImageButton topicImageButton;
    @Bind(R.id.image_button_backspace)
    ImageButton backspaceImageButton;
    @Bind(R.id.linear_layout_function_bar)
    LinearLayout functionBarLayout;
    @Bind(R.id.frame_layout_function_content)
    FrameLayout functionContentLayout;

    private EmotionFragment emotionFragment = EmotionFragment.newInstance();
    private GalleryFragment galleryFragment = GalleryFragment.newInstance();
    private FriendshipFragment friendshipFragment = FriendshipFragment.newInstance();

    private List<String> selectedPhotoList = new ArrayList<>();

    private StatusService statusService = new StatusService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("发微博");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final View contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect windowVisibleDisplayRect = new Rect();
                contentView.getWindowVisibleDisplayFrame(windowVisibleDisplayRect);
                int heightDiff = contentView.getRootView().getHeight() - (windowVisibleDisplayRect.bottom - windowVisibleDisplayRect.top);
                if (heightDiff > ViewUtils.getScreenSize(PublishActivity.this).getHeight() / 4) {
                    hideFunctionContent();
                }
            }
        });

        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        contentEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emotionImageButton.setSelected(false);
                galleryImageButton.setSelected(false);
                hideFunctionContent();
            }
        });

        contentEditText.addTextChangedListener(new TextWatcher() {

            boolean changedByUser = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (changedByUser) {
                    changedByUser = false;
                    SpannableString spannableString = null;
                    if (s instanceof SpannableString) {
                        spannableString = (SpannableString) s;
                    } else {
                        spannableString = new SpannableString(s.toString());
                    }
                    TextDecorator.decorateTopics(spannableString);
                    TextDecorator.decorateMentions(spannableString);
                    TextDecorator.decorateEmotions(spannableString);
                    contentEditText.setText(spannableString);
                    contentEditText.setSelection(start + count);
                } else {
                    changedByUser = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        emotionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                galleryImageButton.setSelected(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout_function_content, emotionFragment)
                        .commit();
                hideSoftKeyBoard();
            }
        });

        galleryImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(true);
                emotionImageButton.setSelected(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout_function_content, galleryFragment)
                        .commit();
                hideSoftKeyBoard();
            }
        });

        mentionImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryImageButton.setSelected(false);
                emotionImageButton.setSelected(false);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout_function_content, friendshipFragment)
                        .commit();
                hideSoftKeyBoard();
            }
        });

        topicImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = contentEditText.getEditableText();
                editable.insert(contentEditText.getSelectionStart(), "##");
                contentEditText.setSelection(contentEditText.getSelectionStart() - 1);
                hideFunctionContent();
            }
        });

        backspaceImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emotionImageButton.isSelected()) {
                    contentEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                } else if (galleryImageButton.isSelected()) {
                    if (selectedPhotoList.size() > 0) {
                        selectedPhotoList.remove(selectedPhotoList.size() - 1);
                        selectedImageLayout.removeViewAt(selectedImageLayout.getChildCount() - 1);
                    }
                } else {
                    contentEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }
            }
        });
    }

    private void hideSoftKeyBoard() {
        if (functionContentLayout.getVisibility() == View.GONE) {
            inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    functionContentLayout.setVisibility(View.VISIBLE);
                }
            }, 100);
        }
    }

    private void hideFunctionContent() {
        if (functionContentLayout.getVisibility() == View.VISIBLE) {
            functionBarLayout.setVisibility(View.INVISIBLE);
            functionContentLayout.setVisibility(View.GONE);
            inputMethodManager.showSoftInput(contentEditText, 0);
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    functionBarLayout.setVisibility(View.VISIBLE);
                }
            }, 100);
        }
    }

    @Override
    public void onBackPressed() {
        if (functionContentLayout.getVisibility() == View.VISIBLE) {
            functionContentLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onEmotionClick(String phrase) {
        Editable editable = contentEditText.getEditableText();
        int start = contentEditText.getSelectionStart();
        editable.insert(start, phrase);
    }


    @Override
    public void onPhotoClick(int id, String data) {
        if (selectedPhotoList.size() < 9) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(getApplicationContext().getContentResolver(),
                    id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            imageView.setImageBitmap(bitmap);
            LinearLayout.LayoutParams imageViewLayoutParam = new LinearLayout.LayoutParams(ViewUtils.dp2px(this, 96), ViewUtils.dp2px(this, 96));
            imageViewLayoutParam.setMargins(ViewUtils.dp2px(this, 2), 0, ViewUtils.dp2px(this, 2), 0);
            imageView.setLayoutParams(imageViewLayoutParam);
            selectedImageLayout.addView(imageView);
            selectedPhotoList.add(data);
        }
    }

    @Override
    public void onFriendClick(User user) {
        Editable editable = contentEditText.getEditableText();
        int start = contentEditText.getSelectionStart();
        editable.insert(start, "@" + user.screen_name + " ");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_publish_option_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_publish);
        menuItem.setIcon(ViewUtils.tintDrawable(menuItem.getIcon(), Color.WHITE));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_publish:
                publishStatus();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishStatus() {
        if (contentEditText.getText().toString().isEmpty()) {
            return;
        }
        if (contentEditText.getText().toString().length() > Weibo.consts.STATUS_TEXT_MAX_LENGTH) {
            return;
        }
        if (selectedPhotoList != null && selectedPhotoList.size() > 9) {
            return;
        }
        statusService.publishStatus(contentEditText.getText().toString(), selectedPhotoList, new SubscriberAdapter<Status>() {

            MaterialDialog progressDialog = null;

            @Override
            public void onSubscribe() {
                progressDialog = Popup.indeterminateProgressDialog("发布微博", "正在发布微博...");
            }

            @Override
            public void onError(Throwable e) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Popup.errorDialog("错误", "微博发布失败: " + e.getMessage());
            }

            @Override
            public void onCompleted() {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                Popup.infoDialog("信息", "微博发布成功")
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                PublishActivity.this.finish();
                            }
                        });

            }
        });
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PublishActivity.class);
        context.startActivity(intent);
    }
}
