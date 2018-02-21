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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wurfel.yaqeen.inst.Activity.MainActivity;
import com.wurfel.yaqeen.inst.Activity.ResearchPostsActivity;
import com.wurfel.yaqeen.inst.Adapter.SectionsGridViewAdapter;
import com.wurfel.yaqeen.inst.Models.Section;
import com.wurfel.yaqeen.inst.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aliapple on 7/4/17.
 */

public class SectionsFragment extends Fragment {

    private boolean fragmentResume=false;
    private boolean fragmentVisible=false;
    private boolean fragmentOnCreated=false;
    RelativeLayout fragmentContainer;
    GridView gridViewSections;
    List<Section> listSection = new ArrayList<>();
    SectionsGridViewAdapter adapter;

    public SectionsFragment(){
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file

        return inflater.inflate(R.layout.layout_sections_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)   {
        super.onViewCreated(view, savedInstanceState);

        Bundle extras = getArguments();

        //Initialize variables
        if (!fragmentResume && fragmentVisible){   //only when first time fragment is created
            changeToolbarTitle();
        }

        initializeControls(view);
        setDataInGridViews();
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
        // Do what you want here, for example animate the content
        if (fragmentContainer != null) {
            Animation fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            fragmentContainer.startAnimation(fadeIn);
        }
    }

    /**
     * Called when a fragment will be hidden
     */
    public void willBeHidden() {
        if (fragmentContainer != null) {
            Animation fadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
            fragmentContainer.startAnimation(fadeOut);
        }
    }

    void changeToolbarTitle() {
        Toolbar toolbar = (Toolbar) ((MainActivity)getActivity()).findViewById(R.id.toolbar);
        TextView TextViewTitle = (TextView) toolbar.findViewById(R.id.textView_toolbarTilte);
        TextViewTitle.setText("Research");
    }

    void initializeControls(View view){
        fragmentContainer = (RelativeLayout) view.findViewById(R.id.fragment_layout);
        gridViewSections = (GridView) view.findViewById(R.id.gridView_sections);
    }

    void setListeners() {
        gridViewSections.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!listSection.get(position).getCommingSoon()) {
                    Intent intent = new Intent(getActivity(), ResearchPostsActivity.class);
                    intent.putExtra("toolbarTitle", listSection.get(position).getName());
                    intent.putExtra("tagId", listSection.get(position).getTagId());
                    startActivity(intent);
                }
            }
        });
    }

    List<Section> getSectionsDataList(){
        listSection = new ArrayList<>();
        listSection.add(new Section("Publications",R.drawable.ic_sections_publication,"83",false));
        listSection.add(new Section("Articles",R.drawable.ic_sections_article, "84",false));
        listSection.add(new Section("Lectures",R.drawable.ic_sections_lecture, "85",false));
        listSection.add(new Section("Q/A",R.drawable.ic_sections_q_a, "96",false));
        listSection.add(new Section("Infographics",R.drawable.ic_sections_infographics, "153",true));
        listSection.add(new Section("Audio Books",R.drawable.ic_sections_audio, "1",true));
        listSection.add(new Section("Yaqeen Series",R.drawable.ic_sections_yakeen_series, "1",true));
        return listSection;
    }

    void setDataInGridViews(){
        if(adapter==null) {
            adapter = new SectionsGridViewAdapter(getActivity(), R.layout.item_sections_gridview, getSectionsDataList());
            gridViewSections.setAdapter(adapter);
        }
    }

}
