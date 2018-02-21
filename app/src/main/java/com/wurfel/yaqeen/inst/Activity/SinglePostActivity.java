package com.wurfel.yaqeen.inst.Activity;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.wurfel.yaqeen.inst.R;
import com.wurfel.yaqeen.inst.ResponseModels.Posts.Post;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SinglePostActivity extends AppCompatActivity {

    ImageView imgViewPost;
    ImageView imgViewVideoIcon;
    TextView textViewTitle;
    TextView textViewAuthor;
    TextView textViewDetail;

    Post postObj;
    String toolbarTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }
        setContentView(R.layout.activity_single_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initializeControls();
        getIntentData();

        if(postObj!=null) {
            setData();
        }
        changeToolbarTitle();
        setListeners();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    void getIntentData(){
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            postObj = (Post) extras.getSerializable("postData");
            toolbarTitle = extras.getString("toolbarTitle",getResources().getString(R.string.app_name));
        }
    }

    void changeToolbarTitle() {
        Toolbar toolbar = (Toolbar) ((SinglePostActivity.this).findViewById(R.id.toolbar));
        TextView TextViewTitle = (TextView) toolbar.findViewById(R.id.textView_toolbarTilte);
        TextViewTitle.setText(toolbarTitle);
    }

    void initializeControls(){
        textViewTitle = (TextView) findViewById(R.id.textView_title);
        textViewAuthor = (TextView) findViewById(R.id.textView_autherName);
        textViewDetail = (TextView) findViewById(R.id.textView_detail);
        imgViewPost = (ImageView) findViewById(R.id.imageView_post);
        imgViewVideoIcon = (ImageView) findViewById(R.id.imageView_videoIcon);
    }

    void setData(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            textViewAuthor.setText(Html.fromHtml(postObj.getJkAuthorName(),Html.FROM_HTML_MODE_LEGACY));
            textViewTitle.setText(Html.fromHtml(postObj.getTitle().getRendered(),Html.FROM_HTML_MODE_LEGACY));
            textViewDetail.setText(Html.fromHtml(postObj.getContent().getRendered(),Html.FROM_HTML_MODE_LEGACY));
        } else {
            textViewAuthor.setText(Html.fromHtml(postObj.getJkAuthorName()));
            textViewTitle.setText(Html.fromHtml(postObj.getTitle().getRendered()));
            textViewDetail.setText(Html.fromHtml(postObj.getContent().getRendered()));
        }

        textViewDetail. setMovementMethod(LinkMovementMethod.getInstance());

        if(postObj.getJkPostVideoUrl().size()>0){
            imgViewVideoIcon.setVisibility(View.VISIBLE);
        } else {
            imgViewVideoIcon.setVisibility(View.GONE);
        }

        Glide.with(this).load(postObj.getJkPostImageUrl()).asBitmap()
                .placeholder(R.drawable.placeholder_resized)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .fitCenter()
                .into(new BitmapImageViewTarget(imgViewPost) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        imgViewPost.setImageBitmap(resource);
                    }
                });
    }

    void setListeners(){
        imgViewVideoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postObj.getJkPostVideoUrl().size()>0) {
                    String videoId = getYoutubeVideoId(postObj.getJkPostVideoUrl().get(0).toString());
                    if(videoId!=null) {
                        watchYoutubeVideo(SinglePostActivity.this, videoId);
                    }
                }
            }
        });
    }

    void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            try {
                context.startActivity(webIntent);
            } catch (Exception e){
                Toast.makeText(context, "No app available to open Youtube video", Toast.LENGTH_SHORT).show();
            }

        }
    }

    String getYoutubeVideoId(String url){
        final String regex = "v=([^\\s&#]*)";
//        final String string = " https://m.youtube.com/watch?v=9tg3csrFVJw";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(url);

        if(matcher.find()) {
            return matcher.group(1);
        }
        else {
            return null;
        }
    }
}
