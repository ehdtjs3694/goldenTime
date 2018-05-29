package com.example.kim.goldentime.Contact;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import com.example.kim.goldentime.BlueToothActivity;
import com.example.kim.goldentime.Messenger;
import com.example.kim.goldentime.R;
import com.example.kim.goldentime.UpdateContactActivity;

import org.json.JSONObject;

import java.util.List;


/**
 * Created by kim on 2017-03-23.
 */

public class ContactListAdapter extends BaseAdapter {
    private Context context;
    private List<Contact> contactList;
    private Fragment parent;

    private String userID = BlueToothActivity.userID;

    public ContactListAdapter(Context context, List<Contact> contactList, Fragment parent) {
        this.context = context;
        this.contactList = contactList;
        this.parent = parent;
    }

    @Override
    public int getCount() {
        return contactList.size();
    }

    @Override
    public Object getItem(int i) {
        return contactList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, final ViewGroup parent) {
        View v = View.inflate(context, R.layout.contact, null);

        final TextView nameText = (TextView) v.findViewById(R.id.nameText);
        final TextView telText = (TextView) v.findViewById(R.id.telText);

        nameText.setText(contactList.get(i).getName());
        telText.setText(contactList.get(i).getTel());

        v.setTag(contactList.get(i).getName());

        //삭제 버튼
        Button contactdeleteButton = (Button) v.findViewById(R.id.contactdeleteButton);
        //삭제 버튼 누를 시 이벤트 발생
        contactdeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                                AlertDialog dialog = builder.setMessage("연락처를 삭제 했습니다.")
                                        .setPositiveButton("확인",null) //확인 버튼을 만드는 것
                                        .create();
                                dialog.show();

                                contactList.remove(i);
                                notifyDataSetChanged(); //저장 유지

                            } else { //회원등록에 실패할 경우
                                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                                AlertDialog dialog = builder.setMessage("연락처 삭제를 실패 하였습니다.")
                                        .setNegativeButton("확인",null) //확인 버튼을 만드는 것
                                        .create();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                //후에 RegisterRequest가 실행된다.
                //정상적으로 회원등록 완료
                ContactDeleteRequest contactDeleteRequest = new ContactDeleteRequest(userID, contactList.get(i).getName(), contactList.get(i).getTel(),responseListener);
                RequestQueue queue = Volley.newRequestQueue(parent.getContext()); //보내는 부분
                queue.add(contactDeleteRequest);

            }
        });
        //수정 버튼
        Button contactupdateButton = (Button) v.findViewById(R.id.contactupdateButton);

        //수정 버튼 클릭 시 이벤트 UpdateContactActivity 이동 거기서 이벤트를 발생 시켜야됨
        contactupdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), UpdateContactActivity.class);
                intent.putExtra("userName", contactList.get(i).getName());
                intent.putExtra("userTel", contactList.get(i).getTel());

                parent.getContext().startActivity(intent);
            }
        });

        return v;
    }
}
