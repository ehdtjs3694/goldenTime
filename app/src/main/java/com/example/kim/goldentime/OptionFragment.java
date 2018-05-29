package com.example.kim.goldentime;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.kim.goldentime.Option.OptionUpdateRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OptionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public OptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OptionFragment newInstance(String param1, String param2, String param3) {
        OptionFragment fragment = new OptionFragment();
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

    /**
     * 옵션 부분을 설정 하는 부분
     */
    private String userID;

    public void onActivityCreated(Bundle b) { //option 프레그먼트 메인
        super.onActivityCreated(b);

        final EditText minText = (EditText) getView().findViewById(R.id.minText);
        final EditText maxText = (EditText) getView().findViewById(R.id.maxText);
        final EditText vibeText = (EditText) getView().findViewById(R.id.vibeText);

        final TextView heartMin = (TextView) getView().findViewById(R.id.heartMin);
        final TextView heartMax = (TextView) getView().findViewById(R.id.heartMax);
        final TextView heartVib = (TextView) getView().findViewById(R.id.heartVib);
        final Button optionSaveButton = (Button) getView().findViewById(R.id.optionSaveButton);
        userID = BlueToothActivity.userID;

        new BackgroundTask().execute();

        optionSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String minCnt = minText.getText().toString();
                final String maxCnt = maxText.getText().toString();
                final String vibration = vibeText.getText().toString();

                if (minCnt.equals("") || maxCnt.equals("") || vibration.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    AlertDialog dialog = builder.setMessage("빈 칸 없이 입력 해주세요.")
                            .setNegativeButton("확인", null) //확인 버튼을 만드는 것
                            .create();
                    dialog.show();
                    return;
                }


                heartMin.setText("최소 수치 : " + minCnt + " BPM");
                heartMax.setText("최대 수치 : " + maxCnt + " BPM");
                heartVib.setText("진동 제한 : " + vibration + "번");

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");
                            if(success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                AlertDialog dialog = builder.setMessage("저장 되었습니다.")
                                        .setPositiveButton("확인",null) //확인 버튼을 만드는 것
                                        .create();
                                dialog.show();

                                HeartFragment.minHeartCnt.setText("최소 수치 : " + minCnt);
                                HeartFragment.maxHeartCnt.setText("최대 수치 : " + maxCnt);
                                HeartFragment.vibrationCnt.setText("진동 제한 수 : " + vibration);


                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                AlertDialog dialog = builder.setMessage("저장에 실패 했습니다.")
                                        .setNegativeButton("확인",null) //확인 버튼을 만드는 것
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
                OptionUpdateRequest optionUpdateRequest = new OptionUpdateRequest(userID, minCnt, maxCnt, vibration, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getContext()); //보내는 부분
                queue.add(optionUpdateRequest);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_option, container, false);
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

    /**
     * 텍스트 뷰에 3개의 값을 데이터베이스에 저장 하는 부분
     * 저장 한 후 텍스트 뷰에 수정한 내용을 보여준다.
     */
    class BackgroundTask extends AsyncTask<Void, Void, String>
    {
        String target; //접속할 홈페이지 주소

        @Override
        protected void onPreExecute() {
            target = "http://ehdtjs3694.cafe24.com/DefaltUserOption.php?userID=" + BlueToothActivity.userID;
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
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                String minCnt;
                String maxCnt;
                String vibration;

                JSONObject object = jsonArray.getJSONObject(0);
                minCnt = object.getString("minCnt");
                maxCnt = object.getString("maxCnt");
                vibration = object.getString("vibration");

                TextView heartMin = (TextView) getView().findViewById(R.id.heartMin);
                TextView heartMax = (TextView) getView().findViewById(R.id.heartMax);
                TextView heartVib = (TextView) getView().findViewById(R.id.heartVib);

                heartMin.setText("최소 수치 : " + minCnt + " BPM");
                heartMax.setText("최대 수치 : " + maxCnt + " BPM");
                heartVib.setText("진동 제한 : " + vibration + "번");

                EditText minText = (EditText) getView().findViewById(R.id.minText);
                EditText maxText = (EditText) getView().findViewById(R.id.maxText);
                EditText vibeText = (EditText) getView().findViewById(R.id.vibeText);

                minText.setText(minCnt);
                maxText.setText(maxCnt);
                vibeText.setText(vibration);



            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}