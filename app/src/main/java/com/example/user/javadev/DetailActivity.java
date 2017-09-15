package com.example.user.javadev;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by TheTundeDoherty on 9/9/2017.
 */

public class DetailActivity extends AppCompatActivity {

    // User Name
    private static String Title = "title";

    // User Id
    private static String Id = "id";

    // User Profile Url
    private static String Profile="profile";

    // FloatingActionButton
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Getting the Intent from MainActivity
        Intent intent = getIntent();

        // Getting the instance of the ImageLoader
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        // Getting the String User Name
        String name = intent.getStringExtra(Title);

        // Getting the String User Id
        String id = intent.getStringExtra(Id);

        // Getting the String Profile Url
        String profileUrl = intent.getStringExtra(Profile);

        // Getting the String bitmap image
        String bitmap = intent.getStringExtra("images");

        // Getting the reference to the CircularNetworkImageView in the activity_detail xml
        CircularNetworkImageView thumbNail = (CircularNetworkImageView) findViewById(R.id.thumbnail);

        // Getting the reference to the Profile Name of the user in the activity_detail xml
        TextView labelName = (TextView) findViewById(R.id.name_label);

        // Getting the reference to the Profile Url of the user in the activity_detail xml
        TextView profileLink = (TextView) findViewById(R.id.profile_url_text_view);

        // Getting the reference to the Profile Id of the user in the activity_detail xml
        TextView identity = (TextView) findViewById(R.id.id_text_view);

        // Setting the imageUrl method to the  thumbnail passing in imageLoader and bitmap as a parameter
        thumbNail.setImageUrl(bitmap, imageLoader);

        // Setting the setText method to the labelName passing in name as a parameter
        labelName.setText(name);

        // Setting the setText method to the profileLink passing in profileUrl as a parameter
        profileLink.setText(profileUrl);

        // Setting the setText method to the identity passing in id as a parameter
        identity.setText(id);

        // Getting the reference to the floatingActionButton in the activity_detail xml
        fab = (FloatingActionButton) findViewById(R.id.fab);

        // Setting OnClickListener on the  floatingActionButton
        fab.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                //Creating and parsing Github Uri
                Uri uri = Uri.parse("https://github.com/join");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                //Starting the web Intent
                startActivity(intent);
            }
        });

    }

}

