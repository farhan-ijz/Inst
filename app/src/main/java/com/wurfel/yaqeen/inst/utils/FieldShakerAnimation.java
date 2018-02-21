package com.wurfel.yaqeen.inst.utils;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.wurfel.yaqeen.inst.R;


/**
 * Created by Farhan Ijaz on 4/5/2017.
 */
public class FieldShakerAnimation {
    Context context;
    static Animation shake;


    public static void startShaking(Context c, EditText field){
        shake = AnimationUtils.loadAnimation(c, R.anim.shake);
//        field.setHintTextColor(c.getResources().getColor(R.color.colorRed));
        field.startAnimation(shake);

    }
}
