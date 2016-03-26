package io.github.dongwoo1005.fotagmobile.View;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import java.util.Observable;
import java.util.Observer;

import io.github.dongwoo1005.fotagmobile.Model.ImageCollectionModel;
import io.github.dongwoo1005.fotagmobile.R;

/**
 * Created by Dongwoo on 23/03/2016.
 */
public class ImageCollectionView extends LinearLayout implements Observer {

    private ImageCollectionModel imageCollectionModel;

    public ImageCollectionView(Context context, ImageCollectionModel imageCollectionModel) {
        super(context);

        View.inflate(context, R.layout.content_main, this);

        this.imageCollectionModel = imageCollectionModel;
        imageCollectionModel.addObserver(this);
    }

    @Override
    public void update(Observable observable, Object data) {

    }
}
