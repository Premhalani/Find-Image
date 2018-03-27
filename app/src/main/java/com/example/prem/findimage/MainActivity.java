package com.example.prem.findimage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.example.prem.findimage.adapters.CustomRecyclerViewAdapter;
import com.example.prem.findimage.dataobjects.Image;
import com.example.prem.findimage.dataobjects.ImageFactory;
import com.example.prem.findimage.dataobjects.SearchObject;
import com.example.prem.findimage.room.AppDatabase;
import com.example.prem.findimage.util.DataHelper;
import com.example.prem.findimage.util.EndlessRecyclerViewScrollListener;
import com.example.prem.findimage.util.RecyclerViewClickListener;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.pwittchen.infinitescroll.library.InfiniteScrollListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Main activity to display all the images got from Imgur
 */
public class MainActivity extends AppCompatActivity {
    private FloatingSearchView searchView;
    private List<SearchObject> searchHistory = new ArrayList<>();
    //private ArrayList<Image> searchImages = new ArrayList<>();
    private ImageFactory imageFactory;
    private OkHttpClient httpClient;
    private RecyclerView recyclerView;
    private LinearLayoutManager reLayoutManager;
    private CustomRecyclerViewAdapter customRecyclerViewAdapter;
    private RecyclerViewClickListener listener;
    private LinearLayout newSearchLayout;
    private String lastQuery="";
    private ProgressBar progressBar;
    private EndlessRecyclerViewScrollListener infiniteScrollListener;
    private AppDatabase db;
    private CountDownTimer timer;
    private int lastPageCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getAppDatabase(this);
        listener = new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this,ImageViewActivity.class);
                //intent.putExtra("image",searchImages);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        };
        initViews();
        httpClient = new OkHttpClient.Builder().build();
        Fresco.initialize(this);
        getSearchHistory();
        if(!isConnectedToInternet()) showSnackbar();
    }

    /**
     * Initialize all the views
     */
    private void initViews(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Find Image");
        imageFactory = ImageFactory.getImageFactory();
        searchView = findViewById(R.id.floating_search_view);
        recyclerView = findViewById(R.id.image_list);
        reLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(reLayoutManager);
        customRecyclerViewAdapter = new CustomRecyclerViewAdapter(this,imageFactory.getImageList(),listener);
        recyclerView.setAdapter(customRecyclerViewAdapter);
        progressBar = findViewById(R.id.progress_bar);
        newSearchLayout = findViewById(R.id.new_search);
        infiniteScrollListener = new EndlessRecyclerViewScrollListener(reLayoutManager) {
            //Endless Scrolling effect
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                lastPageCount = page;
                getSearchResult(lastQuery);
            }
        };
        recyclerView.addOnScrollListener(infiniteScrollListener);

    }

    /**
     * Clears the list and resets the page count to 1
     */
    private void clear(){
        //searchImages.clear();
        imageFactory.clear();
        customRecyclerViewAdapter.notifyDataSetChanged();
        lastPageCount = 0;
        infiniteScrollListener.resetState();
    }

    public void showSnackbar(){
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.coordinator_layout),"Internet Connection not available", Snackbar.LENGTH_SHORT);
        View view = mySnackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.RED);
        view.setBackgroundColor(Color.WHITE);
        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        mySnackbar.show();
    }
    /**
     * Check for internet connection
     */
    public boolean isConnectedToInternet(){
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    /**
     * Set up all the the search bar and handle its events
     */
    private void setupSearchView(){

        /**
         * Called when user types into searchbar
         */
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(final String oldQuery, final String newQuery) {
                //Added this to see if the user stops typing
                //Only fires the query 1 second after the user stops typing.
                if(timer != null)
                    timer.cancel();
                timer = new CountDownTimer(1000,1000) {
                    @Override
                    public void onTick(long l) {

                    }
                    @Override
                    public void onFinish() {
                        if(!newQuery.equals("") &&  oldQuery!=""){
                            searchView.clearSuggestions();
                            cancelQuery(httpClient,oldQuery);
                            clear();
                            getSearchResult(newQuery);
                            lastQuery = newQuery;
                        }else{
                            searchView.swapSuggestions(searchHistory);
                            lastQuery="";
                        }
                    }
                }.start();


            }
        });
        /**
         * Used to show history
         */ 
        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                searchView.setSearchText(searchSuggestion.getBody());
                lastQuery = searchSuggestion.getBody();
                clear();
                getSearchResult(lastQuery);
            }

            @Override
            public void onSearchAction(String currentQuery) {
                if(!currentQuery.equals("")) {
                    int i = DataHelper.alreadyExist(currentQuery, searchHistory);
                    if (i == -1) {
                        if (searchHistory.size() == 10) {
                            searchHistory.remove(9);
                        }
                        SearchObject searchObject = new SearchObject(currentQuery);
                        searchObject.setHistory(true);
                        searchHistory.add(0, searchObject);
                    } else {
                        searchHistory.add(0, searchHistory.remove(i));
                    }
                    lastQuery = currentQuery;
                }
            }
        });
        /**
         * Called when focus changes on the searchbar
         */
        searchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                if(lastQuery == "")
                searchView.swapSuggestions(searchHistory);
            }

            @Override
            public void onFocusCleared() {
                if(lastQuery.equals("")){
                    //searchImages.clear();
                    imageFactory.clear();
                    customRecyclerViewAdapter.notifyDataSetChanged();
                    newSearchLayout.setVisibility(View.VISIBLE);
                }else {
                    int i = DataHelper.alreadyExist(lastQuery, searchHistory);
                    if (i == -1) {
                        if (searchHistory.size() == 10) {
                            searchHistory.remove(9);
                        }
                        SearchObject searchObject = new SearchObject(lastQuery);
                        searchObject.setHistory(true);
                        searchHistory.add(0, searchObject);
                    } else {
                        searchHistory.add(0, searchHistory.remove(i));
                    }
                }
                searchView.setSearchText(lastQuery);
            }
        });
        /**
         * Required to display history Icon besides the search history item
         */
        searchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                SearchObject searchObject = (SearchObject) item;

                String textColor = "#000000";
                String textLight = "#787878";

                if (searchObject.getHistory()) {
                    leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.ic_history_black_24dp, null));
                    Util.setIconColor(leftIcon, Color.parseColor(textColor));
                    leftIcon.setAlpha(.36f);
                } else {
                    leftIcon.setAlpha(0.0f);
                    leftIcon.setImageDrawable(null);
                }

                textView.setTextColor(Color.parseColor(textColor));
                String text = searchObject.getBody()
                        .replaceFirst(searchView.getQuery(),
                                "<font color=\"" + textLight + "\">" + searchView.getQuery() + "</font>");
                textView.setText(Html.fromHtml(text));
            }
        });
    }

    /**
     * Save the search history for this session in to db
     */
    @Override
    protected void onStop() {
        super.onStop();
        saveSearchHistory();
    }

    /**
     * Helper to save search history
     */
    public void saveSearchHistory(){
        removeSearchFromDb();
        addSearchToDb(searchHistory);
    }

    /**
     * Main method to get reponse from imgur for the given query
     * Called everytime the text gets changed
     * @param query
     */
    private void getSearchResult(String query){
        if(!isConnectedToInternet()){
            showSnackbar();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        Request request = new Request.Builder()
                .url("https://api.imgur.com/3/gallery/search/"+lastPageCount+"?q="+query)
                .header("Authorization","Client-ID c4a8c12f3703c93")
                .header("User-Agent","ImageSearch")
                .tag(query)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Server Response",e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject data = new JSONObject(response.body().string());
                    JSONArray items = data.getJSONArray("data");
                    for(int i=0;i<items.length();i++) {
                        JSONObject image_object = items.getJSONObject(i);
                        Image image = new Image();
                        if (image_object.getBoolean("is_album"))
                        {
                            image.setId(image_object.getString("cover"));
                        }else {
                            image.setId(image_object.getString("id"));
                        }
                        image.setTitle(image_object.getString("title"));
                        image.setUrl("https://i.imgur.com/"+ image.getId()+"l.jpg");    //Get Large Bitmap
                        image.setDescription(image_object.getString("description"));
                        //searchImages.add(image);
                        imageFactory.addImage(image);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         * Update UI on main thread
                         */
                        //if(searchImages.size() == 0){
                        if(imageFactory.size() == 0){
                            Toast.makeText(getApplicationContext(),"No Images Found",Toast.LENGTH_SHORT).show();
                            newSearchLayout.setVisibility(View.VISIBLE);
                        }else{
                            newSearchLayout.setVisibility(View.GONE);
                        }
                        progressBar.setVisibility(View.GONE);
                        customRecyclerViewAdapter.notifyDataSetChanged();
                    }
                });
           //
            }

        });
    }

    /**
     * cancelQuery a request if the user is typing fast to improve network effeciency
     * @param client
     * @param tag
     */
    public void cancelQuery(OkHttpClient client, Object tag) {
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) call.cancel();
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) call.cancel();
        }
    }

    /**
     * Adds search object to db
     * @param object
     */
    public void addSearchToDb(final List<SearchObject> object){
        new AsyncTask<Void,Void,Void>(){
            List<SearchObject> searchObjects = object;
            @Override
            protected Void doInBackground(Void... voids) {
                for(SearchObject searchObject : searchObjects)
                    db.taskDao().insertALL(searchObject);
                return null;
            }
        }.execute();
    }

    /**
     * Removes all elements from db
     */
    public void removeSearchFromDb(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                db.taskDao().deleteAll();
                return null;
            }
        }.execute();
    }

    /**
     * Retrieves search history from db
     */
    public void getSearchHistory(){
        new AsyncTask<Void,Void,List<SearchObject>>(){
            @Override
            protected List<SearchObject> doInBackground(Void... voids) {
                return db.taskDao().getAll();
            }

            @Override
            protected void onPostExecute(List<SearchObject> searchObjects) {
                super.onPostExecute(searchObjects);
                searchHistory = searchObjects;
                setupSearchView();
            }
        }.execute();
    }
}
