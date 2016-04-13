package www.seu.com.lab4_sc;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NearbyUsers extends FragmentActivity  implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private  GoogleMap mGoogleMap;
    Location mLastLocation;
    GoogleApiClient myGoogleApiClient;
    LatLng latLng;

    SupportMapFragment mFragment;
    Marker currLocationMarker;
    private AlertDialog ad;
    ProgressDialog mProgressDialog;
    int selected=4;
    List<Marker> atm_markers = new ArrayList<>();
    String Ilat,Ilng,Irad,Igender,Iusername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_users);
        Irad=getIntent().getStringExtra("radius");
        Iusername=getIntent().getStringExtra("username");
        if (getIntent().hasExtra("gender")) {
            Igender = getIntent().getStringExtra("gender");
            View view = this.getWindow().getDecorView();
            if (Igender.equals("Female"))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(getResources().getColor(R.color.female));
                }

        }
        mProgressDialog= new ProgressDialog(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mGoogleMap.setIndoorEnabled(true);
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        buildGoogleApiClient();
        myGoogleApiClient.connect();
//        new GetUsers()
    }
    protected synchronized void buildGoogleApiClient() {
        myGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }
    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                myGoogleApiClient);
        if (mLastLocation != null) {
            //create a new LatLng obj to store position
            latLng = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);//set the position of marker to new location
            markerOptions.icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            //set lat, lng to appear in marker title
            markerOptions.title("You @ " + mLastLocation.getLatitude()
                    + " , " + mLastLocation.getLongitude());
            currLocationMarker = mGoogleMap.addMarker(markerOptions);//show marker on map
            //zoom to current position:
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng).zoom(15).build();
            mGoogleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
            new GetUsers(latLng.latitude+"",latLng.longitude+"",Irad,Igender).execute();
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    // Async Task to get atms coordinates from server
    public class GetUsers extends AsyncTask<Void, Void, JSONObject> {

        // current location and searching radius will be sent via POST CALL
        String lat,lng,radius,gender=null;
        GetUsers(String lat, String lng, String radius, String gender) {
            this.lat=lat;
            this.lng=lng;
            this.radius=radius;
            if(gender!=null)
            this.gender=gender;
        }

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            //preparing post elements
            HashMap<String, String> paras = new HashMap<>();
            paras.put("lat", lat);
            paras.put("lng", lng);
            paras.put("radius", radius);
            paras.put("username", Iusername);
            if(gender!=null){
                paras.put("gender", gender);
            }
            Log.d("Parameters are : ",paras.toString());
            // Calling post function from HttpCall class
            JSONObject json = new HttpCall().postForJSON(getResources().getString(R.string.findusers), paras);
            try {
                if (json==null)
                    return  null;
                int success=json.getInt("success");
                Log.i("suc val int", "" + success);
                if (success==-2)
                    return null;
                if (success==-1)
                    return null;
                // if we get successful JSON back then we will update UI
                // in onPostExecute task
                return json;
            } catch (JSONException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(JSONObject success) {
            mProgressDialog.hide();
            Log.i("success val", success.toString());
            if (success == null) {
                Toast.makeText(getApplicationContext(),
                        "Error Occurred! Try Again", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "Successfully updated!", Toast.LENGTH_SHORT).show();


                try {
                    // before setting new data, first remove the old markers
                    for (int i = 0; i < atm_markers.size(); i++) {
                        atm_markers.get(i).remove();
                    }
                    // also clear the holding list
                    atm_markers.clear();

                    // read json array from JSONObject
                    JSONArray ja = success.getJSONArray("results");
                    for (int i = 0; i < ja.length(); i++) {
                        // for each array entry create new marker and
                        // show that on the map and store in arraylist of markers
                        JSONObject result = ja.getJSONObject(i);
                        //create a new LatLng obj to store position
                        LatLng temp = new LatLng(result.getDouble("lat"), result.getDouble("lng"));
                        MarkerOptions markerOptions = new MarkerOptions();
                        //set the position of marker to new location
                        markerOptions.position(temp);
                        //set name in title
                        markerOptions.title(result.getString("place"));
                        Marker marker =
                                mGoogleMap.addMarker(markerOptions);

                        atm_markers.add(marker);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
