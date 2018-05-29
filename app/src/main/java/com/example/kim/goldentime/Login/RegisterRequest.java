package com.example.kim.goldentime.Login;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kim on 2017-03-17.
 */

//회원가입을 요청하는 클래스
public class RegisterRequest extends StringRequest {
    //RegisterRequest클래스를 이용해서 해당 php파일에 아이디 패스워드 등 정보를 보내는 것을 요청
    final static private String URL ="http://ehdtjs3694.cafe24.com/UsersRegister.php";
    private Map<String, String> parameters;

    //생성자 부분
    public RegisterRequest(String userID, String userPassword, String userEmail, String userTel, String userGender, String minCnt, String maxCnt, String vibration, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("userPassword", userPassword);
        parameters.put("userEmail", userEmail);
        parameters.put("userTel", userTel);
        parameters.put("userGender", userGender);
        parameters.put("minCnt", minCnt);
        parameters.put("maxCnt", maxCnt);
        parameters.put("vibration", vibration);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
