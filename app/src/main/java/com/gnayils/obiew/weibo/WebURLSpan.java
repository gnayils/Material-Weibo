package com.gnayils.obiew.weibo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.text.Spanned;
import android.view.View;

import com.gnayils.obiew.App;
import com.gnayils.obiew.R;

/**
 * Created by Gnayils on 07/05/2017.
 */
public class WebURLSpan extends TouchableSpan {

    public String url;
    public int start;
    public int end;
    public int flag = Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

    public WebURLSpan(String url, int start, int end) {
        super(App.resources().getColor(R.color.colorLink), App.resources().getColor(R.color.colorLink), App.resources().getColor(R.color.colorLinkBackground));
        this.url = url;
        this.start = start;
        this.end = end;
    }

    @Override
    public void onClick(View widget) {
        Uri uri = Uri.parse(url);
        Context context = widget.getContext();
        if (uri.getScheme().startsWith("http")) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        }
    }
}
