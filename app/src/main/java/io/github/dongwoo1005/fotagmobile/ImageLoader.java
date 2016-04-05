package io.github.dongwoo1005.fotagmobile;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.github.dongwoo1005.fotagmobile.View.ImageView;

/**
 * Created by Dongwoo on 26/03/2016.
 */
public class ImageLoader extends AsyncTask<String, Void, Bitmap>{

    private static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
    private static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds
    private static final int MAX_REDIRECT_COUNT = 5;
    private static final int DEFAULT_BUFFER_SIZE = 32 * 1024; // 32 KB
    private static final int IMAGE_MAX_SIZE_THUMBNAIL = 688;
    private static final int IMAGE_MAX_SIZE_FULLSIZE = 1980;

    private ImageView mHolder;
    private android.widget.ImageView mImageView;
    private Context mContext;
    private String mFilePath;

    public ImageLoader(ImageView holder, Context context, String filePath) {
        this.mHolder = holder;
        this.mImageView = null;
        this.mContext = context;
        this.mFilePath = filePath;
    }
    public ImageLoader(android.widget.ImageView imageView, Context context, String filePath) {
        this.mHolder = null;
        this.mImageView = imageView;
        this.mContext = context;
        this.mFilePath = filePath;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String param = params[0];
        Bitmap b = param.equals("full") ? decodeFile(mFilePath, IMAGE_MAX_SIZE_FULLSIZE) :
                decodeFile(mFilePath, IMAGE_MAX_SIZE_THUMBNAIL);
        return b;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (mHolder == null){
            mImageView.setImageBitmap(bitmap);
        } else {
            mHolder.thumbnail.setImageBitmap(bitmap);
        }
    }

    private HttpURLConnection createConnection(String url) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        conn.setConnectTimeout(DEFAULT_HTTP_CONNECT_TIMEOUT);
        conn.setReadTimeout(DEFAULT_HTTP_READ_TIMEOUT);
        return conn;
    }

    private void readAndCloseStream(InputStream is) {
        final byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
        try {
            while (is.read(bytes, 0, DEFAULT_BUFFER_SIZE) != -1);
        } catch (IOException ignored) {
        } finally {
            closeSilently(is);
        }
    }

    private void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    private boolean shouldBeProcessed(HttpURLConnection conn) throws IOException {
        return conn.getResponseCode() == 200;
    }

    private InputStream downloadImage(String url) throws IOException{
        HttpURLConnection conn = createConnection(url);
        int redirectCount = 0;
        while (conn.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT){
            conn = createConnection(conn.getHeaderField("Location"));
            redirectCount++;
        }
        InputStream istream;
        try {
            istream = conn.getInputStream();
        } catch (IOException e) {
            readAndCloseStream(conn.getErrorStream());
            throw e;
        }
        if (!shouldBeProcessed(conn)) {
            closeSilently(istream);
            throw new IOException("Image request failed with response code " + conn.getResponseCode());
        }

        return istream;
    }

    private Bitmap decodeFile(String filePath, int maxSize){

        Bitmap b = null;
        InputStream istream = null;

        try{
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            if (filePath.startsWith("my_images/")){
                istream =  mContext.getAssets().open(filePath);
            } else {
                istream = downloadImage(filePath);
            }
            if (istream != null){
                BitmapFactory.decodeStream(istream, null, o);
                istream.close();
            }

            int scale = 1;
            if (o.outHeight > maxSize || o.outWidth > maxSize) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(maxSize /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            if (filePath.startsWith("my_images/")){
                istream = mContext.getAssets().open(filePath);
            }else {
                istream = downloadImage(filePath);
            }
            if (istream != null){
                b = BitmapFactory.decodeStream(istream, null, o2);
                istream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }
}
