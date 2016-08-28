package criminal.com.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by adam on 09.08.16.
 */
public class FlowerListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new FlowerListFragment();
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, FlowerListActivity.class);
        return intent;
    }

}
