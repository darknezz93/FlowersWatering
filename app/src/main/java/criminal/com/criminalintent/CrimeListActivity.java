package criminal.com.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

/**
 * Created by adam on 09.08.16.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, CrimeListActivity.class);
        return intent;
    }

}
