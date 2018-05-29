package com.example.kim.goldentime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kim.goldentime.Login.LoginRequest;

import org.json.JSONObject;

/**
 * 사용자는 로그인을 해서 접속 한다.
 */
public class LoginActivity extends AppCompatActivity {
    private AlertDialog dialog;

    //권한 체크를 위한 값 선언
    private static final int REQUEST_CODE = 0;
    /*요청받은 권한을 설정합니다. 여기선 저장소와 카메라를 설정
    * android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    * android.Manifest.permission.CAMERA
    * */
    private static final String[] PERMISSIONS =
            new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.SEND_SMS,
                    android.Manifest.permission.RECEIVE_SMS
            };
    //PermissionChecker를 사용하기 위해 선언
    private PermissionsChecker checker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Oncreate 에서 Checker를 호출
        checker = new PermissionsChecker(this);


        final TextView registerButton = (TextView) findViewById(R.id.registerButton); //회원가입 버튼 초기화

        registerButton.setOnClickListener(new View.OnClickListener() { //회원가입 버튼 누를 시 엑티비티 이동

            @Override
            public void onClick(View v) { // 회원가입 버튼 클릭 시 회원가입 activity이동
                Intent registerIntend = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntend);
            }
        });

        //activity_login에 있는 변수들 초기화
        final EditText idText = (EditText) findViewById(R.id.idText);
        final EditText passwordText = (EditText) findViewById(R.id.passwordText);
        final Button loginButton = (Button) findViewById(R.id.loginButton);


        //로그인 버튼을 눌렸을 때 이벤트 발생
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userID = idText.getText().toString();
                String userPassword = passwordText.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if (success) { //로그인 성공 시
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("로그인에 성공했습니다.")
                                        .setPositiveButton("확인", null) //확인 버튼을 만드는 것
                                        .create();
                                dialog.show();
                                Intent intent = new Intent(LoginActivity.this, BlueToothActivity.class);
                                intent.putExtra("userID", userID); // userID에 대한 정보를 보낸다
                                //화면 전환
                                LoginActivity.this.startActivity(intent);
                                finish();
                            } else { //로그인 실패 시
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                dialog = builder.setMessage("게정을 다시 확인하세요.")
                                        .setNegativeButton("다시 시도", null) //확인 버튼을 만드는 것
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                //실제로 로그인을 보낸다.

                LoginRequest loginRequest = new LoginRequest(userID, userPassword, responseListener);
                //큐에 담는다
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);

                //정상 적으로 요청이 들어가고 그결과로 제이슨 리스폰스로 들어가서 나온다
            }
        });


        TextView information = (TextView) findViewById(R.id.information);
        information.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, Pop.class));
            }
        });

    }

    private long lastTimeBackPressed;
    //두번 뒤로가기 버튼 눌리면 종료되게 설정
    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 1500) { //뒤로가기 버튼 누른 후 1.2초 뒤 한번 더 누르면 종료
            finish();
            return;
        }
        Toast toast = Toast.makeText(this, "'뒤로' 버튼을 한번 더 눌러 종료합니다.", Toast.LENGTH_SHORT);
        toast.show();
        lastTimeBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (checker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == PermissionsActivity.PERMISSIONS_DENIED) {
            finish();
        }
    }

}