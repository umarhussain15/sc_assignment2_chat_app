package www.seu.com.lab4_sc;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Umar on 23-Mar-16.
 */
public class BGServiceLocation extends Service implements LocationListener, ConnectionCallbacks,
        OnConnectionFailedListener {
    private boolean service_status = false;
    Location myLocation;
    private GoogleApiClient myGoogleApiClient; // Google client to interact with Google API
    public double lat,lat2;
    public double lng,lng2;
    Intent intent;
     SharedPreferences sp;
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!service_status) {
            service_status = true;
            checkGooglePlayService();
        }
        return START_NOT_STICKY;
    }

    private void checkGooglePlayService() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this)
                == ConnectionResult.SUCCESS) {
            myGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            if (!myGoogleApiClient.isConnected() || !myGoogleApiClient.isConnecting()) {
                myGoogleApiClient.connect();
            }
        } else {
            Log.e("checkGooglePlayService", "unable to connect to google play services.");
        }
    }

    protected void stopLocationUpdates() {
        if (myGoogleApiClient != null && myGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    myGoogleApiClient, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        myLocation = LocationServices.FusedLocationApi
                .getLastLocation(myGoogleApiClient);
        myLocation.setAccuracy(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (myLocation != null) {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
//            stopLocationUpdates();
//            stopSelf();
            new UserLoginTask(location.getLatitude(),location.getLongitude()).execute();
        }
    }
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        double latitude,longitude;
        String name,message,receiver;
        SharedPreferences ed;
        String type;
        public UserLoginTask(double lat, double lng) {
            latitude=lat;
            longitude=lng;


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HashMap<String, String > paras = new HashMap<>();
            paras.put("username", name);
            paras.put("lat", latitude+"");
            paras.put("lng", longitude+"");


            JSONObject json = new HttpCall().postForJSON(getResources().getString(R.string.sendmessage), paras);
            try {
                if (json==null)
                    return  "error-j";
                int success=json.getInt("success");
                Log.i("suc val int INSERT", "" + success);
                if (success==-2)
                    return "error";
                if (success==-1)
                    return json.getString("errors");

                Log.d("lastmessage-----------",ed.getString("lastmessage","-1"));
                return "signupcomplete";
            } catch (JSONException e) {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String success) {
            Log.i("success val STRING", success);
            switch(success){
                case "error-j":
                case "error":
                    break;
                case "signupcomplete":
//                    stopSelf();

                    break;
            }
        }
    }
}
