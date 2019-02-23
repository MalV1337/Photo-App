package com.photoapp.high.photoapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.photoapp.high.photoapp.SupportMethods.*;

public class MainActivity extends AppCompatActivity {

    //Data Model
    private ImageDataModel imgMod = new ImageDataModel();

    private TextView textTargetUri;
    private ImageView targetImage;
    private Button buttonLoadImage;
    private Button showAllPicBtn;
    private Button applyBtn;
    private EditText editHashtag;
    private Uri currentSelectedUri;
    private String currentHashtag;
    private boolean selfPhotoExperimental;
    private boolean savePhotosExperimental;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final String EMPTY_STRING_TAG = "";
    private static final String TAG = "1337";
    private static final String SAVE_TAG = "save_all_info";
    private static final String LATEST_URI_TAG = "latest_uri";
    private static final String LATEST_HASHTAG_TAG = "latest_hashtag";
    private static final String SHARED_TAG = "HiGH";
    private static final String REAL_PATH_TAG = "Real_path:";
    private static final String REAL_PATH_SIZE_TAG = "Size_of_Real_path_Array";
    private static final String HASHTAG_TAG = "Hashtag:";
    private static final String HASHTAG_TAG_SIZE_TAG = "Size_of_Hashtag_path_Array";

    public MainActivity() {
        this.selfPhotoExperimental = false;
        this.savePhotosExperimental = false;
    }


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        pref = getSharedPreferences(SHARED_TAG, 0);
        int size_of_uri_array = pref.getInt(REAL_PATH_SIZE_TAG, 0);
        for (int i = 0; i < size_of_uri_array; i++) {
            File f = new File(pref.getString(REAL_PATH_TAG + i, ""));
            Log.i(TAG, Uri.fromFile(f).toString());
            MediaScannerConnection.scanFile(this, new String[] { f.getAbsolutePath() }, null,
                    (path, uri) -> Log.i(TAG, uri.toString()));

            //More semantic code
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getAbsolutePath()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            imgMod.getAllUriImages().add(uri.toString());
                        }
                    });

            //Lambda version
            /*MediaScannerConnection.scanFile(this,
                    new String[]{f.getAbsolutePath()}, null,
                    (path, uri) -> imgMod.getAllUriImages().add(uri));*/

            imgMod.getAllHashtagImages().add(pref.getString(HASHTAG_TAG + i, EMPTY_STRING_TAG));
        }

        initializeAppElements();

        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });

        applyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgMod.getAllUriImagesSize() > imgMod.getAllHashtagImagesSize()){
                    imgMod.getAllHashtagImages().add(editHashtag.getText().toString());

                    clearEditViewAndCurrentUri();

                    Log.i(TAG, "Uri list size is " + imgMod.getAllUriImages().size());
                    Log.i(TAG, "Hashtag list size is " + imgMod.getAllHashtagImages().size());
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "Abort - Only one hashtag per picture", Toast.LENGTH_SHORT).show();
                }

            }
        });

        showAllPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Uri list size is " + imgMod.getAllUriImages().size());
                Log.i(TAG, "Hashtag list size is " + imgMod.getAllHashtagImages().size());
                if (checkPictureOrder()) {
                    Intent intent = new Intent(getApplicationContext(), ShowAllPictures.class);
                    intent.putExtra(TAG, imgMod);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean checkPictureOrder() {
        //return imgMod.getAllHashtagImagesSize() == imgMod.getAllUriImagesSize();
        if(imgMod.getAllUriImagesSize() == 0 && imgMod.getAllHashtagImagesSize() == 0) {
            Toast.makeText(getApplicationContext(),
                    "There are no picture(s) yet", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(imgMod.getAllHashtagImagesSize() < imgMod.getAllUriImagesSize()){
            Toast.makeText(getApplicationContext(),
                    "Please add hashtag(s) for the previous picture(s)", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(imgMod.getAllHashtagImagesSize() > imgMod.getAllUriImagesSize()){
            Toast.makeText(getApplicationContext(),
                    "Please add more picture(s) for the previous hashtag(s)", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initializeAppElements() {
        currentSelectedUri = null;
        buttonLoadImage = (Button) findViewById(R.id.loadimage);
        targetImage = (ImageView) findViewById(R.id.targetimage);
        showAllPicBtn = (Button) findViewById(R.id.showAllPicBtn);
        applyBtn = (Button) findViewById(R.id.applyBtn);
        editHashtag = (EditText) findViewById(R.id.editHashtag);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (currentSelectedUri != null) {
            outState.putSerializable(SAVE_TAG, imgMod);
            outState.putParcelable(LATEST_URI_TAG, currentSelectedUri);
            outState.putString(LATEST_HASHTAG_TAG, editHashtag.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            imgMod = (ImageDataModel) savedInstanceState.getSerializable(SAVE_TAG);
            currentSelectedUri = savedInstanceState.getParcelable(LATEST_URI_TAG);
            currentHashtag = savedInstanceState.getString(LATEST_HASHTAG_TAG);
            targetImage.setImageURI(currentSelectedUri);
            editHashtag.setText(currentHashtag);
        }
    }

    private void clearEditViewAndCurrentUri() {
        editHashtag.getText().clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            switch (requestCode) {
                case 0:
                    processReceivingUri(data);
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    processReceivingUri(data);
                    break;
                case REQUEST_CAPTURE_IMAGE:
                    processReceivingUri(data);
                    break;
            }
        }
    }

    private void processReceivingUri(Intent data) {
        Uri targetUri = data.getData();
        Log.i(TAG, targetUri.toString());
        imgMod.getAllUriImages().add(targetUri.toString());
        currentSelectedUri = targetUri;
        Glide.with(MainActivity.this).load(targetUri).into(targetImage);
        Log.i(TAG, "TargetUri" + targetUri.toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the main_menu from the menu resource folder
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAllPic:
                clearSession();
                return (true);
            case R.id.saveConf:
                saveSettings();
                return (true);
            case R.id.clearConf:
                clearingSettings();
                return (true);
            case R.id.takingPic:
                takingPicWithIntent();
                return (true);
            case R.id.credit:
                showCredits();
                return (true);
            case R.id.exitApp:
                finish();
                System.exit(0);
                return (true);
        }
        return (super.onOptionsItemSelected(item));
    }

    private void showCredits() {
        final Dialog diag = new Dialog(MainActivity.this);
        diag.setContentView(R.layout.custom_dialog);

        //Setting up for credit dialog
        Button closingBtn = (Button) diag.findViewById(R.id.closingBtn);

        //Using lambda
        closingBtn.setOnClickListener(v -> diag.dismiss());

        diag.show();
    }

    private void clearSession() {
        imgMod.getAllUriImages().clear();
        imgMod.getAllHashtagImages().clear();
        targetImage.setImageResource(0);
    }


    private File createImageFile() throws IOException {
        String imageFilePath;
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void takingPicWithIntent() {
        if (warningUserFeatureV2()) return;

        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.android.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                startActivityForResult(pictureIntent,
                        REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private boolean warningUserFeatureV1() {
        if(!savePhotosExperimental){
            Toast.makeText(getApplicationContext(),
                    "This feature might crash in the future - Tap again at your own risk ", Toast.LENGTH_SHORT).show();
            savePhotosExperimental = true;
            return true;
        }
        return false;
    }

    private boolean warningUserFeatureV2() {
        if(!selfPhotoExperimental){
            Toast.makeText(getApplicationContext(),
                    "This feature might crash your app - Tap again at your own risk ", Toast.LENGTH_SHORT).show();
            selfPhotoExperimental = true;
            return true;
        }
        return false;
    }

    private void saveSettings() {
        if (warningUserFeatureV1()) return;

        //SharedPreferences - Save all credentials after closing app
        pref = getApplicationContext().getSharedPreferences(SHARED_TAG, MODE_PRIVATE);
        editor = pref.edit();

        //Clear first before add new one
        pref.edit().clear().apply();

        for (int i = 0; i < imgMod.getAllUriImagesSize(); i++) {
            //Invoke custom Stackoverflow method for conversion
            editor.putString(REAL_PATH_TAG + i, getPath(this, Uri.parse(imgMod.getAllUriImages().get(i))));
            editor.putString(HASHTAG_TAG + i, imgMod.getAllHashtagImages().get(i));
        }
        editor.putInt(REAL_PATH_SIZE_TAG, imgMod.getAllUriImagesSize());
        editor.putInt(HASHTAG_TAG_SIZE_TAG, imgMod.getAllHashtagImagesSize());
        editor.apply();
    }

    private void clearingSettings() {
        pref.edit().clear().apply();
        imgMod.getAllHashtagImages().clear();
        imgMod.getAllUriImages().clear();
    }
}

