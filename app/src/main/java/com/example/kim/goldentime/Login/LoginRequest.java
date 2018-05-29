package com.example.kim.goldentime.Login;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kim on 2017-03-19.
 */

public class LoginRequest extends StringRequest {
    final static private String URL ="http://ehdtjs3694.cafe24.com/UsersLogin.php";
    private Map<String, String> parameters;

    //생성자 부분
    public LoginRequest(String userID, String userPassword, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
