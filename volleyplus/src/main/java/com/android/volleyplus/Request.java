package com.android.volleyplus;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

import java.util.Collections;
import java.util.Map;

/**
 * Created by isapoetra on 12/5/15.
 */
public abstract class Request<T> extends com.android.volley.Request<T> {
    private Map<String, String> headers= Collections.emptyMap();

    public Request(int method, String url, Response.ErrorListener listener) {
        super(method, url, listener);
    }


    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    /**
     * Returns a list of extra HTTP headers to go along with this request. Can
     * throw {@link AuthFailureError} as authentication may be required to
     * provide these values.
     * @throws AuthFailureError In the event of auth failure
     */
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers;
    }
}
