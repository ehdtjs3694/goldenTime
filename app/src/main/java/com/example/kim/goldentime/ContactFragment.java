package com.example.kim.goldentime;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.kim.goldentime.Contact.Contact;
import com.example.kim.goldentime.Contact.ContactListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ListView contactListView;
    private ContactListAdapter adapter;
    public static List<Contact> contactList;

    private String userID = BlueToothActivity.userID;

    public void onActivityCreated(Bundle b) {
        super.onActivityCreated(b);

        final Button contactaddButton = (Button) getView().findViewById(R.id.contactaddButton);

        final LinearLayout contact = (LinearLayout) getView().findViewById(R.id.contact);
        contactListView = (ListView) getView().findViewById(R.id.contactListView);
        contactList = new ArrayList<Contact>();
        //contactList.add(new Contact("한성화", "01066063741"));
        adapter = new ContactListAdapter(getContext().getApplicationContext(), contactList, this);
        contactListView.setAdapter(adapter); //리스트뷰에 해당 어뎁터가 매칭된다. 어뎁터에 들어있는 내용 각각이 뷰형태로 들어간다
        //정상적으로 데이터베이스 실행
        new BackgroundTask().execute();

        contactaddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContactActivity.class);
                getActivity().startActivity(intent);
            }
        });

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    //주소록 DB에서 데이터를 가져와서 리스트뷰에 뿌려주는 부분
    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        String target; //접속할 홈페이지 주소

        @Override
        protected void onPreExecute() {
            target = "http://ehdtjs3694.cafe24.com/ContactList.php?userID=" + BlueToothActivity.userID;
        }

        @Override
        protected String doInBackground(Void... params) { //실질적으로 데이터를 얻어오는 부분

            try {
                URL url = new URL(target); //해당 서버에 접속
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();//넘어 오는 결과값을 저장 할 수 있다.
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream)); //넘어온 결과값을 버퍼에 넣어서 읽을 수 있게 해준다
                String temp; // 하나씩 읽고 문자열 형태로 읽기 위해서 사용
                StringBuilder stringBuilder = new StringBuilder();

                while((temp = bufferedReader.readLine()) != null) { //버퍼에서 가져온 것을 하나씩 읽는다.
                    stringBuilder.append(temp + "\n"); //한줄 씩 추가 한다.
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim(); //문자열 반환

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        //해당 결과를 처리
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result); //해당 결과(응답)부분 처리
                JSONArray jsonArray = jsonObject.getJSONArray("response"); //response에 각각의주소록이 들어간다.

                int count = 0;
                String userName, userTel; //주소록에 필요한 변수

                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count); //현재 배열의 원소값 저장
                    //해당 값을 들 가져온다.
                    userName = object.getString("userName");
                    userTel = object.getString("userTel");
                    //하나의 주소록 객체를 생성한다.
                    Contact contact = new Contact(userName, userTel);
                    contactList.add(contact);//리스트에 추가한다.
                    count++;
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
