package www.seu.com.lab4_sc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class InsertData extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Button BUpload, Bchoose;
    private EditText Name, Experience, Department, DOB, DOJ;
    private ImageView imageView;
    public static final String UPLOAD_KEY = "image";
//    public static final String TAG = "MY MESSAGE";
    //private final LocationListener mLocationListener;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private UserLoginTask mAuthTask;
    private ProgressDialog mProgressDialog;
    private Uri filePath;

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_data);
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        Bchoose = (Button) findViewById(R.id.bchoose);
//        Bchoose=(Button)findViewById(R.id.)
        Bchoose.setOnClickListener(this);
        BUpload = (Button) findViewById(R.id.sendd);
        BUpload.setOnClickListener(this);
        Name = (EditText) findViewById(R.id.name);
        Department = (EditText) findViewById(R.id.city);
        Experience = (EditText) findViewById(R.id.lastname);
        DOB = (EditText) findViewById(R.id.dob);
        DOJ = (EditText) findViewById(R.id.doj);
        imageView = (ImageView) findViewById(R.id.imageView);

    }

    @Override
    public void onClick(View v) {

        if (v == Bchoose) {
            showFileChooser();
        }
        if (v == BUpload) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
                if(TextUtils.isEmpty(Name.getText().toString()) ||TextUtils.isEmpty(Department.getText().toString())
                        ||TextUtils.isEmpty(Experience.getText().toString()) ||TextUtils.isEmpty(DOB.getText().toString())
                        ||TextUtils.isEmpty(DOJ.getText().toString()) || imageView.getDrawable()==null){

                    Toast.makeText(getApplicationContext(),"Fill the fields please",Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuthTask= new UserLoginTask();
                mAuthTask.execute();
            }

    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        AlertDialog ad;
        private final String name,exp,dep,dob,doj,photo;
        UserLoginTask() {
           name=Name.getText().toString();
            exp=Experience.getText().toString();
            dep=Department.getText().toString();
            dob=DOB.getText().toString();
            doj=DOJ.getText().toString();
            photo=getStringImage(bitmap);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //preparing post elements
            double latitude,longitude;

            HashMap<String, String > paras = new HashMap<>();
            paras.put("name", name);
            paras.put("exp", exp);
            paras.put("dep", dep);
            paras.put("dob", dob);
            paras.put("doj", doj);
            paras.put("photo", photo);
            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                paras.put("latitude",latitude+"");
                paras.put("longitude",longitude+"");


            } else {
            }

            // Calling post function from HttpCall class
            JSONObject json = new HttpCall().postForJSON(getResources().getString(R.string.server_send), paras);
            try {
                if (json==null)
                    return  "error-j";
                int success=json.getInt("success");
                Log.i("suc val int", "" + success);
                if (success==-2)
                    return "error";
                if (success==-1)
                    return json.getString("errors");

                return "signupcomplete";
            } catch (JSONException e) {
                return "error";
            }
            // TODO: register the new account here.

        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            mProgressDialog.hide();
            Log.i("success val",success);
            switch(success){
                case "error-j":
                case "error":
                    Toast.makeText(getApplicationContext(), "Error Occurred! Try Again", Toast.LENGTH_SHORT).show();
                    break;
                case "signupcomplete":
                   Toast.makeText(getApplicationContext(),"Successfully Sent!",Toast.LENGTH_SHORT).show();
                   // finish();
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mProgressDialog.hide();
        }
    }
}
