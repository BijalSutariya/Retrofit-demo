package com.example.loginregistration.home;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.loginregistration.R;
import com.example.loginregistration.home.HomeInterface.OnImageAddClick;
import com.example.loginregistration.home.HomeInterface.PlaceArrayAdapter;
import com.example.loginregistration.utils.ApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener, OnImageAddClick, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private View view;
    public static final int REQUEST_CAMERA = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int GALLERY_REQUEST = 3;
    private static final int VIDEO_CAPTURED_REQUEST = 4;
    private static final int PLACE_AUTOCOMPLETE_REQUEST = 5;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private HomeImageAdapter adapter;
    private List<HomeImageModel> photoList = new ArrayList<>();
    private Calendar myCalendar = Calendar.getInstance();
    private RadioGroup rgAccidentType;
    private EditText dateTime, title, name, phoneNo, email, description;
    private AutoCompleteTextView location;
    private File file;
    private ProgressBar progressBar;
    private Place place;
    private PlaceArrayAdapter placeAdapter;
    private GoogleApiClient mGoogleApiClient = null;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(23.022505, 72.571362), new LatLng(37.430610, 72.571362));

    /**
     * onCreate view initialize
     *
     * @param savedInstanceState saveinstance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();


        view = findViewById(R.id.layoutConstraint);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewHome);
        rgAccidentType = findViewById(R.id.rgHomeAccidentType);

        dateTime = findViewById(R.id.etHomeDateTime);
        dateTime.setInputType(InputType.TYPE_NULL);
        title = findViewById(R.id.etHomeTitle);
        name = findViewById(R.id.etHomeName);
        phoneNo = findViewById(R.id.etHomeContact);
        email = findViewById(R.id.etHomeEmail);
        location = findViewById(R.id.etHomeLocation);
        location.setThreshold(3);
        description = findViewById(R.id.etHomeDescription);
        Button btnProfile = findViewById(R.id.btnAddProfile);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        dateTime.setOnClickListener(this);
        //location.setOnClickListener(this);

        btnProfile.setOnClickListener(this);

        adapter = new HomeImageAdapter(photoList, this, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        getImage();

        location.setOnItemClickListener(mAutocompleteClickListener);
        placeAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        location.setAdapter(placeAdapter);

/*
        // place autocomplate using fragment

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.etHomeLocation);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("TAG", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("TAG", "An error occurred: " + status);
            }
        });*/
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = placeAdapter.getItem(position);
            if (item != null) {
                final String placeId = String.valueOf(item.placeId);
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                Log.i("TAG", "Fetching details for ID: " + item.placeId);
            }
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            place = places.get(0);
            CharSequence attributions = places.getAttributions();
            if (attributions != null) {
                location.setText(attributions.toString());
            }
        }
    };


    /**
     * getImage add the images in model
     */
    private void getImage() {
        HomeImageModel model = new HomeImageModel();
        photoList.add(model);
        adapter.notifyDataSetChanged();
    }

    /**
     * onclick listner interface
     *
     * @param v View
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.etHomeDateTime:
                selectDateAndTime();
                break;
            case R.id.etHomeLocation:
                searchLocation();
                break;
            case R.id.btnAddProfile:
                validateData();
                break;
        }

    }

    /**
     * search location pemission
     */
    private void searchLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            intentLocation();
        } else {
            String[] permossionRequested = {Manifest.permission.ACCESS_FINE_LOCATION};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permossionRequested, PLACE_AUTOCOMPLETE_REQUEST);
            }
        }
    }

    /**
     * intent location using google place api
     */
    private void intentLocation() {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        try {
            Intent locationIntent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .setFilter(typeFilter)
                            .build(this);
            startActivityForResult(locationIntent, PLACE_AUTOCOMPLETE_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Log.d("TAG", "Google Play Service Repairable" + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Log.d("TAG", "Google Play Service Not Available" + e.getMessage());
        }
    }

    /**
     * select date from datePickerDialog
     */
    private void selectDateAndTime() {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };
        new DatePickerDialog(HomeActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    /**
     * updateLable
     * set date formate and set in editText
     */
    private void updateLabel() {
        String myFormat = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateTime.setText(sdf.format(myCalendar.getTime()));
    }

    /**
     * selectPhoto
     * capture or from gallery
     */
    private void selectPhoto() {
        final CharSequence[] items = {"Take Photo", "Choose photo", "Capture Video"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    requestpermission();
                } else if (items[item].equals("Choose photo")) {
                    displayImage();
                } else if (items[item].equals("Capture Video")) {
                    captureVideo();
                }
            }
        });
        builder.show();
    }

    /**
     * capture Video
     */
    private void captureVideo() {
        Intent captureVideoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
        if (captureVideoIntent.resolveActivity(getPackageManager()) != null) {

            try {
                file = getOutputVideoFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            captureVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(captureVideoIntent, VIDEO_CAPTURED_REQUEST);

        }


    }

    /**
     * getOutPutVideoFile format of video file path
     *
     * @return file path
     * @throws IOException exception
     */
    private File getOutputVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "VIDEO_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".mp3", storageDir);
    }

    /**
     * onRequestParmissionResult give camera and gallery permossion
     *
     * @param requestCode  requestcode (REQUEST_CAMERA,GALLERY_REQUEST)
     * @param permissions  gallery and camera pemission
     * @param grantResults result array
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentcamera();
            } else {
                Toast.makeText(this, "Unable to invoke camera without permossion", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == GALLERY_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImage();
            } else {
                Toast.makeText(this, "cannot take photo", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentLocation();
            } else {
                Toast.makeText(this, "You need to enable location permission first", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * requestpermission
     * cameraRequest
     */
    private void requestpermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            intentcamera();
        } else {
            String[] permossionRequested = {Manifest.permission.CAMERA};
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permossionRequested, REQUEST_CAMERA);
            }
        }
    }

    /**
     * intent camera
     * create file for captured image and store
     */
    private void intentcamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {

            try {
                file = getOutputMediaFile() ;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, CAMERA_REQUEST);

        }
    }

    /**
     * getOutPutMediaFile
     *
     * @return file path
     * @throws IOException exception
     */
    private File getOutputMediaFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    /**
     * displayImage
     * gallery request
     */

    private void displayImage() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            openImage();
        } else {
            String[] permossionRequested = new String[0];
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                permossionRequested = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permossionRequested, GALLERY_REQUEST);
            }
        }
    }

    /**
     * openImage gallery intent
     */
    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    /**
     * onActivityResult
     *
     * @param requestCode camera & gallery
     * @param resultCode  camera & gallery
     * @param data        get data from intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            if (file != null) {
                addImage(file.getAbsolutePath());
            }
        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                addImage(getRealPathFromURI(uri));
            }
        } else if (requestCode == VIDEO_CAPTURED_REQUEST && resultCode == RESULT_OK) {
            if (file != null) {
                photoList.remove(0);
                adapter.notifyItemRemoved(0);

                addVideo(file.getAbsolutePath());
                Log.i("TAG", "onActivityResult: " + file.getAbsolutePath());
            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlaceAutocomplete.getPlace(this, data);
                location.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i("TAG", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("TAG", "cancel");
            }

        }
    }

    private void addVideo(String absolutePath) {
        HomeImageModel model = new HomeImageModel();
        model.setVideo(absolutePath);
        photoList.add(model);
        adapter.notifyDataSetChanged();
    }

    private void addImage(String absolutePath) {
        HomeImageModel model = new HomeImageModel();
        model.setImages(absolutePath);
        photoList.add(model);
        adapter.notifyDataSetChanged();
    }


    /**
     * getRealPathFromURI
     *
     * @param contentUri uri
     * @return file path
     */
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(
                this,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    /**
     * validateData validation and post
     */

    private void validateData() {
        int selectedId = rgAccidentType.getCheckedRadioButtonId();
        RadioButton rbAccidentType = findViewById(selectedId);
        String strAccidentType = rbAccidentType.getText().toString();
        String strDate = dateTime.getText().toString().trim();
        String strTitle = title.getText().toString().trim();
        String strName = name.getText().toString().trim();
        String strPhoneNo = phoneNo.getText().toString().trim();
        String strEmail = email.getText().toString().trim();
        String strLocation = location.getText().toString().trim();
        String strDescription = description.getText().toString();

        Snackbar snackbar;
        if (TextUtils.isEmpty(strDate)) {
            snackbar = Snackbar.make(view, "Select Date", Snackbar.LENGTH_SHORT);
            snackbar.show();
            dateTime.setFocusableInTouchMode(true);
            dateTime.requestFocus();
        } else if (TextUtils.isEmpty(strTitle)) {
            snackbar = Snackbar.make(view, "Enter title", Snackbar.LENGTH_SHORT);
            snackbar.show();
            title.setFocusableInTouchMode(true);
            title.requestFocus();
        } else if (TextUtils.isEmpty(strName)) {
            snackbar = Snackbar.make(view, "Enter Your Name", Snackbar.LENGTH_SHORT);
            snackbar.show();
            name.setFocusableInTouchMode(true);
            name.requestFocus();
        } else if (!isValidMobile(strPhoneNo)) {
            snackbar = Snackbar.make(view, "Enter Your Phone Number", Snackbar.LENGTH_SHORT);
            snackbar.show();
            phoneNo.setFocusableInTouchMode(true);
            phoneNo.requestFocus();
        } else if (!isValidEmail(strEmail)) {
            snackbar = Snackbar.make(view, "Enter Your Email", Snackbar.LENGTH_SHORT);
            snackbar.show();
            email.setFocusableInTouchMode(true);
            email.requestFocus();
        } else if (TextUtils.isEmpty(strLocation)) {
            snackbar = Snackbar.make(view, "Enter Location", Snackbar.LENGTH_SHORT);
            snackbar.show();
            location.setFocusableInTouchMode(true);
            location.requestFocus();
        } else if (TextUtils.isEmpty(strDescription)) {
            snackbar = Snackbar.make(view, "Describe Accident", Snackbar.LENGTH_SHORT);
            snackbar.show();
            description.setFocusableInTouchMode(true);
            description.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            // upload data using retrofit

            MultipartBody.Part images;
            List<MultipartBody.Part> parts = new ArrayList<>();
            MultipartBody.Part video = null;
            for (int i = 0; i < photoList.size(); i++) {
                if (photoList.get(i).getImages() != null) {
                    File file = new File(photoList.get(i).getImages());
                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), file);
                    images = MultipartBody.Part.createFormData("images[" + i + "]", file.getName(), requestFile);
                    parts.add(images);
                } else if (photoList.get(i).getVideo() != null) {
                    File file1 = new File(photoList.get(i).getVideo());
                    RequestBody videoBody = RequestBody.create(MediaType.parse("video/MP4"), file1);
                    video = MultipartBody.Part.createFormData("video[]", file1.getName(), videoBody);
                }
                Log.d("TAG", "validateData: "+parts);
            }
            RequestBody user_id = RequestBody.create(MultipartBody.FORM, "51");
            RequestBody title = RequestBody.create(MultipartBody.FORM, strTitle);
            RequestBody accident_date = RequestBody.create(MultipartBody.FORM, strDate);
            RequestBody accident_time = RequestBody.create(MultipartBody.FORM, "2:30 PM");
            RequestBody accident_type = RequestBody.create(MultipartBody.FORM, strAccidentType);
            RequestBody building_name = RequestBody.create(MultipartBody.FORM, "Test");
            RequestBody location = RequestBody.create(MultipartBody.FORM, strLocation);
            RequestBody latitude = RequestBody.create(MultipartBody.FORM, String.valueOf(place.getLatLng().latitude));
            RequestBody longitude = RequestBody.create(MultipartBody.FORM, String.valueOf(place.getLatLng().longitude));
            RequestBody address = RequestBody.create(MultipartBody.FORM, "demo");
            RequestBody description = RequestBody.create(MultipartBody.FORM, strDescription);
            RequestBody witnessType = RequestBody.create(MultipartBody.FORM, "witness");

            Call<ResponseBody> call = ApiClient.getRetrofit().uploadFile(parts, video, user_id, title, accident_date, accident_time, accident_type,
                    building_name, location, latitude, longitude, address, description, witnessType);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                            Log.d("TAG", "onResponse: " + response.message());
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "somthing went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("TAG", "onFailure: " + t.getMessage());
                }
            });

        }
    }

    /**
     * onMyClick from own Interface
     *
     * @param view     view
     * @param position item position
     */
    @Override
    public void onMyClick(View view, int position) {
        switch (view.getId()) {
            case R.id.ivImageProof:
                selectPhoto();
                break;
            case R.id.ivDelete:
                if (!TextUtils.isEmpty(photoList.get(position).getImages())) {
                    photoList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyDataSetChanged();
                } else if (!TextUtils.isEmpty(photoList.get(position).getVideo())) {
                    photoList.remove(position);
                    adapter.notifyItemRemoved(position);

                    HomeImageModel model = new HomeImageModel();
                    model.setVideo("");
                    photoList.add(model);
                    adapter.notifyDataSetChanged();

                }
                break;
        }
    }

    /**
     * isValidEmail email validation
     *
     * @param target email
     * @return valid email
     */
    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    /**
     * isValidMobile contact validation
     *
     * @param phone contact
     * @return valid contact
     */
    public static boolean isValidMobile(String phone) {
        return phone.length() == 10;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        placeAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i("TAG", "Google Places API connected.");
    }

    @Override
    public void onConnectionSuspended(int i) {
        placeAdapter.setGoogleApiClient(null);
        Log.e("TAG", "Google Places API connection suspended.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("TAG", "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }
}
