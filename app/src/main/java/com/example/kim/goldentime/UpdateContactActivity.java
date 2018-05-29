package com.example.kim.goldentime;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kim.goldentime.Contact.ContactUpdateRequest;
import com.example.kim.goldentime.Contact.ContactValidateRequest;

import org.json.JSONObject;



/**
 * 연락처를 수정하는 Activity
 */
public class UpdateContactActivity extends AppCompatActivity {
    private String userNameinfo;
    private String userTelinfo;
    private AlertDialog dialog;
    private String userID = BlueToothActivity.userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_contact);

        final EditText updatenameText = (EditText) findViewById(R.id.updatenameText);
        final EditText updatetelText = (EditText) findViewById(R.id.updatetelText);
        final Button updateButton = (Button) findViewById(R.id.updateButton);


        userNameinfo = getIntent().getStringExtra("userName"); //예전에 입력했던 전화번호 , 이름 을 가져온다
        userTelinfo = getIntent().getStringExtra("userTel");

        updatenameText.setText(userNameinfo); // 예전에 입력한 것을 미리보고 해준다.
        updatetelText.setText(userTelinfo);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 추가하기 버튼 클릭시 이벤트 발생
                final String userName = updatenameText.getText().toString();
                final String userTel = updatetelText.getText().toString();

                if(userTel.length() > 12) {//전화번호 길이 제한
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateContactActivity.this);
                    AlertDialog dialog = builder.setMessage("전화번호를 정확히 입력해 주세요")
                            .setNegativeButton("확인", null) //확인 버튼을 만드는 것
                            .create();
                    dialog.show();
                    return;
                }

                if(userName.equals("") || userTel.equals("")) { // 빈칸이 있을 시
                    AlertDialog.Builder builder = new AlertDialog.Builder(UpdateContactActivity.this);
                    AlertDialog dialog = builder.setMessage("빈 칸 없이 입력 해주세요.")
                            .setNegativeButton("확인", null) //확인 버튼을 만드는 것
                            .create();
                    dialog.show();
                    return;
                }

                Response.Listener<String> responseListener0 = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (!success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateContactActivity.this);
                                dialog = builder.setMessage("이미 추가한 전화번호 입니다.")
                                        .setNegativeButton("다시시도", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        })
                                        .create();
                                dialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                ContactValidateRequest contactValidateRequest = new ContactValidateRequest(userID, userTel, responseListener0);
                RequestQueue queue0 = Volley.newRequestQueue(UpdateContactActivity.this);
                queue0.add(contactValidateRequest);

                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateContactActivity.this);
                dialog = builder.setMessage("정말로 수정 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");
                                            if (success) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateContactActivity.this);
                                                AlertDialog dialog = builder.setMessage("전화번호 수정이 완료 되었습니다.")
                                                        .setPositiveButton("확인", null) //확인 버튼을 만드는 것
                                                        .create();
                                                dialog.show();

                                                finish(); //주소록 수정 화면을 닫는다


                                            } else { //회원등록에 실패할 경우
                                                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateContactActivity.this);
                                                AlertDialog dialog = builder.setMessage("전화번호 수정에 실패 하였습니다.")
                                                        .setNegativeButton("확인", null) //확인 버튼을 만드는 것
                                                        .create();
                                                dialog.show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                //후에 RegisterRequest가 실행된다.
                                //정상적으로 회원등록 완료
                                ContactUpdateRequest contactUpdateRequest = new ContactUpdateRequest(userID, userNameinfo, userTelinfo, userName, userTel, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(UpdateContactActivity.this); //보내는 부분
                                queue.add(contactUpdateRequest);
                            }
                        })
                        .setNegativeButton("취소", null)
                        .create();
                dialog.show();
            }
        });
    }

    //전화번호 추가 화면이 꺼질 때.
    @Override
    protected  void onStop() {
        super.onStop();

        if(dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

}