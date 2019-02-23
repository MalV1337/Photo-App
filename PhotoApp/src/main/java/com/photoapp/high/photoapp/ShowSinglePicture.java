package com.photoapp.high.photoapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

public class ShowSinglePicture extends AppCompatActivity {

    private int position;
    private ImageDataModel imgMod;
    //private ImageView imgViewPicture; //Without zoom
    private TouchImageView imgViewPictureZoom; //With zoom

    private static final String TAG = "1337";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_single_picture);

        //Give it a back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //imgViewPicture = (ImageView) findViewById(R.id.imgViewPicture);
        imgViewPictureZoom = (TouchImageView) findViewById(R.id.imgViewPicture);

        Intent intent = getIntent();

        imgMod = (ImageDataModel) getIntent().getSerializableExtra("uri_of_selected_picture");
        position = (int) getIntent().getSerializableExtra("clicked_position_of_recycle_item");

        Log.i(TAG, "Uri is " + imgMod.getAllUriImages().get(position).toString()); //Only URI
        //Log.i(TAG, getRealPathFromURI(getApplicationContext(), imgMod.getAllUriImages().get(position)));

        ////With Picasso
        //Picasso.get().load(imgMod.getAllUriImages().get(position)).into(imgViewPictureZoom);
        imgViewPictureZoom.setImageURI(Uri.parse(imgMod.getAllUriImages().get(position)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getParentActivityIntent() == null) {
                    onBackPressed();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
