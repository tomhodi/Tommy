package com.example.tommy.tommy;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tomho on 12/09/2017.
 */

public class updateRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://tomhodi.000webhostapp.com/update.php";
    private Map<String, String> params;

    updateRequest(String username, String password, String kosher, String gluten_free, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("kosher", kosher);
        params.put("gluten_free", gluten_free);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

