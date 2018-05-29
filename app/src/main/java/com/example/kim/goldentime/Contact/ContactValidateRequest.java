package com.example.kim.goldentime.Contact;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kim on 2017-03-17.
 * 회원 아이디가 중복인지 데이터베이스에서 확인 하는 부분
 */

//회원 아이디를 체크하는 클래스
public class ContactValidateRequest extends StringRequest {
    //회원가입이 가능한지 요청하는 것
    final static private String URL ="http://ehdtjs3694.cafe24.com/ContactValidate.php";
    private Map<String, String> parameters;

    //생성자 부분
    public ContactValidateRequest(String userID, String userTel, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("userTel", userTel);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}