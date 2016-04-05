package io.github.dongwoo1005.fotagmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Dongwoo on 23/03/2016.
 */
public class ImageCollectionModel extends Observable implements Parcelable{

    public static final Parcelable.Creator<ImageCollectionModel> CREATOR = new Creator<ImageCollectionModel>() {
        @Override
        public ImageCollectionModel createFromParcel(Parcel source) {
            return new ImageCollectionModel(source);
        }

        @Override
        public ImageCollectionModel[] newArray(int size) {
            return new ImageCollectionModel[size];
        }
    };

    private ArrayList<ImageModel> mImageList;

    public ImageCollectionModel() {

        mImageList = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mImageList);
    }

    public void addToImageList(ImageModel image){

        mImageList.add(image);
        setChanged();
        notifyObservers("addToImageList");
    }

    public ArrayList<ImageModel> getImageList() {
        return mImageList;
    }

    public int deleteAll(){
        int ret = mImageList.size();
        mImageList.clear();
        setChanged();
        notifyObservers("deleteAll");
        return ret;
    }

    public Boolean findIfFileExists(String filePath){
        for (ImageModel image : mImageList){
            if (image.getFilePath().equals(filePath)){
                return true;
            }
        }
        return false;
    }

    private ImageCollectionModel(Parcel in){
        deleteAll();
        mImageList = new ArrayList<>();
        in.readTypedList(mImageList, ImageModel.CREATOR);
    }
}
