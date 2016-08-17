package criminal.com.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Created by adam on 09.08.16.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

}
