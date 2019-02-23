package com.photoapp.high.photoapp;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class ShowAllPictures extends AppCompatActivity {
    private static final String TAG = "1337";

    private ImageDataModel imgMod;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private PictureAdapter pictureAdapter; //important adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_pictures);

        //Give it a back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgMod = (ImageDataModel) getIntent().getSerializableExtra(TAG);

        //Initialization only
        recyclerView = findViewById(R.id.recyclerViewPic);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setHasFixedSize(true);
        //recyclerView.setItemViewCacheSize(20);
        //recyclerView.setDrawingCacheEnabled(true);
        //recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setLayoutManager(gridLayoutManager);

        //Other parts
        pictureAdapter = new PictureAdapter(this, imgMod);
        recyclerView.setAdapter(pictureAdapter);
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
