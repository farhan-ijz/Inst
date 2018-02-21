package com.wurfel.yaqeen.inst.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.wurfel.yaqeen.inst.R;
import com.wurfel.yaqeen.inst.ResponseModels.Posts.Post;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Farhan Ijaz on 3/7/2017.
 */
public class HomePostsListViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<Post> list = new ArrayList<>();

    public HomePostsListViewAdapter(Context context, int layoutResourceId, List<Post> list) {
        super(context, layoutResourceId, list);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imgViewPost = (ImageView) row.findViewById(R.id.imageView_post);
            holder.imgViewVideoIcon = (ImageView) row.findViewById(R.id.imageView_videoIcon);
            holder.txtViewTitle = (TextView) row.findViewById(R.id.textView_title);
            holder.txtViewAuthor = (TextView) row.findViewById(R.id.textView_autherName);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        if( !(position >= list.size())) {
            final ViewHolder finalHolder = holder;
            Glide.with(context).load(list.get(position).getJkPostImageUrl()).asBitmap()
                    .placeholder(R.drawable.placeholder_resized)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .fitCenter()
                    .into(new BitmapImageViewTarget(finalHolder.imgViewPost) {
                @Override
                protected void setResource(Bitmap resource) {
                    finalHolder.imgViewPost.setImageBitmap(resource);
                }
            });

//            String title = list.get(position).getTitle().getRendered();
//            String titleSpecialCharRemoved = title;
//            try {
//                titleSpecialCharRemoved = URLDecoder.decode(title, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                holder.txtViewAuthor.setText(Html.fromHtml(list.get(position).getJkAuthorName(),Html.FROM_HTML_MODE_LEGACY));
                holder.txtViewTitle.setText(Html.fromHtml(list.get(position).getTitle().getRendered(),Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.txtViewAuthor.setText(Html.fromHtml(list.get(position).getJkAuthorName()));
                holder.txtViewTitle.setText(Html.fromHtml(list.get(position).getTitle().getRendered()));
            }

            if(list.get(position).getJkPostVideoUrl().size()>0){
                holder.imgViewVideoIcon.setVisibility(View.VISIBLE);
            } else {
                holder.imgViewVideoIcon.setVisibility(View.GONE);
            }
        }

        return row;
    }

    static class ViewHolder {
        ImageView imgViewPost;
        ImageView imgViewVideoIcon;
        TextView txtViewTitle;
        TextView txtViewAuthor;
    }
}

