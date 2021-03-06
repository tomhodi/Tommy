package com.example.tommy.tommy;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends an extract user info request to the SQL database.
 * Passes the received info back to the my profile activity.
 */
public class ExtractRequest extends StringRequest {

    private static final String EXTRACT_REQUEST_URL = "http://tomhodi.000webhostapp.com/extract.php";

    private Map<String, String> params;

    ExtractRequest(String username, Response.Listener<String> listener) {
        super(Method.POST, EXTRACT_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
    }

    /**
     * Returns the parameters to be sent to the php.
     */
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
