package com.example.kim.goldentime;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * Created by kim on 2017-03-27.
 */

public class Messenger {
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }



    public Messenger(Context context) {
        this.context = context;
    }

    public void sendMessageTo(String phoneNum, String message) {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNum, null, message, null, null);

        Toast.makeText(context, "메세지 전송이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
    }

}
