package com.android.volleyplus.cache;

import com.android.volley.Cache;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by isapoetra on 12/5/15.
 */
public interface IExtendedCache extends Cache {
    void flush();
    void close();
}
