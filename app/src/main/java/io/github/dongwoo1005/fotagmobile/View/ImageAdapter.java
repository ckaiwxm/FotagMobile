package io.github.dongwoo1005.fotagmobile.View;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.github.dongwoo1005.fotagmobile.ImageLoader;
import io.github.dongwoo1005.fotagmobile.Model.ImageCollectionModel;
import io.github.dongwoo1005.fotagmobile.Model.ImageModel;
import io.github.dongwoo1005.fotagmobile.R;

/**
 * Created by dwson on 3/23/16.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageView> implements Observer{

    private Context mContext;
    private ImageCollectionModel mImageCollectionModel;
    private ArrayList<ImageModel> mImageCollectionOnView;
    private int mRating;

    public ImageAdapter(Context context, ImageCollectionModel imageCollectionModel, int rating) {

        this.mContext = context;
        this.mRating = rating;
        this.mImageCollectionModel = imageCollectionModel;
        imageCollectionModel.addObserver(this);
        mImageCollectionOnView = new ArrayList<>();
        filter(rating);
    }

    @Override
    public ImageView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_row, parent, false);
        ImageView holder = new ImageView(view, mImageCollectionOnView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageView holder, int position) {

        ImageModel currentImage = mImageCollectionOnView.get(position);
        ImageLoader imageLoader = new ImageLoader(holder, mContext, currentImage.getFilePath());
        imageLoader.execute("thumb");
        holder.thumbnailRate.setRating(currentImage.getRating());
    }

    @Override
    public int getItemCount() {
        return mImageCollectionOnView.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void update(Observable observable, Object data) {

        if (data.equals("addToImageList")){

            // if rating on image >= rating on filter
            int lastIndex = mImageCollectionModel.getImageList().size() - 1;
            ImageModel imageModel = mImageCollectionModel.getImageList().get(lastIndex);
            if (mRating <= imageModel.getRating()){
                mImageCollectionOnView.add(imageModel);
                this.notifyDataSetChanged();
            }
        } else if (data.equals("deleteAll")){

            mImageCollectionOnView.clear();
            this.notifyDataSetChanged();
        } else if (data.equals("setImageList")){

            this.notifyDataSetChanged();
        } else if (data.equals("setRating")){

            List<Integer> toBeRemoved = new ArrayList<>();
            for (ImageModel image : mImageCollectionOnView){
                if (image.getRating() < mRating){
                    int index = mImageCollectionOnView.indexOf(image);
                    toBeRemoved.add(index);
                }
            }
            for (int i=0; i<toBeRemoved.size(); ++i){
                int index = toBeRemoved.get(i);
                mImageCollectionOnView.remove(index);
                this.notifyItemRemoved(index);
            }
        }
    }

    public int filter(int rating){
        mImageCollectionOnView.clear();
        ArrayList<ImageModel> original = mImageCollectionModel.getImageList();
        this.mRating = rating;
        for (ImageModel image : original){
            if (image.getRating() >= rating){
                mImageCollectionOnView.add(image);
                image.addObserver(this);
            }
        }
        this.notifyDataSetChanged();
        return mImageCollectionOnView.size();
    }
}
