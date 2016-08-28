package criminal.com.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by adam on 13.08.16.
 */
public class FlowerPagerActivity extends AppCompatActivity {

    private static final String EXTRA_CRIME_ID =
            "criminal.com.criminalintent.crime_id";

    private ViewPager mViewPager;
    private List<Flower> mFlowers;

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, FlowerPagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flower_pager);

        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_flower_pager_view_pager);

        mFlowers = FlowerLab.get(this).getFlowers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                Flower flower = mFlowers.get(position);
                return FlowerFragment.newInstance(flower.getId());
            }

            @Override
            public int getCount() {
                return mFlowers.size();
            }
        });

        for (int i = 0; i < mFlowers.size(); i++) {
            if (mFlowers.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
