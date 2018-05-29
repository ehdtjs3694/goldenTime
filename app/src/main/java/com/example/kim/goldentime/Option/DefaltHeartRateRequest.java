package com.example.kim.goldentime.Option;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kim on 2017-04-02.
 * 사용자의 옵션 값 ( 최대, 최소, 진동수 제한범위를 설정 ) 을 데이터베이스에 저장 한다.
 */
//데이터 베이스로 수치 부분을 저장 하기 위한 클래스
public class DefaltHeartRateRequest extends StringRequest {
    //ContactAddRequest클래스를 이용해서 해당 php파일에 아이디 패스워드 등 정보를 보내는 것을 요청
    final static private String URL ="http://ehdtjs3694.cafe24.com/DefaltRateSave.php";
    private Map<String, String> parameters;

    //생성자 부분 구축
    public DefaltHeartRateRequest(String userID, String minCnt, String maxCnt, String vibration, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null); //해당 URL에 parameter들을 POST방식으로 해당요철을 숨겨서 보내는 방법
        parameters = new HashMap<>();//각각의 parameter들을 HashMap형태로 넣는다.

        parameters.put("userID", userID); //parameter 매칭 부분
        parameters.put("minCnt", minCnt);
        parameters.put("maxCnt", maxCnt);
        parameters.put("vibration", vibration);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}