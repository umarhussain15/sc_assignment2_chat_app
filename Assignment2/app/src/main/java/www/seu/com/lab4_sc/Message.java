package www.seu.com.lab4_sc;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Message extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    ListView lv;
    ListAdapter la;
    TextView tvName, tvMessage;
    EditText etmessage;
    Button send;
    String curu;
    IntentFilter i;
    MyReceiver m;
    ArrayList<HashMap<String, Object>> list;
    AlarmManager alarmManager;
    String lastmessage = "-1";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static final String BROADCAST = "PACKAGE_NAME.android.action.broadcast";
    private Location mLastLocation;
    public double lat;
    public double lng;
    private GoogleApiClient mGoogleApiClient;
    private ImageView imageView;
    public static final String UPLOAD_KEY = "image";
    //    public static final String TAG = "MY MESSAGE";
    //private final LocationListener mLocationListener;
    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private Uri filePath;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        Bundle income = getIntent().getExtras();
        setTitle(income.getString("username"));
        curu = income.getString("username");
        Toast.makeText(getApplicationContext(), "Receiver Name" + curu, Toast.LENGTH_SHORT).show();
        sp=getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE);
        list = new ArrayList<>();
        m = new MyReceiver();
        i = new IntentFilter("com.latlong.org");
        lv = (ListView) findViewById(R.id.listView2);
        la = new SimpleAdapter(Message.this,
                list, R.layout.messageitem,
                new String[]{"name", "message", "messageid", "type"}, new int[]{R.id.tvrecp, R.id.tvmessage, R.id.hid, R.id.type}) {

            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
//
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView) view.findViewById(R.id.tvrecp);
                if (tv != null) {
                    if (tv.getText().toString().contains(" (me)")) {
                        view.setBackgroundColor(Color.parseColor("#607D8B"));

                        tv.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                    } else {
                        view.setBackgroundColor(Color.parseColor("#D32F2F"));
                    }
                }
                return view;
            }

        };
        ((BaseAdapter) la).notifyDataSetChanged();
        lv.setAdapter(la);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {

                HashMap<String, String> h = (HashMap<String, String>) adapter.getItemAtPosition(position);
                if(h.get("type").equals("loc")) {
                    Intent ebook = new Intent(Message.this, MapLocationSelection.class);
                    // get id of book and send it to EditBook activity
                    Log.i(h.get("lat"),h.get("lng"));
                    ebook.putExtra("lat", h.get("lat"));
                    ebook.putExtra("lng", h.get("lng"));
                    startActivity(ebook);
                }
                else
                     if(h.get("type").equals("image")){
                         Intent ebook = new Intent(Message.this, ShowImage.class);
                         // get id of book and send it to EditBook activity

                         ebook.putExtra("type", h.get("type"));
                         ebook.putExtra("imageid", h.get("messageid"));
                         AlarmManagerStops();
                         startActivity(ebook);
                     }
            }
        });
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        tvName = (TextView) findViewById(R.id.tvName);
        tvMessage = (TextView) findViewById(R.id.tvText);
        etmessage = (EditText) findViewById(R.id.etmessage);
        send=(Button)findViewById(R.id.btnsend);
        send.setOnClickListener(this);
//        AlarmManagerStops();
        AlarmManagerStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messagemenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sendloc:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return super.onOptionsItemSelected(item);
                }
                mLastLocation = LocationServices.FusedLocationApi
                        .getLastLocation(mGoogleApiClient);
                new UserLoginTask("loc").execute();
                return super.onOptionsItemSelected(item);
            case R.id.sendpic:
                Intent c=new Intent(Message.this,ChooseImage.class);
                c.putExtra("username",sp.getString("username","-1"));
                c.putExtra("receiver",curu);
                startActivity(c);
                return super.onOptionsItemSelected(item);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    @Override
    public void onConnected(Bundle bundle) {

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
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onClick(View v) {
        if (v == send) {

            new UserLoginTask("msg").execute();
        }

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
    protected void onStop() {
        super.onStop();
        AlarmManagerStops();
        stopService(new Intent(getBaseContext(), RetriveService.class));
        //curu=null;
        mGoogleApiClient.disconnect();

    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(m);
        AlarmManagerStops();
        stopService(new Intent(getBaseContext(), RetriveService.class));

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPostResume() {
        registerReceiver(m, i);
        AlarmManagerStart();
        super.onPostResume();
    }

    private void AlarmManagerStart(){
        Context context = getBaseContext();
        alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent gpsTrackerIntent = new Intent(context, AlarmReceiver.class);

        SharedPreferences ed=getSharedPreferences(MainActivity.MY_PREFS_NAME,MODE_PRIVATE);

        gpsTrackerIntent.putExtra("username", ed.getString("username","-1"));
        gpsTrackerIntent.putExtra("lastmessage", ed.getString("lastmessage","-1"));
        gpsTrackerIntent.putExtra("receiver", curu);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME,10000,pendingIntent);
            Log.i("sending data to alarm", ed.getString("username", "-1") + " " + curu);
        }
        else {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(),
                    20000, // 60000 = 1 minute
                    pendingIntent);
            Log.i("sending data to alarm", ed.getString("username", "-1") + " " + curu);
        }
    }

    private void AlarmManagerStops(){
        Context context = getBaseContext();
        Intent gpsTrackerIntent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, gpsTrackerIntent, 0);
        gpsTrackerIntent.removeExtra("receiver");
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);

    }

    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        double latitude,longitude;
        String name,message,receiver;
        SharedPreferences ed;
        String type;
        public UserLoginTask(String type) {
            this.type=type;
            message=etmessage.getText().toString();
            etmessage.setText("");
            receiver=curu;
            ed=getSharedPreferences(MainActivity.MY_PREFS_NAME,MODE_PRIVATE);

            name=ed.getString("username","-1");

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            HashMap<String, String > paras = new HashMap<>();
            paras.put("username", name);
            paras.put("receiver", receiver);

            if (type.equals("msg")) {
                paras.put("type", "msg");
                paras.put("message", message);
            }
            else if (type.equals("loc") && mLastLocation!=null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
                paras.put("loc",latitude+"");
                paras.put("lng",longitude+"");
                paras.put("type", "loc");
                Log.e("loc get",latitude+"   "+longitude);

            } else {
                return "error-Jds"+type.equals("msg");
            }

            // Calling post function from HttpCall class
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
                ed.edit().putString("lastmessage",json.getString("insertid")).commit();
//                lastmessage=json.getString("insertid");
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
                    Toast.makeText(getApplicationContext(), "Error Occurred! Try Again", Toast.LENGTH_SHORT).show();
                    break;
                case "signupcomplete":

                    Intent i = new Intent(getBaseContext(), RetriveService.class);
                    i.putExtra("username",name);
                    i.putExtra("lastmessage",ed.getString("lastmessage","-1"));
                    i.putExtra("receiver",curu);
                    getBaseContext().startService(i);
                    Toast.makeText(getApplicationContext(),"Successfully Sent!",Toast.LENGTH_SHORT).show();
                   // finish();
                    break;
            }
        }
    }
    public class MyReceiver extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                JSONObject jo= new JSONObject(intent.getStringExtra("json"));
                JSONArray ja= jo.getJSONArray("results");
//                list.clear();
                boolean move=false;
                for(int i=0;i<ja.length();i++){
                    JSONObject t= ja.getJSONObject(i);
                    if(i<list.size()){
                        if(list.get(i).get("messageid").equals(t.getString("messageid"))){
                            continue;
                        }
                    }
                    move=true;
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("name",t.getString("name"));
                    map.put("message",t.getString("message"));
                    map.put("messageid",t.getString("messageid"));
                    map.put("type", t.getString("type"));
                    if (t.getString("type").equals("loc")){
                        map.put("lat", t.getString("lat"));
                        map.put("lng", t.getString("lng"));
                    }
                    list.add(map);
                }
                ((BaseAdapter) la).notifyDataSetChanged();
//                lv.setAdapter(la);
                if(move)
                lv.setSelection(list.size()-1);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch(requestCode) {

            case 2:
                // when map activity finishes set text view
//                nextclass_time.setText("Latitude: "+data.getDoubleExtra("lat",-1)+
//                        "\n"+"Longitude: "+data.getDoubleExtra("lng",-1));

                break;
        }
    }
}
