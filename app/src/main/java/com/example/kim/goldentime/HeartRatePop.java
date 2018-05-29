package com.example.kim.goldentime;

/**
 * Created by PL1 on 2017-04-18.
 */
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;

public class HeartRatePop extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.heartratepop);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.6));
    }
}
