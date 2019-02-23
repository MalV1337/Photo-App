package com.photoapp.high.photoapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ImageViewHolder> {
    private static final String TAG = "1337 is HERE";
    private static final String IMAGE_TAG = "CLICKED_PICTURE";

    private int currentPosition = 0;

    private LayoutInflater inflater;
    protected Context mContext;
    private ImageDataModel imgmod;
    private File f;

    //Seperate the hashmap
    private DisplayMetrics metrics;
    private int screenWidth;


    public PictureAdapter(Context c, ImageDataModel imgmod){
        this.mContext = c;
        this.imgmod = imgmod;
        this.inflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = inflater.inflate(R.layout.content_show_all_pictures, parent, false);
        ImageViewHolder imgViewHold = new ImageViewHolder(view);
        return imgViewHold;
    }

    @Override
    public void onBindViewHolder(@NonNull final ImageViewHolder viewHolder, int position) {
        currentPosition = position;
        //Making picture visible again
        viewHolder.oneImg.setVisibility(View.VISIBLE);

        //Using Picasso library
        Picasso.get().load(imgmod.getAllUriImages().get(position)).into(viewHolder.oneImg);
        //viewHolder.oneImg.setImageURI(Uri.parse(imgmod.allUriImages.get(position))); //Old school solution
        viewHolder.picDescription.setText(imgmod.allHashtagImages.get(position));
    }

    @Override
    public int getItemCount() {
        return imgmod.allUriImages.size();
    }


    class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView oneImg;
        private TextView picDescription;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            oneImg = itemView.findViewById(R.id.recyclerViewPic);
            picDescription = itemView.findViewById(R.id.recyclerViewText);

            //OnClickListener for event
            oneImg.setOnClickListener(this);
            picDescription.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "Opening selected picture with getAdapterPosition(): " + getAdapterPosition());
            Intent intent = new Intent(mContext, ShowSinglePicture.class);
            intent.putExtra("uri_of_selected_picture", imgmod);
            intent.putExtra("clicked_position_of_recycle_item", getAdapterPosition());
            mContext.startActivity(intent);
        }
    }
}


