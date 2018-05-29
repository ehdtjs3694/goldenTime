
package com.example.kim.goldentime;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class FragMainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;

    MenuItem prevMenuItem;

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frag_main);

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //Initializing the bottomNavigationView
        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        boolean state = HeartFragment.rating;

                        if(!state) {
                            switch (item.getItemId()) {
                                case R.id.contactButton:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.heartButton:
                                    viewPager.setCurrentItem(1);
                                    break;
                                case R.id.optionButton:
                                    viewPager.setCurrentItem(2);
                                    break;
                            }
                        } else {
                            new AlertDialog.Builder(FragMainActivity.this)
                                    .setMessage("심장박동수 측정 중에는 메뉴이동을 할 수 없습니다.")
                                    .setNegativeButton("확인", null).show();
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Disable ViewPager Swipe
       viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                boolean state = HeartFragment.rating;
                int action = event.getAction();

                if(!state) {
                    return FragMainActivity.super.onTouchEvent(event);
                }
                if(action == MotionEvent.ACTION_UP){
                    new AlertDialog.Builder(FragMainActivity.this)
                            .setMessage("심장박동수 측정 중에는 메뉴이동을 할 수 없습니다.")
                            .setNegativeButton("확인", null).show();
                }
                return true;
            }

        });

        setupViewPager(viewPager);
        viewPager.setCurrentItem(1);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContactFragment());
        adapter.addFragment(new HeartFragment());
        adapter.addFragment(new OptionFragment());
        viewPager.setAdapter(adapter);
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
}


