package com.example.kim.goldentime.Contact;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kim on 2017-03-24.
 */

public class ContactDeleteRequest extends StringRequest {
    //ContactAddRequest클래스를 이용해서 해당 php파일에 아이디 패스워드 등 정보를 보내는 것을 요청
    final static private String URL ="http://ehdtjs3694.cafe24.com/DeleteContact.php";
    private Map<String, String> parameters;

    //생성자 부분 구축
    public ContactDeleteRequest(String userID, String userName, String userTel, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null); //해당 URL에 parameter들을 POST방식으로 해당요철을 숨겨서 보내는 방법
        parameters = new HashMap<>();//각각의 parameter들을 HashMap형태로 넣는다.

        parameters.put("userID", userID);
        parameters.put("userName", userName); //parameter 매칭 부분
        parameters.put("userTel", userTel);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
