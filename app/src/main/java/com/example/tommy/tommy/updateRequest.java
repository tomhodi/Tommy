package com.example.tommy.tommy;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends a update request to the SQL database.
 * The possible modifiable parameters are kosher, gluten_free and vegetarian preferences.
 */

public class updateRequest extends StringRequest {

    private static final String UPDATE_REQUEST_URL = "http://tomhodi.000webhostapp.com/update.php";

    private Map<String, String> params;

    updateRequest(String username, String kosher, String gluten_free, String vegetarian, Response.Listener<String> listener) {
        super(Method.POST, UPDATE_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("kosher", kosher);
        params.put("gluten_free", gluten_free);
        params.put("vegetarian", vegetarian);
    }

    /**
     * Returns the parameters to be sent to the php.
     */
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}

