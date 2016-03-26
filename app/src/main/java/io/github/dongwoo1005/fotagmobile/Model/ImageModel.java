package io.github.dongwoo1005.fotagmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Observable;

/**
 * Created by Dongwoo on 23/03/2016.
 */
public class ImageModel extends Observable implements Parcelable{

    private String filepath;
    private String uri;
    private int rating;

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

    public ImageModel(String filestr) {
        this.filepath = filestr;
        rating = 0;
    }

    private ImageModel(Parcel source){
        filepath = source.readString();
        uri = source.readString();
        rating = source.readInt();
    }

    public String getFilepath() {
        return filepath;
    }

    public String getUri() {
        return uri;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
        setChanged();
        notifyObservers("setRating");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filepath);
        dest.writeString(uri);
        dest.writeInt(rating);
    }


}
