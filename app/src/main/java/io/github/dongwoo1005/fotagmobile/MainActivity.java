package io.github.dongwoo1005.fotagmobile;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.IOException;

import io.github.dongwoo1005.fotagmobile.Model.ImageCollectionModel;
import io.github.dongwoo1005.fotagmobile.Model.ImageModel;
import io.github.dongwoo1005.fotagmobile.View.ImageAdapter;

public class MainActivity extends AppCompatActivity implements EnterUriDialog.EnterUriDialogListener{

    private ImageCollectionModel mImageCollectionModel;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private ImageAdapter mImageAdapter;

    private Boolean mIsFabOpen = false;
    private FloatingActionButton mFabAdd, mFabLoad, mFabLink, mFabSearch;
    private Animation mFabOpen, mFabClose, mRotateForward, mRotateBackward;
    private RatingBar mRatingBar;
    private int mCurrRateFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Drawable logo = getDrawable(R.drawable.ic_fotag_logo);
        toolbar.setLogo(logo);
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View child = toolbar.getChildAt(i);
            if (child != null)
                if (child.getClass() == ImageView.class) {
                    ImageView iv2 = (ImageView) child;
                    if ( iv2.getDrawable() == logo ) {
                        iv2.setAdjustViewBounds(true);
                    }
                }
        }

        mFabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        mFabLoad = (FloatingActionButton) findViewById(R.id.fab_load);
        mFabLink = (FloatingActionButton) findViewById(R.id.fab_link);
        mFabSearch = (FloatingActionButton) findViewById(R.id.fab_search);


        mCurrRateFilter = 0;
        if (savedInstanceState != null){
            mCurrRateFilter = savedInstanceState.getInt("FILTER");
        }
        mRatingBar = (RatingBar) findViewById(R.id.toolbar_ratingBar);
        mRatingBar.setRating(mCurrRateFilter);

        mFabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        mFabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        mRotateForward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        mRotateBackward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        mImageCollectionModel = new ImageCollectionModel();
        registerControllers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mRecyclerView = (RecyclerView) findViewById(R.id.image_list);
        final int numColumns = getResources().getInteger(R.integer.num_columns);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mImageAdapter = new ImageAdapter(this, mImageCollectionModel, mCurrRateFilter);
        mRecyclerView.setAdapter(mImageAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_delete_all:
                int ret = mImageCollectionModel.deleteAll();
                if (ret > 0){
                    Toast.makeText(getBaseContext(), "Images deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "No images to delete", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.action_clear_filter:
                mRatingBar.setRating(0F);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String uri) {

        if (!validateWebImageURL(uri)){
            Toast.makeText(getBaseContext(), "Invalid Web Image URL", Toast.LENGTH_SHORT).show();
        } else if (mImageCollectionModel.findIfFileExists(uri)){
            Toast.makeText(getBaseContext(), "Duplicated URL", Toast.LENGTH_SHORT).show();
        } else {
            ImageModel image = new ImageModel(uri);
            mImageCollectionModel.addToImageList(image);
        }
    }

    // For the Orientation Change
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // save the model
        outState.putParcelable("KEY", mImageCollectionModel);
        outState.putInt("FILTER", mCurrRateFilter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

//        super.onRestoreInstanceState(savedInstanceState);
        // restore the model
        mImageCollectionModel = savedInstanceState.getParcelable("KEY");
        mCurrRateFilter = savedInstanceState.getInt("FILTER");
    }

    private void registerControllers(){
        FabController fabController = new FabController();
        mFabAdd.setOnClickListener(fabController);
        mFabLoad.setOnClickListener(fabController);
        mFabLink.setOnClickListener(fabController);
        mFabSearch.setOnClickListener(fabController);
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                mCurrRateFilter = (int) rating;
                int size = mImageAdapter.filter(mCurrRateFilter);
                mRecyclerView.scrollToPosition(size - 1);
            }
        });
    }

    private boolean validateWebImageURL(String uri){
        String lowercaseUri = uri.toLowerCase();
        return (URLUtil.isHttpUrl(uri) || URLUtil.isHttpsUrl(uri)) &&
                (lowercaseUri.endsWith(".jpg") || lowercaseUri.endsWith(".jpeg") ||
                        lowercaseUri.endsWith(".png") || lowercaseUri.endsWith(".gif") ||
                        lowercaseUri.endsWith(".bmp") || lowercaseUri.endsWith(".webp"));
    }

    private void animateFab(){

        if (mIsFabOpen){
            animateFabClose();
        } else {
            animateFabOpen();
        }
    }

    private void animateFabClose(){

        mFabAdd.startAnimation(mRotateBackward);
        mFabLoad.startAnimation(mFabClose);
        mFabLink.startAnimation(mFabClose);
        mFabSearch.startAnimation(mFabClose);
        mFabLoad.setClickable(false);
        mFabLink.setClickable(false);
        mFabSearch.setClickable(false);
        mIsFabOpen = false;
    }

    private void animateFabOpen(){

        mFabAdd.startAnimation(mRotateForward);
        mFabLoad.startAnimation(mFabOpen);
        mFabLink.startAnimation(mFabOpen);
        mFabSearch.startAnimation(mFabOpen);
        mFabLoad.setClickable(true);
        mFabLink.setClickable(true);
        mFabSearch.setClickable(true);
        mIsFabOpen = true;
    }

    private void loadFromAssets(){
        try {
            AssetManager assetManager = getAssets();
            String[] files  = assetManager.list("my_images");
            // For every image in the asset
            int count = 0;
            for (int i=0; i<files.length; ++i){
                String file = "my_images/" + files[i];
                if (!mImageCollectionModel.findIfFileExists(file)){
                    // create new image with its bitmap
                    ImageModel imageModel = new ImageModel(file);
                    // then add it to the image collection model
                    mImageCollectionModel.addToImageList(imageModel);
                    count++;
                }
            }
            if (count > 0){
                Toast.makeText(getBaseContext(), "Images loaded", Toast.LENGTH_SHORT).show();
                mRecyclerView.scrollToPosition(mImageAdapter.getItemCount() - 1);
            } else {
                Toast.makeText(getBaseContext(), "Images are already loaded", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showUriDialog() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        EnterUriDialog enterUriDialog = EnterUriDialog.newInstance("Add Image from Web URI");
        enterUriDialog.show(fm, "fragment_enter_uri");
    }

    private class FabController implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id){
                case R.id.fab_add:
                    animateFab();
                    break;
                case R.id.fab_load:
                    animateFabClose();
                    loadFromAssets();
                    break;
                case R.id.fab_link:
                    animateFabClose();
                    showUriDialog();
                    break;
                case R.id.fab_search:
                    animateFabClose();
                    Toast.makeText(getBaseContext(), "Google Image Search not ready yet", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
