package criminal.com.criminalintent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by adam on 07.08.16.
 */
public class FlowerFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO= 2;

    private Flower mFlower;
    private File mPhotoFile;
    private EditText mNameField;
    private Button mStartDateButton;
    private CheckBox mNotificationCheckBox;
    private EditText mDaysField;
    private Button mEndDateButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static FlowerFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        FlowerFragment fragment = new FlowerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mFlower.setStartDate(date);
            updateDate();
        } else if (requestCode == REQUEST_PHOTO) {
            updatePhotoView();
        }
    }

    private void updateDate() {
        DateFormat dateFormat = new DateFormat();
        mStartDateButton.setText(dateFormat.format("yyyy-MM-dd", mFlower.getStartDate()));
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mFlower = FlowerLab.get(getActivity()).getFlower(crimeId);
        mPhotoFile = FlowerLab.get(getActivity()).getPhotoFile(mFlower);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_flower, container,false);

        mNameField = (EditText)v.findViewById(R.id.flower_name);
        mNameField.setText(mFlower.getName());
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mFlower.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mStartDateButton = (Button)v.findViewById(R.id.start_date);
        final DateFormat dateFormat = new DateFormat();

        mStartDateButton.setText(dateFormat.format("yyyy-MM-dd", mFlower.getStartDate()));
        mStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mFlower.getStartDate());
                dialog.setTargetFragment(FlowerFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });


        mDaysField = (EditText)v.findViewById(R.id.days);
        mDaysField.setText(String.valueOf(mFlower.getDays()));
        mDaysField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(mFlower.getStartDate() != null && validateNumber(mDaysField.getText().toString())) {
                    mFlower.setDays(Integer.valueOf(mDaysField.getText().toString()));

                    if(Integer.valueOf(mDaysField.getText().toString()) > 0) {
                        mFlower.setEndDate(addDays(mFlower.getStartDate(), Integer.valueOf(String.valueOf(mDaysField.getText()))));
                        mEndDateButton.setText(dateFormat.format("yyyy-MM-dd", mFlower.getEndDate()));
                    } else {
                        mNotificationCheckBox.setEnabled(false);
                        mNotificationCheckBox.setChecked(false);
                    }
                    mNotificationCheckBox.setEnabled(true);
                } else if(!validateNumber(mDaysField.getText().toString())) {
                    mFlower.setEndDate(null);
                    mNotificationCheckBox.setEnabled(false);
                    mNotificationCheckBox.setChecked(false);
                    mEndDateButton.setText("Fill start date and days fields.");
                }
            }
        });

        mEndDateButton = (Button)v.findViewById(R.id.end_date);
        if(mFlower.getEndDate() != null) {
            mEndDateButton.setText(dateFormat.format("yyyy-MM-dd", mFlower.getEndDate()));
        } else {
            mEndDateButton.setText("Fill start date and days fields.");
        }


        mNotificationCheckBox = (CheckBox)v.findViewById(R.id.flower_notification);

        if(mFlower.getDays() < 1) {
            mNotificationCheckBox.setEnabled(false);
            mNotificationCheckBox.setChecked(false);
        } else {
            mNotificationCheckBox.setChecked(mFlower.isNotification());
        }

        mNotificationCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the crime's solved property
                mFlower.setNotification(isChecked);
            }
        });

        PackageManager packageManager = getActivity().getPackageManager();

        mPhotoButton = (ImageButton) v.findViewById(R.id.flower_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.flower_photo);

        updatePhotoView();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_flower, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                FlowerLab.get(getActivity()).deleteFlower(mFlower);
                Intent intent = FlowerListActivity.newIntent(getActivity());
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        FlowerLab.get(getActivity())
                .updateFlower(mFlower);
    }

    public static Date addDays(Date date, int days)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(date.toString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static boolean validateNumber(String number) {
        return number.matches("^-?\\d+$");
    }



}
