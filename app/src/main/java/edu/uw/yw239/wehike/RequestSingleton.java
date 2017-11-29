package edu.uw.yw239.wehike;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by wangchen on 11/27/17.
 */

public class RequestSingleton {
    private static RequestSingleton instance;
    private RequestQueue requestQueue = null;
    private ImageLoader imageLoader;



    private RequestSingleton(Context ctx){
        this.requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());

        this.imageLoader = new ImageLoader(this.requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap> cache = new LruCache<>(20);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url,bitmap);
                    }
                });

    }


    public static RequestSingleton getInstance(Context ctx){
        if (instance == null){
            instance = new RequestSingleton((ctx));
        }
        return instance;
    }

    public RequestQueue getRequestQueue(){
        return this.requestQueue;
    }

    public <T> void add(Request<T> req){
        requestQueue.add(req);
    }

    public ImageLoader getImageLoader(){
        return this.imageLoader;
    }
}
