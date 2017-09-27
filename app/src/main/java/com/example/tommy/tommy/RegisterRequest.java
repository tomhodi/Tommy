package com.example.tommy.tommy;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Sends a register request to the SQL database.
 * Passes the received info back to the register activity.
 */
class RegisterRequest extends StringRequest {

    private static final String REGISTER_REQUEST_URL = "http://tomhodi.000webhostapp.com/register.php";

    private Map<String, String> params;

    RegisterRequest(String username, String password, String name, String date_of_birth, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("name", name);
        params.put("date_of_birth", date_of_birth);
        params.put("kosher", "0");
        params.put("gluten_free", "0");
        params.put("vegetarian", "0");
    }

    /**
     * Returns the parameters to be sent to the php.
     */
    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
