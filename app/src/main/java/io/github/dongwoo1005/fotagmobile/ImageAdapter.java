package io.github.dongwoo1005.fotagmobile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import io.github.dongwoo1005.fotagmobile.Model.ImageCollectionModel;
import io.github.dongwoo1005.fotagmobile.Model.ImageModel;
import io.github.dongwoo1005.fotagmobile.View.ImageView;

/**
 * Created by dwson on 3/23/16.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageView> implements Observer{

    private Context context;
    private ImageCollectionModel imageCollectionModel;
    private ArrayList<ImageModel> imageCollectionOnView;
    private int rating;

    public ImageAdapter(Context context, ImageCollectionModel imageCollectionModel, int rating) {

        this.context = context;
        this.rating = rating;
        this.imageCollectionModel = imageCollectionModel;
        imageCollectionModel.addObserver(this);
        imageCollectionOnView = new ArrayList<>();
        filter(rating);
    }

    public int filter(int rating){
        Log.d("Test", "filter");
        imageCollectionOnView.clear();
        ArrayList<ImageModel> original = imageCollectionModel.getImageList();
        this.rating = rating;
        for (ImageModel image : original){
            if (image.getRating() >= rating){
                Log.d("Test", "if:" + image.getRating() + " >=" + rating);
                imageCollectionOnView.add(image);
                image.addObserver(this);
                Log.d("Test", "addimagefromFilter");
            }
        }
        this.notifyDataSetChanged();
        return imageCollectionOnView.size();
    }

    @Override
    public ImageView onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_list_row, parent, false);
        ImageView holder = new ImageView(view, imageCollectionOnView);
        return holder;
    }

    @Override
    public void onBindViewHolder(ImageView holder, int position) {

        ImageModel currentImage = imageCollectionOnView.get(position);
//        holder.thumbnail.setImageBitmap(decodeFile(currentImage.getFilepath()));
        ImageDownloader imageDownloader = new ImageDownloader(holder, context, currentImage.getFilepath());
        imageDownloader.execute("thumb");
        holder.thumbnailRate.setRating(currentImage.getRating());
    }

    @Override
    public int getItemCount() {
        return imageCollectionOnView.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void update(Observable observable, Object data) {

        if (data.equals("addToImageList")){

            Log.d("Test", "update from addToImageList");
            // if rating on image >= rating on filter
            int lastIndex = imageCollectionModel.getImageList().size() - 1;
            ImageModel imageModel = imageCollectionModel.getImageList().get(lastIndex);
            if (rating <= imageModel.getRating()){
                imageCollectionOnView.add(imageModel);
                this.notifyDataSetChanged();
            }
        } else if (data.equals("deleteAll")){

            Log.d("Test", "update from delete");
            imageCollectionOnView.clear();
            this.notifyDataSetChanged();
        } else if (data.equals("setImageList")){

            Log.d("Test", "update from setImageList");
            this.notifyDataSetChanged();
        } else if (data.equals("setRating")){

            Log.d("Test", "update from setRating");
            List<Integer> toBeRemoved = new ArrayList<>();
            for (ImageModel image : imageCollectionOnView){
                Log.d("Test", "image rating:" + image.getRating() + ", rating: " + rating);
                if (image.getRating() < rating){
                    int index = imageCollectionOnView.indexOf(image);
                    Log.d("Test", "index" + index);
                    toBeRemoved.add(index);
                }
            }
            for (int i=0; i<toBeRemoved.size(); ++i){
                int index = toBeRemoved.get(i);
                imageCollectionOnView.remove(index);
                this.notifyItemRemoved(index);
            }
        }
    }
}
