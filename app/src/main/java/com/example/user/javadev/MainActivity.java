package com.example.user.javadev;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Github Java users in Lagos json url
    private static final String url = "https://api.github.com/search/users?q=language:Java%20location:Lagos";

    // Progress dialog
    private ProgressDialog pDialog;

    // Array list
    private List<JavaUserListLagos> javaUserLagosList = new ArrayList<JavaUserListLagos>();

    // List view
    private ListView listView;

    // List Adapter
    private JavaListAdapter adapter;

    // TextView that is displayed when the list is empty
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        //Find the list in the xml layout with the reference code
        listView = (ListView) findViewById(R.id.list);

        //Setting the JavaListAdapter to the referenced listView
        adapter = new JavaListAdapter(this, javaUserLagosList);
        listView.setAdapter(adapter);

        //If there is no data to display set the emptyTextView in the xml resource layout to the listView
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        listView.setEmptyView(mEmptyStateTextView);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        //Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //Setting up the dialog to indicate loading of data
        pDialog = new ProgressDialog(this);

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
            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet);
        }

        // Set the ListView to enable filter mode
        listView.setTextFilterEnabled(true);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}









