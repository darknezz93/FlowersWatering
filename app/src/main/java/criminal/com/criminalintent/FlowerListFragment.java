package criminal.com.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;


import java.util.List;
import java.util.Locale;

import backgroundService.NotificationService;

/**
 * Created by adam on 09.08.16.
 */
public class FlowerListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private FlowerAdapter mAdapter;
    private boolean mSubtitleVisible;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    int PLACE_PICKER_REQUEST = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.flower_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void updateUI() {
        FlowerLab flowerLab = FlowerLab.get(getActivity());
        List<Flower> flowers = flowerLab.getFlowers();

        if(mAdapter == null) {
            mAdapter = new FlowerAdapter(flowers);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setFlowers(flowers);
            mAdapter.notifyDataSetChanged();
        }

        NotificationService.setServiceAlarm(getActivity(), true);

        updateSubtitle();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Flower flower = new Flower();
                FlowerLab.get(getActivity()).addFlower(flower);
                Intent intent = FlowerPagerActivity
                        .newIntent(getActivity(), flower.getId());
                startActivity(intent);
                return true;
            case R.id.menu_item_set_localization:
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    GooglePlayServicesUtil
                            .getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getActivity());
                String toastMsg = String.format("Localization updated: %s", place.getName());

                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                LocalizationLab localizationLab = LocalizationLab.get(getActivity());
                Localization localization = new Localization(latitude,longitude);
                localization.setId(1);
                localizationLab.addLocalization(localization);

                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateSubtitle() {
        FlowerLab flowerLab = FlowerLab.get(getActivity());
        int crimeCount = flowerLab.getFlowers().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        //if (!mSubtitleVisible) {
        //    subtitle = null;
        //}

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }



    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Flower mFlower;
        private TextView mTitleTextView;
        private TextView mEndDateTextView;
        private CheckBox mNotificationCheckBox;

        public CrimeHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_flower_name_text_view);
            mEndDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_flower_end_date_text_view);
            mNotificationCheckBox = (CheckBox) itemView.findViewById(R.id.list_item_flower_notification_check_box);

            mNotificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Set the crime's solved property
                    mFlower.setNotification(isChecked);
                    FlowerLab.get(getActivity()).updateFlower(mFlower);
                }
            });

            itemView.setOnClickListener(this);
        }

        public void bindFlower(Flower flower) {

            final DateFormat dateFormat = new DateFormat();

            mFlower = flower;
            mTitleTextView.setText(mFlower.getName());
            if(mFlower.getEndDate() != null) {
                mEndDateTextView.setText(dateFormat.format("yyyy-MM-dd", mFlower.getEndDate()));
            } else {
                mEndDateTextView.setText("No pending notification.");
            }
            mNotificationCheckBox.setChecked(mFlower.isNotification());
        }

        @Override
        public void onClick(View v) {
            Intent intent = FlowerPagerActivity.newIntent(getActivity(), mFlower.getId());
            startActivity(intent);
        }
    }



    private class FlowerAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Flower> mFlowers;

        public FlowerAdapter(List<Flower> flowers) {
            mFlowers = flowers;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_flower, parent, false);
            return new CrimeHolder(view);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Flower flower = mFlowers.get(position);
            holder.bindFlower(flower);
        }

        @Override
        public int getItemCount() {
            return mFlowers.size();
        }

        public List<Flower> getFlowers() {
            return mFlowers;
        }

        public void setFlowers(List<Flower> flowers) {
            mFlowers = flowers;
        }
    }
}
