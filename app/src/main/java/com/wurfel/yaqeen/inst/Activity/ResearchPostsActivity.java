package com.wurfel.yaqeen.inst.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.wurfel.yaqeen.inst.Adapter.HomePostsListViewAdapter;
import com.wurfel.yaqeen.inst.Helper.Constants;
import com.wurfel.yaqeen.inst.Interface.VolleyResponseListener;
import com.wurfel.yaqeen.inst.R;
import com.wurfel.yaqeen.inst.ResponseModels.Posts.Post;
import com.wurfel.yaqeen.inst.utils.VolleyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResearchPostsActivity extends AppCompatActivity {

    Gson gson;
    ListView listView;
    List<Post> postsDataList = new ArrayList<>();
    HomePostsListViewAdapter adapter;
    private int preLast=0;
    private Boolean moreScrollFlag=true;
    private int offset=0;
    int pageNum = 1;
    int perPageItems = 15;
    String toolbarTitle = "";
    String tagId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorStatusBar));
        }
        setContentView(R.layout.activity_research_posts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentData();
        changeToolbarTitle();
        initializeControls();
        setListeners();
        if(tagId!=null) {
            requestForPosts();
        }
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
            toolbarTitle = extras.getString("toolbarTitle",getResources().getString(R.string.app_name));
            tagId = extras.getString("tagId",null);
        }
    }

    void changeToolbarTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView TextViewTitle = (TextView) toolbar.findViewById(R.id.textView_toolbarTilte);
        TextViewTitle.setText(toolbarTitle);
    }

    void initializeControls(){
        listView = (ListView) findViewById(R.id.listView_research);
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

                Intent intent = new Intent(ResearchPostsActivity.this,SinglePostActivity.class);
                intent.putExtra("toolbarTitle","Yakeen");
                Bundle bundle = new Bundle();
                bundle.putSerializable("postData", postsDataList.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }});

    }

    public void requestForPosts(){
        //Headers
        HashMap<String, String> headers = new HashMap<String, String>();

        //Params
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("access_token", Constants.accessToken);
        params.put("page", pageNum+"");
        params.put("per_page", perPageItems+"");
        params.put("tags", tagId+"");
//        params.put("Accept", "application/json");

        String URL_POST = this.getResources().getString(R.string.url_posts);
        VolleyUtils.GET_METHOD(ResearchPostsActivity.this, URL_POST,params,headers, new VolleyResponseListener() {
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
            adapter = new HomePostsListViewAdapter(ResearchPostsActivity.this, R.layout.item_home_listview, postsDataList);
            listView.setAdapter(adapter);
        }
        else {
            adapter.notifyDataSetChanged();
        }
    }

}
