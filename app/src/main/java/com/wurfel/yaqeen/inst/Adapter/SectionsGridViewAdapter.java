package com.wurfel.yaqeen.inst.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.wurfel.yaqeen.inst.Models.Section;
import com.wurfel.yaqeen.inst.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Farhan Ijaz on 3/7/2017.
 */
public class SectionsGridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private List<Section> list = new ArrayList<>();

    public SectionsGridViewAdapter(Context context, int layoutResourceId, List<Section> list) {
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
            holder.imgView = (ImageView) row.findViewById(R.id.itemImage);
            holder.txtView = (TextView) row.findViewById(R.id.itemTitle);
            holder.layoutComingSoon = (RelativeLayout) row.findViewById(R.id.layout_comingSoon);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.txtView.setText(list.get(position).getName());
        holder.imgView.setImageDrawable(context.getResources().getDrawable(list.get(position).getImageResourceId()));
        if(list.get(position).getCommingSoon()){
            holder.layoutComingSoon.setVisibility(View.VISIBLE);
        }
        else {
            holder.layoutComingSoon.setVisibility(View.GONE);
        }

        return row;
    }

    static class ViewHolder {
        ImageView imgView;
        TextView txtView;
        RelativeLayout layoutComingSoon;
    }
}

