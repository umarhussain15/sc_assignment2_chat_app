package www.seu.com.lab4_sc;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Retrive extends AppCompatActivity {

    ListView lv;
    ListAdapter la;
    ArrayList<HashMap<String, Object>> list;
    UserLoginTask mAuthTask;
    private ProgressDialog mProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrive);
        mProgressDialog= new ProgressDialog(this);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        list= new ArrayList<>();
        lv= (ListView)findViewById(R.id.listView);
        mAuthTask= new UserLoginTask();
        mAuthTask.execute();


    }
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        AlertDialog ad;
//        private final String name,exp,dep,dob,doj,photo;
        UserLoginTask() {

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
            HashMap<String, String> paras = new HashMap<>();
//            paras.put("name", name);
//            paras.put("exp", exp);
//            paras.put("dep", dob);
//            paras.put("dob", doj);
//            paras.put("doj", doj);
//            paras.put("photo", photo);
            // Calling post function from HttpCall class
            JSONObject json = new HttpCall().getJSON(getResources().getString(R.string.server_receive));
            try {
                if (json==null)
                    return  "error-j";
                int success=json.getInt("success");
                Log.i("suc val int", "" + success);
                if (success==-2)
                    return "error";
                if (success==-1)
                    return json.getString("errors");
                JSONArray ja= json.getJSONArray("results");
                for(int i=0;i<ja.length();i++){
                    JSONObject t= ja.getJSONObject(i);
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("name",t.getString("name"));
                    map.put("exp",t.getString("exp"));
                    map.put("dep",t.getString("dep"));
                    map.put("dob",t.getString("dob"));
                    map.put("doj",t.getString("doj"));
                    if(t.has("latitude") && t.has("longitude")) {
                        map.put("lat", t.getString("latitude"));
                        map.put("doj", t.getString("longitude"));
                    }
                    byte[] decodedString = Base64.decode(t.getString("photo"), Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    map.put("photo",new BitmapDrawable(getResources(), decodedByte));
                    list.add(map);
                }
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
            Log.i("success val", success);
            switch(success){
                case "error-j":
                case "error":
                    Toast.makeText(getApplicationContext(), "Error Occurred! Try Again", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case "signupcomplete":
                    la=new SimpleAdapter(Retrive.this,
                            list,R.layout.listitem,
                            new String[]{"name","exp","dep","dob","doj","photo","lat","long"},new int[]{R.id.tvName,R.id.tvexp,R.id.tvDepartment,R.id.tvdob,R.id.tvdoj,R.id.imageView2,R.id.tvlat,R.id.tvLon});
                    ((BaseAdapter) la).notifyDataSetChanged();
                    lv.setAdapter(la);
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
