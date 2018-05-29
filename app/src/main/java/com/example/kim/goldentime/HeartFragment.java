package com.example.kim.goldentime;

/**
 * Created by PL1 on 2017-03-29.
 * 심장박동수 측정을 위한 프레그먼트
 * 심박측정, 심박측정 알람, AED위치
 */
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.example.kim.goldentime.Miband.*;

import org.json.JSONArray;
import org.json.JSONObject;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static android.content.Context.LOCATION_SERVICE;

public class HeartFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HeartFragment() {
        // Required empty public constructor
        setRetainInstance(true);
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
    public static HeartFragment newInstance(String param1, String param2) {
        HeartFragment fragment = new HeartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    static public boolean rating = false;

    static private boolean activated = false;

    private static final String TAG = HeartFragment.class.getSimpleName();

    private Miband miband;
    private BluetoothAdapter mBluetoothAdapter;

    private TextView heartRateText, batteryText;

    private Timer HRTimer;
    private Handler HRHandler;
    private Timer vibTimer;
    private Handler vibHandler;

    private int currentHR = 0;
    private int unusualHRCount = 0;

    static public TextView minHeartCnt;
    static public TextView maxHeartCnt;
    static public TextView vibrationCnt;


    private HeartrateListener heartrateNotifyListener = new HeartrateListener() {
        @Override
        public void onNotify(final int heartRate) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    currentHR = heartRate;
                    heartRateText.setText("현재 심박수 : " + heartRate + " BPM");
                    if(heartRate < Integer.parseInt(maxCnt) || heartRate > Integer.parseInt(minCnt)) { //옵션 값 최소, 최대 심박수 설정 값과 비교
                        unusualHRCount++;
                    }
                    else {
                        unusualHRCount = 0;
                    }
                }
            });
        }
    };

    private final MibandCallback mibandCallback = new MibandCallback() {
        @Override
        public void onSuccess(Object data, int status) {
            switch (status) {
                case MibandCallback.STATUS_SEARCH_DEVICE:
                    Log.e(TAG, "성공: STATUS_SEARCH_DEVICE");
                    miband.connect((BluetoothDevice) data, this);
                    break;
                case MibandCallback.STATUS_CONNECT:
                    Log.e(TAG, "성공: STATUS_CONNECT");
                    miband.getUserInfo(this);
                    break;
                case MibandCallback.STATUS_SEND_ALERT:
                    Log.e(TAG, "성공: STATUS_SEND_ALERT");
                    break;
                case MibandCallback.STATUS_GET_USERINFO:
                    Log.e(TAG, "성공: STATUS_GET_USERINFO");
                    UserInfo userInfo = new UserInfo().fromByteData(((BluetoothGattCharacteristic) data).getValue());
                    miband.setUserInfo(userInfo, this);
                    break;
                case MibandCallback.STATUS_SET_USERINFO:
                    Log.e(TAG, "성공: STATUS_SET_USERINFO");
                    miband.setHeartRateScanListener(heartrateNotifyListener);
                    break;
                case MibandCallback.STATUS_START_HEARTRATE_SCAN:
                    Log.e(TAG, "성공: STATUS_START_HEARTRATE_SCAN");
                    break;
                case MibandCallback.STATUS_GET_BATTERY:
                    Log.e(TAG, "성공: STATUS_GET_BATTERY");
                    final int level = (int) data;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            batteryText.setText(level+ " % battery");

                        }
                    });
                    break;
            }
        }

        @Override
        public void onFail(int errorCode, String msg, int status) {
            switch (status) {
                case MibandCallback.STATUS_SEARCH_DEVICE:
                    Log.e(TAG, "실패: STATUS_SEARCH_DEVICE");
                    break;
                case MibandCallback.STATUS_CONNECT:
                    Log.e(TAG, "실패: STATUS_CONNECT");
                    break;
                case MibandCallback.STATUS_SEND_ALERT:
                    Log.e(TAG, "실패: STATUS_SEND_ALERT");
                    break;
                case MibandCallback.STATUS_GET_USERINFO:
                    Log.e(TAG, "실패: STATUS_GET_USERINFO");
                    break;
                case MibandCallback.STATUS_SET_USERINFO:
                    Log.e(TAG, "실패: STATUS_SET_USERINFO");
                    break;
                case MibandCallback.STATUS_START_HEARTRATE_SCAN:
                    Log.e(TAG, "실패: STATUS_START_HEARTRATE_SCAN");
                    break;
                case MibandCallback.STATUS_GET_BATTERY:
                    Log.e(TAG, "실패: STATUS_GET_BATTERY");
                    break;

            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static String HR; // 비정상 심박수와 비교 하기 위해 카운트를 설정
    public static int vibrationCount; // 비정상 심박수로 진동을 울리면 카운트 하는 변수
    public static boolean vibeOn = false; //진동

    public static String minCnt; //자신이 설정한 최소 심박수 범위
    public static String maxCnt; // 자신이 설정한 최대 심박수 범위
    public static String vibration; // 자신이 설정한 진동수 제한 범위

    public ArrayList<String> userTel; // 메세지를 보낼 전화번호 리스트

    int streamId;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        final TextView textProgress = (TextView) getView().findViewById(R.id.textProgress);
        final TextView heartrateInfomation = (TextView) getView().findViewById(R.id.heartRateInfomation);

        heartRateText = (TextView) getActivity().findViewById(R.id.heartRateText);
        batteryText = (TextView) getActivity().findViewById(R.id.batteryText);

        userTel = new ArrayList<String>(); //리스트 초기화

        getActivity().findViewById(R.id.button_AED).setOnClickListener(this);
        getActivity().findViewById(R.id.button_battery).setOnClickListener(this);
        getActivity().findViewById(R.id.heartRateInfomation).setOnClickListener(this);

        minHeartCnt = (TextView) getView().findViewById(R.id.minCnt);
        maxHeartCnt = (TextView) getView().findViewById(R.id.maxCnt);
        vibrationCnt = (TextView) getView().findViewById(R.id.vibration);

        minHeartCnt.setText("최소 수치 : " + minCnt);
        maxHeartCnt.setText("최대 수치 : " + maxCnt);
        vibrationCnt.setText("진동 제한 수 : " + vibration);

        HR = "5";
        final SoundPool sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        final int soundID = sp.load(getContext(), R.raw.siren, 1);

        textProgress.setText("심장박동 측정 준비");
        /**
         *  심박수 측정 시작/중지를 토글버튼으로 구현
         *  이상심박수가 5회 이상이면 반응
         */
        final ToggleButton tb = (ToggleButton) this.getActivity().findViewById(R.id.button_heart);
        tb.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                if(tb.isChecked()) {
                    textProgress.setText("심장박동 측정중..");
                    rating = true;
                    HRTimer = new Timer(false);
                    HRHandler = new Handler();
                    HRTimer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    HRHandler.post(new Runnable() {
                                        public void run() {
                                            if (unusualHRCount == Integer.parseInt(HR)) {
                                                vibeOn = true;
                                                push();
                                                HRTimer.cancel();
                                                HRTimer = null;

                                            } else {
                                                miband.startHeartRateScan(1, mibandCallback);
                                            }
                                        }
                                    });
                                }
                            }, 0, 500
                    );
                    vibTimer = new Timer(false);
                    vibHandler = new Handler();
                    vibTimer.schedule(
                            new TimerTask() {
                                @Override
                                public void run() {
                                    HRHandler.post(new Runnable() {
                                        public void run() {
                                            if (vibeOn) {
                                                if ((vibrationCount == Integer.parseInt(vibration))) {
                                                    new PhoneNumberList().execute(); //전화번호 리스트에 담긴 것에 문자를 보낸다
                                                    streamId = sp.play(soundID, 1, 1, 1, -1, 1);
                                                    vibeOn = false;
                                                    activated = true;
                                                    vibTimer.cancel();
                                                    vibTimer = null;
                                                } else {
                                                    miband.sendAlert(mibandCallback);
                                                    vibrationCount++;
                                                }
                                            }
                                        }
                                    });
                                }
                            }, 0, 3000
                    );
                } else{
                    rating = false;
                    textProgress.setText("심장박동 측정 준비");
                    if(vibeOn){
                        unusualHRCount = 0;
                        vibrationCount = 0;
                        vibeOn = false;
                        vibTimer.cancel();
                        vibTimer = null;
                    } else if(activated) {
                        unusualHRCount = 0;
                        vibrationCount = 0;
                        sp.stop(streamId);
                        activated = false;
                    } else{
                        unusualHRCount = 0;
                        HRTimer.cancel();
                        HRTimer = null;
                    }
                }
            }
        });

        miband = new Miband(getActivity().getApplicationContext());

        mBluetoothAdapter = ((BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();

        miband = new Miband(getActivity().getApplicationContext());

        miband.searchDevice(mBluetoothAdapter, this.mibandCallback);

        miband.setDisconnectedListener(new NotifyListener() {
            @Override
            public void onNotify(byte[] data) {
                miband.searchDevice(mBluetoothAdapter, mibandCallback);
            }
        });

        new BackgroundTask().execute();

    }

    @Override
    public void onClick (View view) {
        int i = view.getId();

        if (i == R.id.button_AED) {
            if(checkGPS()) {
                Intent intent = new Intent(getActivity().getApplicationContext(), AEDMapsActivity.class);
                this.startActivity(intent);
            }
        } else if (i == R.id.button_battery) {
            miband.getBatteryLevel(this.mibandCallback);
        } else if (i == R.id.heartRateInfomation){
            startActivity(new Intent(getContext(), HeartRatePop.class));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_heart, container, false);
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
     * gps 설정 확인
     */
    public boolean checkGPS() {
        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        boolean isGPS = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPS) {
            return true;
        } else {
            new AlertDialog.Builder(getContext())
                    .setMessage("GPS가 꺼져있습니다.\n ‘위치 서비스’에서 ‘Google 위치 서비스’를 체크해주세요")
                    .setPositiveButton("설정", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("취소", null).show();
        }
        return false;
    }

    /**
     *사용자 옵션의 최소최대 심박수와 제한진동 수를 데이터베이스에서 가져온다.
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

                JSONObject object = jsonArray.getJSONObject(0);
                minCnt = object.getString("minCnt");
                maxCnt = object.getString("maxCnt");
                vibration = object.getString("vibration");

                minHeartCnt.setText("최소 수치 : " + minCnt);
                maxHeartCnt.setText("최대 수치 : " + maxCnt);
                vibrationCnt.setText("진동 제한 수 : " + vibration);



            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *사용자의 주소록에서 전화번호를 데이터베이스를 통해서 가지고 와서 문자를 자동으로 보내는 부분
     */
    class PhoneNumberList extends AsyncTask<Void, Void, String>
    {
        String target; //접속할 홈페이지 주소

        @Override
        protected void onPreExecute() {
            target = "http://ehdtjs3694.cafe24.com/PhoneNumberList.php?userID=" + BlueToothActivity.userID;
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

                int count = 0;

                while(count < jsonArray.length()) {
                    JSONObject object = jsonArray.getJSONObject(count); //현재 배열의 원소값 저장
                    //해당 값을 들 가져온다.
                    userTel.add(object.getString("userTel"));

                    count++;
                }

                for(int i=0 ; i<userTel.size() ; i++) { // 리스트에 담긴 전화번호에 순서대로 문자를 보낸다.
                    Messenger messenger = new Messenger(getContext());
                    messenger.sendMessageTo(userTel.get(i), "GoldenTime 어플리케이션에서 송신한 메시지입니다.\n사용자가 응급상황입니다.");
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void push(){
        NotificationManager notificationManager = (NotificationManager) HeartFragment.this.getActivity().getSystemService(getContext().NOTIFICATION_SERVICE);
        Intent intent = new Intent(getContext(), FragMainActivity.class); //인텐트 생성.


        Notification.Builder builder = new Notification.Builder(getActivity().getApplicationContext());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);//현재 액티비티를 최상으로 올리고, 최상의 액티비티를 제외한 모든 액티비티를 없앤다.

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setSmallIcon(R.drawable.aed_heart).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle("심장박동에 이상발생").setContentText("취소하실려면 탭하여 측정중지버튼을 누르세요")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingNotificationIntent).setAutoCancel(true).setOngoing(true);

        notificationManager.notify(1, builder.build()); // Notification send
    }

}
