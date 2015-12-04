package com.android.volleyplus.toolbox;

import android.graphics.Bitmap;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;

import java.io.ByteArrayOutputStream;

/**
 * Created by isapoetra on 12/5/15.
 */
public class HttpHeaderParser extends com.android.volley.toolbox.HttpHeaderParser  {
    /**
     * Extracts a {@link Cache.Entry} from a {@link NetworkResponse}.
     * Cache-control headers are ignored. SoftTtl == 3 mins, ttl == 24 hours.
     * @param bitmap The network response to parse headers from
     * @return a cache entry for the given response, or null if the response is not cacheable.
     */
    public static Cache.Entry parseBitmapCacheHeaders(Bitmap bitmap) {
        NetworkResponse response = null;
        if(null != bitmap){
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            byte[] byteArray = stream.toByteArray();
            response = new NetworkResponse(byteArray);
        }
        return parseCacheHeaders(response);
    }

}
