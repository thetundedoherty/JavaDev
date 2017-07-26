package com.example.user.javadev;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by USER on 7/17/2017.
 */

public class JavaListAdapter extends BaseAdapter {
    //Activity
    private Activity activity;

    //Layout inflater to inflate the the list item
    private LayoutInflater inflater;

    //List of java lang User in lagos with the items passed in as a parameter
    private List<JavaUserListLagos> javaUserListLagosItems;

    //List of java lang user search in the SearchView set as null
    private List<JavaUserListLagos> javaItemSearched = null;

    //Volley ImageLoader to load the NetworkImageView
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    /**
     *
     * @param activity
     * @param javaUserListLagosItems
     */
    public JavaListAdapter(Activity activity, List<JavaUserListLagos> javaUserListLagosItems) {
        this.activity = activity;
        this.javaUserListLagosItems = javaUserListLagosItems;
        this.javaItemSearched = new ArrayList<JavaUserListLagos>();
        this.javaItemSearched.addAll(javaUserListLagosItems);
    }

    //Method to update the search List
    public void updateSearchedList() {
        javaItemSearched.addAll(javaUserListLagosItems);
    }

    @Override
    public int getCount() {
        return javaUserListLagosItems.size();
    }

    /**
     *
     * @param location
     * @return
     */
    @Override
    public Object getItem(int location) {
        return javaUserListLagosItems.get(location);
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
        final TextView profileName = (TextView) convertView.findViewById(R.id.profile_name_text_view);
        final TextView userId = (TextView) convertView.findViewById(R.id.id_text_view);
        final TextView profileUrl = (TextView) convertView.findViewById(R.id.profile_url_text_view);

        // Setup share button image to send message about the user using intent
        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out this awesome developer " + profileName.getText() + " with the link " + profileUrl.getText());
                sendIntent.setType("text/plain");
                activity.startActivity(sendIntent);
            }
        });

        // getting  data for the list
        JavaUserListLagos javaUserListLagos = javaUserListLagosItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(javaUserListLagos.getmAvatarUrl(), imageLoader);

        // Profile name
        profileName.setText(javaUserListLagos.getmLogin());

        //profile url
        profileUrl.setText(javaUserListLagos.getmProfileUrl());

        // User id
        userId.setText("User ID: " + String.valueOf(javaUserListLagos.getmId()));

        return convertView;
    }

    // Filter Class
    /**
     *
     * @param charText
     */
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        javaUserListLagosItems.clear();
        if (charText.length() == 0) {
            javaUserListLagosItems.addAll(javaItemSearched);
        } else {
            for (JavaUserListLagos wp : javaItemSearched) {
                if (wp.getmLogin().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    javaUserListLagosItems.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

}



