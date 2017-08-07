package com.example.tommy.tommy;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tom on 8/6/2017.
 */

class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "http://tomhodi.000webhostapp.com/Register.php";
    private Map<String, String> params;
    RegisterRequest(String username, String password, String name, String dateOfBirth, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("name", name);
        params.put("date_of_birth", dateOfBirth);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
