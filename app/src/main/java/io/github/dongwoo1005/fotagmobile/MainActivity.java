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
import android.util.Log;
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

public class MainActivity extends AppCompatActivity implements EnterUriDialog.EnterUriDialogListener{

    ImageCollectionModel imageCollectionModel;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private ImageAdapter imageAdapter;

    private Boolean isFabOpen = false;
    private FloatingActionButton fab_add, fab_load, fab_link, fab_search;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private RatingBar ratingBar;
    private int currRateFilter;

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

        fab_add = (FloatingActionButton) findViewById(R.id.fab_add);
        fab_load = (FloatingActionButton) findViewById(R.id.fab_load);
        fab_link = (FloatingActionButton) findViewById(R.id.fab_link);
        fab_search = (FloatingActionButton) findViewById(R.id.fab_search);


        currRateFilter = 0;
        if (savedInstanceState != null){
            currRateFilter = savedInstanceState.getInt("FILTER");
        }
        ratingBar = (RatingBar) findViewById(R.id.toolbar_ratingBar);
        ratingBar.setRating(currRateFilter);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);

        imageCollectionModel = new ImageCollectionModel();
        registerControllers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        recyclerView = (RecyclerView) findViewById(R.id.image_list);
        final int numColumns = getResources().getInteger(R.integer.num_columns);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(numColumns, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        imageAdapter = new ImageAdapter(this, imageCollectionModel, currRateFilter);
        recyclerView.setAdapter(imageAdapter);
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
                Log.d("Test", "Delete All");
                imageCollectionModel.deleteAll();
                return true;
            case R.id.action_clear_filter:
                Log.d("Test", "Clear Filter");
                ratingBar.setRating(0F);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerControllers(){
        FabController fabController = new FabController();
        fab_add.setOnClickListener(fabController);
        fab_load.setOnClickListener(fabController);
        fab_link.setOnClickListener(fabController);
        fab_search.setOnClickListener(fabController);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                // Get filtered List
                // set adapter to the filtered list
                // scroll position to 0
                currRateFilter = (int) rating;
                int size = imageAdapter.filter(currRateFilter);
                recyclerView.scrollToPosition(size - 1);
            }
        });
    }

    private boolean validateWebImageURL(String uri){
        String lowercaseUri = uri.toLowerCase();
        return (URLUtil.isHttpUrl(uri) || URLUtil.isHttpsUrl(uri)) &&
                (lowercaseUri.endsWith(".jpg") || lowercaseUri.endsWith(".jpeg") ||
                        lowercaseUri.endsWith(".png") || lowercaseUri.endsWith(".gif") ||
                        lowercaseUri.endsWith(".bmp") || lowercaseUri.endsWith(".webp")) &&
                !imageCollectionModel.findIfFileExists(uri);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, String uri) {

        Log.d("Test", "Add from URI Okay");

        if (validateWebImageURL(uri)){
            ImageModel image = new ImageModel(uri);
            imageCollectionModel.addToImageList(image);
        } else {
            Toast.makeText(getBaseContext(), "Invalid Web Image URL", Toast.LENGTH_SHORT).show();
        }
    }

    // For the Orientation Change
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        // save the model
        outState.putParcelable("KEY", imageCollectionModel);
        outState.putInt("FILTER", currRateFilter);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

//        super.onRestoreInstanceState(savedInstanceState);
        // restore the model
        imageCollectionModel = savedInstanceState.getParcelable("KEY");
        currRateFilter = savedInstanceState.getInt("FILTER");
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
                    Log.d("Test", "Fab Load");
                    animateFabClose();
                    loadFromAssets();
                    recyclerView.scrollToPosition(imageAdapter.getItemCount() - 1);
//                    Log.d("Test", "width: " + recyclerView.getWidth());
//                    Log.d("Test", "height: " + recyclerView.getHeight());
                    break;
                case R.id.fab_link:
                    Log.d("Test", "Fab Link");
                    animateFabClose();
                    showUriDialog();
                    break;
                case R.id.fab_search:
                    Log.d("Test", "Fab Search");
                    animateFabClose();
                    break;
            }
        }
    }

    private void animateFab(){

        if (isFabOpen){
            animateFabClose();
        } else {
            animateFabOpen();
        }
    }

    private void animateFabClose(){

        fab_add.startAnimation(rotate_backward);
        fab_load.startAnimation(fab_close);
        fab_link.startAnimation(fab_close);
        fab_search.startAnimation(fab_close);
        fab_load.setClickable(false);
        fab_link.setClickable(false);
        fab_search.setClickable(false);
        isFabOpen = false;
        Log.d("Test", "close ");
    }

    private void animateFabOpen(){

        fab_add.startAnimation(rotate_forward);
        fab_load.startAnimation(fab_open);
        fab_link.startAnimation(fab_open);
        fab_search.startAnimation(fab_open);
        fab_load.setClickable(true);
        fab_link.setClickable(true);
        fab_search.setClickable(true);
        isFabOpen = true;
        Log.d("Test", "open");
    }

    private void loadFromAssets(){
        try {
            AssetManager assetManager = getAssets();
            String[] files  = assetManager.list("my_images");
            Log.d("Test", "Fab Load num images: " + files.length);
            // For every image in the asset
            for (int i=0; i<files.length; ++i){
                String file = "my_images/" + files[i];
                if (!imageCollectionModel.findIfFileExists(file)){
                    Log.d("Test", "Fab Load image file name: " + file);
                    // create new image with its bitmap
                    ImageModel imageModel = new ImageModel(file);
                    // then add it to the image collection model
                    imageCollectionModel.addToImageList(imageModel);
                }
            }
            Log.d("Test", "Fab Done Load");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFromUri(){

    }

    private void showUriDialog() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        EnterUriDialog enterUriDialog = EnterUriDialog.newInstance("Add Image from Web URI");
        enterUriDialog.show(fm, "fragment_enter_uri");
    }
}
