package io.github.dongwoo1005.fotagmobile.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Dongwoo on 23/03/2016.
 */
public class ImageCollectionModel extends Observable implements Parcelable{

//    private List<ImageModel> imageList;
    private ArrayList<ImageModel> imageList;

    public ImageCollectionModel() {

        imageList = new ArrayList<>();
    }

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

    private ImageCollectionModel(Parcel in){
        deleteAll();
        imageList = new ArrayList<>();
        in.readTypedList(imageList, ImageModel.CREATOR);
    }

    public void addToImageList(ImageModel image){

        imageList.add(image);
        setChanged();
        notifyObservers("addToImageList");
    }

    public ArrayList<ImageModel> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<ImageModel> imageList) {
        this.imageList = imageList;
        setChanged();
        notifyObservers("setImageList");
    }

    public Boolean findIfFileExists(String filestr){
        for (ImageModel image : imageList){
            if (image.getFilepath().equals(filestr)){
                return true;
            }
        }
        return false;
    }

    public void deleteAll(){
        imageList.clear();
        setChanged();
        notifyObservers("deleteAll");
    }

    // Observer methods
    @Override
    public void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    @Override
    public synchronized void deleteObservers() {
        super.deleteObservers();
    }

    @Override
    public void notifyObservers() {
        super.notifyObservers();
    }

    @Override
    protected void setChanged() {
        super.setChanged();
    }

    @Override
    protected void clearChanged() {
        super.clearChanged();
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(imageList);
    }


}
