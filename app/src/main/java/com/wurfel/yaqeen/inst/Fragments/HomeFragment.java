package com.wurfel.yaqeen.inst.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wurfel.yaqeen.inst.Activity.MainActivity;
import com.wurfel.yaqeen.inst.Activity.SinglePostActivity;
import com.wurfel.yaqeen.inst.Adapter.HomePostsListViewAdapter;
import com.wurfel.yaqeen.inst.Helper.Constants;
import com.wurfel.yaqeen.inst.Interface.VolleyResponseListener;
import com.wurfel.yaqeen.inst.R;
import com.wurfel.yaqeen.inst.ResponseModels.Posts.Post;
import com.wurfel.yaqeen.inst.ResponseModels.Token.Token;
import com.wurfel.yaqeen.inst.utils.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aliapple on 7/4/17.
 */

public class HomeFragment extends Fragment {

    private boolean fragmentResume=false;
    private boolean fragmentVisible=false;
    private boolean fragmentOnCreated=false;
    RelativeLayout fragmentContainer;
    Gson gson;

    ListView listView;
    List<Post> postsDataList = new ArrayList<>();
    HomePostsListViewAdapter adapter;
    private int preLast=0;
    private Boolean moreScrollFlag=true;
    private int offset=0;
    int pageNum = 1;
    int perPageItems = 15;

    public HomeFragment(){
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file

        return inflater.inflate(R.layout.layout_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)   {
        super.onViewCreated(view, savedInstanceState);

        Bundle extras = getArguments();

        fragmentContainer = (RelativeLayout) view.findViewById(R.id.fragment_layout);

        //Initialize variables
        if (!fragmentResume && fragmentVisible){   //only when first time fragment is created
            changeToolbarTitle();
            initializeControls(view);
            setListeners();
            requestForToken();
        }

    }

    void initializeControls(View view){
        listView = (ListView) view.findViewById(R.id.listView_home);
    }

    void setListeners() {

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView lw, final int firstVisibleItem,
                                 final int visibleItemCount, final int totalItemCount) {
                switch (lw.getId()) {
                    case R.id.listView_home:

                        // Make your calculation stuff here. You have all your
                        // needed info from the parameters of this function.

                        // Sample calculation to determine if the last
                        // item is fully visible.
                        final int lastItem = firstVisibleItem + visibleItemCount;

                        if (lastItem == totalItemCount) {
                            if (preLast != lastItem) {
                                //to avoid multiple calls for last item
                                if (moreScrollFlag) {
                                    pageNum = pageNum + 1;
                                    requestForPosts();
                                    Log.d("Last", "Last");
                                    preLast = lastItem;
                                }
                            }
                        }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                Intent intent = new Intent(getActivity(),SinglePostActivity.class);
                intent.putExtra("toolbarTitle", getActivity().getResources().getString(R.string.app_name));
                Bundle bundle = new Bundle();
                bundle.putSerializable("postData", postsDataList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }});

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
        TextViewTitle.setText("Latest in Yaqeen");
    }

    public void requestForToken(){
        //Headers
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Basic "+encodeBase64(Constants.authorizationKey));
//        headers.put("Authorization", "Basic VFZiZGVzelpkWGN2OHBZT3VVTnE4eTdWSzAzakNDaXd4dEpNV2lMYTpnWEZvbkRkU3kwaUh6cUNrcUdEM2lXelFwaUNtQm54UmxjZ0pRdEJw");

        //Params
        HashMap<String, String> params = new HashMap<String, String>();

        //Request Body
        String bodyJson = getBodyJson();
//        String bodyJson = "";

        String URL_POST = this.getResources().getString(R.string.url_token);
        VolleyUtils.POST_METHOD(getContext(), URL_POST,params,headers,bodyJson, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                System.out.println("Error" + message);
                Toast.makeText(getActivity(), "Error: "+message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Object response) {
                extractTokenData(response.toString());
            }
        });
    }

    void extractTokenData(String response){
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            gson = gsonBuilder.create();
            Token tokenModelObj = gson.fromJson(response, Token.class);
            Constants.accessToken = tokenModelObj.getAccessToken();
            requestForPosts();
        } catch (Exception e) {
            Log.i("TagException", e.toString());
        }
    }

    public void requestForPosts(){
        //Headers
        HashMap<String, String> headers = new HashMap<String, String>();

        //Params
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", Constants.accessToken);
        params.put("page", pageNum+"");
        params.put("per_page", perPageItems+"");
//        params.put("Accept", "application/json");

        String URL_POST = this.getResources().getString(R.string.url_posts);
        VolleyUtils.GET_METHOD(getContext(), URL_POST,params,headers, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                System.out.println("Error" + message);
//                Toast.makeText(getActivity(), "Error: "+message, Toast.LENGTH_SHORT).show();
                moreScrollFlag = false;
            }

            @Override
            public void onResponse(Object response) {
                extractPostsData(response.toString());
            }
        });
    }

    void extractPostsData(String response){
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setDateFormat("M/d/yy hh:mm a");
            gson = gsonBuilder.create();
            List<Post> newDataList = gson.fromJson(response, new TypeToken<List<Post>>(){}.getType());
            postsDataList.addAll(newDataList);
            setDataToListView();
        } catch (Exception e) {
            Log.i("TagException", e.toString());
        }
    }

    void setDataToListView(){
        if(adapter==null) {
            adapter = new HomePostsListViewAdapter(getActivity(), R.layout.item_home_listview, postsDataList);
            listView.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }
    }

    String getBodyJson(){
        //Body
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("grant_type","password");
            jsonObject.put("username",Constants.usernameForToken);
            jsonObject.put("password",Constants.passwordForToken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    String encodeBase64(String text){
        byte[] data = new byte[0];
        try {
            data = text.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String base64 = Base64.encodeToString(data, Base64.NO_WRAP | Base64.URL_SAFE);
        return base64;
    }

}
