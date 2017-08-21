package com.example.user.javadev;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * Created by TheTundeDoherty on 7/17/2017.
 */

@SuppressWarnings("ALL")
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Github Java users in Lagos json url
    private static final String url = "https://api.github.com/search/users?q=language:Java%20location:Lagos";

    // Progress dialog
    private ProgressDialog pDialog;

    // Array list
    private List<JavaUserListLagos> javaUserLagosList = new ArrayList<JavaUserListLagos>();

    // Refresh Layout
    private SwipeRefreshLayout swipeRefreshLayout;

    // List view
    private ListView listView;

    // List Adapter
    private JavaListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Find the list in the xml layout with the reference code
        listView = (ListView) findViewById(R.id.list);


        //Set scroll listener to enable smooth scrolling on listView
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getChildAt(0) != null) {
                    swipeRefreshLayout.setEnabled(listView.getFirstVisiblePosition() == 0 && listView.getChildAt(0).getTop() == 0);
                }
            }
        });

        //Find the refresh layout by id
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        //Setting the JavaListAdapter to the referenced listView
        adapter = new JavaListAdapter(this, javaUserLagosList);
        listView.setAdapter(adapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //Setting up the dialog to indicate loading of data
        pDialog = new ProgressDialog(this);

        //on refresh listener to swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(this);

        //If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // Showing progress dialog before making http request
            pDialog.setMessage("Loading...");
            pDialog.show();

            // Creating volley request obj
            JsonObjectRequest javaListReq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, response.toString());
                            hidePDialog();

                            try {
                                JSONArray jsonArray = response.getJSONArray("items");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String profileName = jsonObject.getString("login");
                                    String avatarUrl = jsonObject.getString("avatar_url");
                                    String profileUrl = jsonObject.getString("html_url");
                                    int id = Integer.parseInt(jsonObject.optString("id").toString());

                                    // Create a new {@link JavaUserListLagos} object with the profileName, avatarUrl, profileUrl,
                                    // and id from the JSON response.
                                    JavaUserListLagos javaUserListLagos = new JavaUserListLagos(profileName, avatarUrl, profileUrl, id);

                                    // adding JavaUserLagosList to javaUserListLagos array
                                    javaUserLagosList.add(javaUserListLagos);
                                }

                                // Update the search list
                                adapter.updateSearchedList();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // notifying list adapter about data changes
                            // so that it renders the list view with updated data
                            adapter.notifyDataSetChanged();
                        }

                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    hidePDialog();

                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(javaListReq);
        } else {
            //Dispaly a toast to notify the user that there isn't internet connection
            makeText(this, "No Internet Connection", Toast.LENGTH_LONG).show();

        }
    }

    //Method to destroy the dialog box when data finished fetching
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    //Hide the dialog box
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu from the resources by using the menu inflater.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //Find the search widget in the menu item layout
        MenuItem searchItem = menu.findItem(R.id.action_search);

        //Associate the SearchView with the getActionView method, taking in searchItem as a parameter
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        //if searchView is not null set the SearchableInfo method to the searchView
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
            // Expand the search view and request focus
            searchView.setIconifiedByDefault(false);
        }

        //if searchView is not null set the onQueryTextListener method to the searchView
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    //Clear the focus
                    searchView.clearFocus();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //Perform the search
                    PerformSearch(newText);
                    return false;
                }
            });
        }

        return super.onCreateOptionsMenu(menu);
    }

    //Method to perform the search
    void PerformSearch(String query) {
        //Filter the adapter
        adapter.filter(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //If there is a network connection, start the refresh
        if (networkInfo != null && networkInfo.isConnected()) {

            //Set a toast notification to intimate the user that the network is fetching data
            makeText(this, "Refreshing list", Toast.LENGTH_LONG).show();

            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    //Clear the adapter
                    javaUserLagosList.clear();

                    //Do the refresh
                    fetchJavaUserListLagos();

                    //Set the refreshing method on the swipeRefreshLayout and enable the Refreshing animation
                    swipeRefreshLayout.setRefreshing(true);
                }
            });

        } else {
            //Dispaly a toast to notify the user that there isn't internet connection
            makeText(this, "Couldn't refresh list", Toast.LENGTH_SHORT).show();
        }
        //Diable the refreshing animation
        swipeRefreshLayout.setRefreshing(false);
    }

    public void fetchJavaUserListLagos() {
        //Handler to carry out the fetching of data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Creating volley request obj
                final JsonObjectRequest javaListReq = new JsonObjectRequest(Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, response.toString());

                                try {
                                    JSONArray jsonArray = response.getJSONArray("items");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        String profileName = jsonObject.getString("login");
                                        String avatarUrl = jsonObject.getString("avatar_url");
                                        String profileUrl = jsonObject.getString("html_url");
                                        int id = Integer.parseInt(jsonObject.optString("id").toString());

                                        // Create a new {@link JavaUserListLagos} object with the profileName, avatarUrl, profileUrl,
                                        // and id from the JSON response.
                                        JavaUserListLagos javaUserListLagos = new JavaUserListLagos(profileName, avatarUrl, profileUrl, id);

                                        // adding JavaUserLagosList to javaUserListLagos array
                                        javaUserLagosList.add(javaUserListLagos);
                                    }

                                    // Update the search list
                                    adapter.updateSearchedList();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                // notifying list adapter about data changes
                                // so that it renders the list view with updated data
                                adapter.notifyDataSetChanged();
                            }

                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                        hidePDialog();

                    }
                });

                //Adding request to request queue
                AppController.getInstance().addToRequestQueue(javaListReq);

                //Creating a new list of JavaUserListLagos
                List<JavaUserListLagos> javaUserLagosList = new ArrayList<JavaUserListLagos>();

                //Add the new items
                javaUserLagosList.addAll(javaUserLagosList);

                //Set the adapter to the new list
                listView.setAdapter(adapter);

                // Notify the adapter of the changes
                adapter.notifyDataSetChanged();

                //Disable the Refreshing animation
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 10000);
    }
}









