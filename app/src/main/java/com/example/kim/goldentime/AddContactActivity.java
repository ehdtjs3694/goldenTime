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
import com.example.kim.goldentime.Contact.Contact;
import com.example.kim.goldentime.Contact.ContactAddRequest;
import com.example.kim.goldentime.Contact.ContactValidateRequest;

import org.json.JSONObject;


/**
 * 사용자의 연락처를 추가 하는 부분
 */
public class AddContactActivity extends AppCompatActivity {
    private String userName;
    private String userTel;
    private AlertDialog dialog;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addcontact);

        final EditText nameText = (EditText) findViewById(R.id.nameText);
        final EditText telText = (EditText) findViewById(R.id.telText);
        final Button addButton = (Button) findViewById(R.id.addButton);

        userID = BlueToothActivity.userID; // 로그인 했던 아이디의 값을 받아온다.


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 추가하기 버튼 클릭시 이벤트 발생
                final String userName = nameText.getText().toString();
                final String userTel = telText.getText().toString();

                if(userTel.length() > 12) {//전화번호 길이 제한
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                    AlertDialog dialog = builder.setMessage("전화번호를 정확히 입력해 주세요")
                            .setNegativeButton("확인", null) //확인 버튼을 만드는 것
                            .create();
                    dialog.show();
                    return;
                }

                if(userName.equals("") || userTel.equals("")) { // 빈칸이 있을 시
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
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
                RequestQueue queue0 = Volley.newRequestQueue(AddContactActivity.this);
                queue0.add(contactValidateRequest);

                AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                dialog = builder.setMessage("정말로 추가 하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Response.Listener<String> responseListener = new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonResponse = new JSONObject(response);
                                            boolean success = jsonResponse.getBoolean("success");
                                            if(success) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                                                AlertDialog dialog = builder.setMessage("전화번호 등록이 완료 되었습니다.")
                                                        .setPositiveButton("확인",null) //확인 버튼을 만드는 것
                                                        .create();
                                                dialog.show();

                                                Contact contact = new Contact(userName, userTel);
                                                ContactFragment.contactList.add(contact);

                                                finish();

                                            } else { //회원등록에 실패할 경우
                                                AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                                                AlertDialog dialog = builder.setMessage("전화번호 등록에 실패 하였습니다.")
                                                        .setNegativeButton("확인",null) //확인 버튼을 만드는 것
                                                        .create();
                                                dialog.show();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };
                                //후에 ContactAddRequest가 실행된다.
                                //정상적으로 회원등록 완료
                                ContactAddRequest addRequest = new ContactAddRequest(userID, userName, userTel, responseListener);
                                RequestQueue queue = Volley.newRequestQueue(AddContactActivity.this); //보내는 부분
                                queue.add(addRequest);

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