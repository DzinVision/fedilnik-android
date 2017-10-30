package fedilnik.android;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import fedilnik.android.data.DataStore;
import fedilnik.android.data.DataStoreCallback;
import fedilnik.android.data.Preferences;
import fedilnik.android.data.Status;

public class MainActivity extends AppCompatActivity implements DataStoreCallback {
    private static final String TAG = "MainActivity";

    private ViewPager mainViewPager;
    private TabLayout tabLayout;
    private String dataDelegateUUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentDayAdapter adapter = new FragmentDayAdapter(getSupportFragmentManager(), this);

        mainViewPager = findViewById(R.id.main_view_pager);
        mainViewPager.setAdapter(adapter);
        mainViewPager.setOffscreenPageLimit(5); // Using a little bit more ram. Easier refresh
        // status UI. RAM still under 64MB so should not be a problem...

        tabLayout = findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(mainViewPager);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        setCurrentDay();

        dataDelegateUUID = UUID.randomUUID().toString();
        DataStore.getInstance().addCallback(dataDelegateUUID, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        DataStore.getInstance().removeCallback(dataDelegateUUID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataStore.getInstance().loadData(this);

        updateDateSubtitle();

        Date lastOpened = Preferences.getLastOpenedDate(this);
        Preferences.setLastOpenedDate(new Date(), this);
        if (lastOpened == null) {
            setCurrentDay();
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastOpened);
        int lastOpenedDay = calendar.get(Calendar.DAY_OF_MONTH);
        int lastOpenedMonth = calendar.get(Calendar.MONTH);
        int lastOpenedYear = calendar.get(Calendar.YEAR);

        calendar.setTime(new Date());
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        if (!(lastOpenedDay == currentDay &&
                lastOpenedMonth == currentMonth &&
                lastOpenedYear == currentYear)) {
            setCurrentDay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataStore.getInstance().saveData(this);
    }

    private void setCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                mainViewPager.setCurrentItem(0);
                break;
            case Calendar.TUESDAY:
                mainViewPager.setCurrentItem(1);
                break;
            case Calendar.WEDNESDAY:
                mainViewPager.setCurrentItem(2);
                break;
            case Calendar.THURSDAY:
                mainViewPager.setCurrentItem(3);
                break;
            case Calendar.FRIDAY:
                mainViewPager.setCurrentItem(4);
                break;
            default:
                mainViewPager.setCurrentItem(0);
        }
    }

    private void updateDateSubtitle() {
        String menuValidDate = DataStore.getInstance().getValidDate();
        getSupportActionBar().setSubtitle(menuValidDate);
    }

    @Override
    public void dataDidChange(Status status) {
        updateDateSubtitle();
    }

    @Override
    public void dataDidStartRefreshing() {

    }
}

class FragmentDayAdapter extends FragmentPagerAdapter {
    private DayFragment fragments[];
    private String titles[];

    FragmentDayAdapter(FragmentManager fm, Context context) {
        super(fm);
        fragments = new DayFragment[5];

        for (int i = 0; i < 5; ++i) fragments[i] = DayFragment.newInstance(i);
        titles = new String[5];
        titles[0] = context.getString(R.string.monday);
        titles[1] = context.getString(R.string.tuesday);
        titles[2] = context.getString(R.string.wednesday);
        titles[3] = context.getString(R.string.thursday);
        titles[4] = context.getString(R.string.friday);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return 5;
    }
}
