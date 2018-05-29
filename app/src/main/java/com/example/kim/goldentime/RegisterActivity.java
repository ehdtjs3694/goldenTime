package com.example.kim.goldentime;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kim.goldentime.Login.RegisterRequest;
import com.example.kim.goldentime.Login.ValidateRequest;

import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {

    private ArrayAdapter adapter;
    private Spinner spinner;

    private String userID;
    private String userPassword;
    private String userGender;
    private String userEmail;
    private String userTel;

    private AlertDialog dialog; // 메세지 출력 변수
    private boolean validate = false; //사용할 수 있는 아이디 인지

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final EditText emailText = (EditText) findViewById(R.id.emailText);
        final EditText telText = (EditText) findViewById(R.id.telText);

        RadioGroup genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
        int genderGroupID = genderGroup.getCheckedRadioButtonId();
        //성별 선택 시 남자 인지 여자인지 산택한 값이 들어간다.
        userGender = ((RadioButton) findViewById(genderGroupID)).getText().toString();

        //라디오 버튼을 클릭 했을 시 이벤트 발생
        genderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                RadioButton genderButton = (RadioButton) findViewById(i); //현재 선택되어 있는 라디오 버튼을 찾는다
                userGender = genderButton.getText().toString(); // 찾는 것을 바꿔준다.
            }
        });

        //회원 중복체크 하는 부분
        final Button validateButton = (Button) findViewById(R.id.validateButton);

        //중복체크를 클릭 했을 시 이벤트 처리
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = idText.getText().toString(); //아이디 값을 얻어오고

                if (validate) { //체크가 되어 있는 상태라면
                    return;
                }
                if (userID.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("아이디는 빈 칸일 수 없습니다.")
                            .setPositiveButton("확인", null) //확인 버튼을 만드는 것
                            .create();
                    dialog.show();
                    return;
                }
                //정상 적인 부분
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) { //접속을 하고 나서 응답을 받는다
                        try {
                            JSONObject jsonResponse = new JSONObject(response); //응답을 받을 수 있는 부분
                            boolean success = jsonResponse.getBoolean("success");

                            if (success) { // 사용할 수 있는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 아이디입니다.")
                                        .setPositiveButton("확인", null) //확인 버튼을 만드는 것
                                        .create();
                                dialog.show();

                                idText.setEnabled(false); //아이디값을 수정 할 수 없도록 고정
                                validate = true; // 체크가 완료 되었다는 것

                                //색상 바꾸기
                                idText.setBackgroundColor(getResources().getColor(R.color.colorGray));
                                validateButton.setBackgroundColor(getResources().getColor(R.color.colorGray));
                            } else { //사용 할 수 없는 아이디라면
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                dialog = builder.setMessage("사용할 수 없는 아이디입니다.")
                                        .setNegativeButton("확인", null) //확인 버튼을 만드는 것
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(validateRequest);
            }
        });

        //회원가입 버튼 만들기
        // 버튼을 누르면 입력한 값을 가져온다
        Button registerButton = (Button) findViewById(R.id.registerButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();
                String userTel = telText.getText().toString();
                String userEmail = emailText.getText().toString();

                if(!validate) {//만약 현재 중복체크가 안되어 있다면
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("먼저 중복 체크를 해주세요")
                            .setNegativeButton("확인", null) //확인 버튼을 만드는 것
                            .create();
                    dialog.show();
                    return;
                }

                //만약 어떤 값이라도 빈공간이 있다면
                if(userID.equals("") || userPassword.equals("") || userTel.equals("") || userEmail.equals("") || userGender.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    dialog = builder.setMessage("빈 칸 없이 입력 해주세요.")
                            .setNegativeButton("확인", null) //확인 버튼을 만드는 것
                            .create();
                    dialog.show();
                    return;
                }

                //아무런 문제가 없을 시 회원가입이 정상적으로 이루어 지는 곳
                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success) {
                                  AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    dialog = builder.setMessage("회원등록에 성공했습니다.")
                                            .setPositiveButton("확인",null) //확인 버튼을 만드는 것
                                           .create();
                                    dialog.show();


                                    finish(); //회원가입 등록창을 닫는다.

                                } else { //회원등록에 실패할 경우
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    dialog = builder.setMessage("회원등록에 실패했습니다.")
                                            .setNegativeButton("확인",null) //확인 버튼을 만드는 것
                                           .create();
                                    dialog.show();
                                }
                                adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                //후에 RegisterRequest가 실행된다.
                //정상적으로 회원등록 완료
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userEmail, userTel, userGender, "30", "120", "5", responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }

    //완료 후 회원등록 창이 꺼질 때
    @Override
    protected  void onStop() {
        super.onStop();

        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
