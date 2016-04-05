package io.github.dongwoo1005.fotagmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Observable;

/**
 * Created by Dongwoo on 23/03/2016.
 */
public class ImageModel extends Observable implements Parcelable{

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel source) {
            return new ImageModel(source);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };

    private String mFilePath;
    private int mRating;

    public ImageModel(String filePath) {
        this.mFilePath = filePath;
        mRating = 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFilePath);
        dest.writeInt(mRating);
    }

    public String getFilePath() {
        return mFilePath;
    }

    public int getRating() {
        return mRating;
    }

    public void setRating(int rating) {
        this.mRating = rating;
        setChanged();
        notifyObservers("setRating");
    }

    private ImageModel(Parcel source){
        mFilePath = source.readString();
        mRating = source.readInt();
    }
}
