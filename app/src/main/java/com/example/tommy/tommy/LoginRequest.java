package com.example.tommy.tommy;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends a login request to the SQL database.
 * Passes the received info back to the login activity.
 */

public class LoginRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "http://tomhodi.000webhostapp.com/login.php";

    private Map<String, String> params;

    LoginRequest(String username, String password, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
    }

    /**
     * Returns the parameters to be sent to the php.
     */
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
