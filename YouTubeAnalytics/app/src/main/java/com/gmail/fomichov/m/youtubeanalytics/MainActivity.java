package com.gmail.fomichov.m.youtubeanalytics;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.gmail.fomichov.m.youtubeanalytics.adapters.MyPagerAdapter;
import com.gmail.fomichov.m.youtubeanalytics.settings.SettingsFragment;
import com.gmail.fomichov.m.youtubeanalytics.utils.MyLog;

public class MainActivity extends AppCompatActivity {
    public static SharedPreferences sharedPreferences;
    public static long startTime;
    public static long endTime;
    public static final String KEY_YOUTUBE_API = "AIzaSyAsR-0YjQ1cD1HRe4wY9AsH-YMLTTMj_34";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false); // подкючаем настройки, которые указаны в файле
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        MyLog.showLog("setSaveCache " + sharedPreferences.getBoolean("setSaveCache", false));
        MyLog.showLog("setNameFolder  " + sharedPreferences.getString("setNameFolder", ""));
        MyLog.showLog("setSaveTime  " + sharedPreferences.getBoolean("setSaveTime", false));

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), this);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setOffscreenPageLimit(6); // храним состояние всех фрагментов
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    // создаем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.action_settings).setIcon(getResources().getDrawable(R.drawable.ic_settings_white_24dp));
        return true;
    }

    // обрабатываем нажатие на меню и запускаем окно настроек
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, new SettingsFragment())
                    .addToBackStack(null) // нужен для возврата назад из окна настроек по кнопке назад
                    .commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
