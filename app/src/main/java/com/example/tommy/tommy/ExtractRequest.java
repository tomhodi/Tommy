package com.example.tommy.tommy;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomho on 19/09/2017.
 */

public class ExtractRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://tomhodi.000webhostapp.com/extract.php";
    private Map<String, String> params;

    ExtractRequest(String username, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
