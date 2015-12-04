/**
 * Copyright (C) 2013 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.volleyplus.toolbox;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Looper;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

/**
 * Helper that handles loading and caching images from remote URLs.
 *
 * The simple way to use this class is to call {@link ImageLoader#get(String, ImageListener)}
 * and to pass in the default image listener provided by
 * {@link ImageLoader#getImageListener(ImageView, int, int)}. Note that all function calls to
 * this class must be made from the main thead, and all responses will be delivered to the main
 * thread as well.
 */
public class ImageLoader extends com.android.volley.toolbox.ImageLoader {

    private final Resources mResources;
    private final ImageCache mCache;
    private RequestQueue mRequestQueue;

    /**
     * Constructs a new ImageLoader.
     *
     * @param queue      The RequestQueue to use for making image requests.
     * @param imageCache The cache to use as an L1 cache.
     */
    public ImageLoader(RequestQueue queue, ImageCache imageCache) {
        this(queue, imageCache, null);
    }

    public ImageLoader(RequestQueue queue, ImageCache imageCache, Resources resources) {
        super(queue, imageCache);
        mRequestQueue = queue;
        mCache = imageCache;
        this.mResources = resources;
    }

    protected RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    protected ImageCache getImageCache() {
        return mCache;
    }

    protected Cache getCache() {
        return mRequestQueue.getCache();
    }

    public Resources getResources() {
        return mResources;
    }

    /**
     * Creates a cache key for use with the L1 cache.
     * @param url The URL of the request.
     * @param maxWidth The max-width of the output.
     * @param maxHeight The max-height of the output.
     */
    public static String getCacheKey(String url, int maxWidth, int maxHeight) {
        return "#W" + maxWidth +
                "#H" + maxHeight + url;
    }
    /**
     * Creates a cache key for use with the L1 cache.
     * @param url The URL of the request.
     * @param maxWidth The max-width of the output.
     * @param maxHeight The max-height of the output.
     * @param scaleType The scaleType of the imageView.
     */
    public static String getCacheKey(String url, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
        if(scaleType == ImageView.ScaleType.CENTER_INSIDE){
            return getCacheKey(url, maxWidth, maxHeight);
        }
        return "#W" + maxWidth +
                "#H" + maxHeight + "#S" + scaleType.ordinal() + url;
    }
    /**
     * Simple cache adapter interface. If provided to the ImageLoader, it
     * will be used as an L1 cache before dispatch to Volley. Implementations
     * must not block. Implementation with an LruCache is recommended.
     */
    public interface ImageCache extends com.android.volley.toolbox.ImageLoader.ImageCache{
        Bitmap getBitmap(String url);
        void putBitmap(String url, Bitmap bitmap);
        void invalidateBitmap(String url);
        void clear();
    }

    /**
     * Issues a bitmap request with the given URL if that image is not available
     * in the cache, and returns a bitmap container that contains all of the data
     * relating to the request (as well as the default image if the requested
     * image is not available).
     * @param requestUrl The url of the remote image
     * @param imageListener The listener to call when the remote image is loaded
     * @param maxWidth The maximum width of the returned image.
     * @param maxHeight The maximum height of the returned image.
     * @param scaleType The ImageViews ScaleType used to calculate the needed image size.
     * @return A container object that contains all of the properties of the request, as well as
     *     the currently available image (default if remote is not loaded).
     */
    public ImageContainer set(String requestUrl, ImageListener imageListener,
                              int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap bitmap) {
        // only fulfill requests that were initiated from the main thread.
        throwIfNotOnMainThread();

        final String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight, scaleType);

        // The bitmap did not exist in the cache, fetch it!
        ImageContainer imageContainer =
                new ImageContainer(bitmap, requestUrl, cacheKey, imageListener);

        // Update the caller to let them know that they should use the default bitmap.
        imageListener.onResponse(imageContainer, true);
        //setImageSuccess(cacheKey, bitmap);

        // cache the image that was fetched.
        mCache.putBitmap(cacheKey, bitmap);

        Response<?> response = Response.success(bitmap, HttpHeaderParser.parseBitmapCacheHeaders(bitmap));
        getCache().put(requestUrl, response.cacheEntry);

/*        Response<?> response = Response.success(bitmap, HttpHeaderParser.parseBitmapCacheHeaders(bitmap));
        Entry cache = getCache().get(requestUrl);
        cache.data = response.cacheEntry.data;
        getCache().put(requestUrl, cache);*/

        return imageContainer;
    }
    private void throwIfNotOnMainThread() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalStateException("ImageLoader must be invoked from the main thread.");
        }
    }

}
