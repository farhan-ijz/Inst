package com.wurfel.yaqeen.inst.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.wurfel.yaqeen.inst.Activity.MainActivity;
import com.wurfel.yaqeen.inst.Activity.SigninActivity;
import com.wurfel.yaqeen.inst.R;

/**
 * Created by FarhanIjaz on 7/4/17.
 */

public class ProfileFragment extends Fragment {

    private boolean fragmentResume=false;
    private boolean fragmentVisible=false;
    private boolean fragmentOnCreated=false;
    RelativeLayout fragmentContainer;
    TextView textViewUserName;
    Button btnSignout;

    private FirebaseAuth mAuth;

    public ProfileFragment(){
        // Empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file

        return inflater.inflate(R.layout.layout_profile_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)   {
        super.onViewCreated(view, savedInstanceState);

        Bundle extras = getArguments();

        mAuth = FirebaseAuth.getInstance();

        //Initialize variables
        if (!fragmentResume && fragmentVisible){   //only when first time fragment is created
            changeToolbarTitle();
        }

        initializeControls(view);
        setUserName();
        setListeners();
    }

    @Override
    public void setUserVisibleHint(boolean visible){
        super.setUserVisibleHint(visible);
        if (visible && isResumed()){   // only at fragment screen is resumed
            fragmentResume=true;
            fragmentVisible=false;
            fragmentOnCreated=true;
            changeToolbarTitle();
        }else  if (visible){        // only at fragment onCreated
            fragmentResume=false;
            fragmentVisible=true;
            fragmentOnCreated=true;
        }
        else if(!visible && fragmentOnCreated){// only when you go out of fragment screen
            fragmentVisible=false;
            fragmentResume=false;
        }
    }

    /**
     * Called when a fragment will be displayed
     */
    public void willBeDisplayed() {
        // Animation
        if (fragmentContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            fragmentContainer.startAnimation(fadeIn);
        }
    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        // Animation
        if (fragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            fragmentContainer.startAnimation(fadeOut);
        }
    }

    void changeToolbarTitle() {
        Toolbar toolbar = (Toolbar) ((MainActivity)getActivity()).findViewById(R.id.toolbar);
        TextView TextViewTitle = (TextView) toolbar.findViewById(R.id.textView_toolbarTilte);
        TextViewTitle.setText("Profile");
    }

    void initializeControls(View view){
        fragmentContainer = (RelativeLayout) view.findViewById(R.id.fragment_layout);
        textViewUserName = (TextView) view.findViewById(R.id.textView_userName);
        btnSignout = (Button) view.findViewById(R.id.btn_signout);
    }

    void setUserName(){
        if (mAuth.getCurrentUser() != null) {
            FirebaseUser user = mAuth.getCurrentUser();

            String name = user.getDisplayName();

            textViewUserName.setText(name);
        }
    }

    void setListeners(){
        // SignOut Medthod
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(),SigninActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

}
