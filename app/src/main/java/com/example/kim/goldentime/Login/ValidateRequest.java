package com.example.kim.goldentime.Login;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kim on 2017-03-17.
 */

//회원 아이디를 체크하는 클래스
public class ValidateRequest extends StringRequest {
    //회원가입이 가능한지 요청하는 것
    final static private String URL ="http://ehdtjs3694.cafe24.com/UsersValidate.php";
    private Map<String, String> parameters;

    //생성자 부분
    public ValidateRequest(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
