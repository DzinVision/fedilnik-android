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

import fedilnik.android.data.DataStore;

public class MainActivity extends AppCompatActivity {
    private ViewPager mainViewPager;
    private TabLayout tabLayout;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataStore.getInstance().loadData(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DataStore.getInstance().saveData(this);
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
