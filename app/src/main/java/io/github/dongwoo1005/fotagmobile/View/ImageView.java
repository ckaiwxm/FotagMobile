package io.github.dongwoo1005.fotagmobile.View;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;

import java.util.ArrayList;

import io.github.dongwoo1005.fotagmobile.DetailActivity;
import io.github.dongwoo1005.fotagmobile.Model.ImageModel;
import io.github.dongwoo1005.fotagmobile.R;

/**
 * Created by Dongwoo on 23/03/2016.
 */
public class ImageView extends RecyclerView.ViewHolder{

    public android.widget.ImageView thumbnail;
    public RatingBar thumbnailRate;
    public Button clearButton;
    private ArrayList<ImageModel> mImageCollectionOnView;

    public ImageView(View itemView, ArrayList<ImageModel> imageCollectionModel){

        super(itemView);
        this.mImageCollectionOnView = imageCollectionModel;
        thumbnail = (android.widget.ImageView) itemView.findViewById(R.id.thumbnail);
        thumbnailRate = (RatingBar) itemView.findViewById(R.id.thumbnail_rate);
        clearButton = (Button) itemView.findViewById(R.id.clearButton);
        registerControllers();
    }

    private void registerControllers(){
        ImageOnClickController imageController = new ImageOnClickController();
        thumbnail.setOnClickListener(imageController);
        thumbnailRate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mImageCollectionOnView.get(getAdapterPosition()).setRating((int) rating);
            }
        });
        clearButton.setOnClickListener(imageController);
    }

    private class ImageOnClickController implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.thumbnail:
                    Intent transitionIntent = new Intent(v.getContext(), DetailActivity.class);
                    transitionIntent.putExtra(DetailActivity.CURR_IMAGE, mImageCollectionOnView.get(getAdapterPosition()));
                    v.getContext().startActivity(transitionIntent);
                    break;
                case R.id.clearButton:
                    thumbnailRate.setRating(0F);
                    break;
            }
        }
    }

}