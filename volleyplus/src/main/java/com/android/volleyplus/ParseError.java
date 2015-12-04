package com.android.volleyplus;

import com.android.volley.NetworkResponse;

/**
 * Created by isapoetra on 12/5/15.
 */
public class ParseError extends com.android.volley.ParseError {
    public ParseError() { }

    public ParseError(NetworkResponse networkResponse) {
        super(networkResponse);
    }

    public ParseError(Throwable cause) {
        super(cause);
    }
    public ParseError(String message) {
        super(new Throwable(message));
    }
}
