package www.seu.com.lab4_sc;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Umar on 11-Mar-16.
 */
public class RetriveService extends Service {

    String url;
    String name,lastmessage,receiver;
    @Override
    public void onCreate() {
        url = getResources().getString(R.string.retrievemessage) ;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        name= intent.getStringExtra("username");
        lastmessage= intent.getStringExtra("lastmessage");
        receiver= intent.getStringExtra("receiver");
        new PollServer().execute();

        //stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public class PollServer extends AsyncTask<Void, Void, JSONObject> {

        //Context cc;
        //        private final String name,exp,dep,dob,doj,photo;
        PollServer() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            //preparing post elements
            HashMap<String, String> paras = new HashMap<>();
//            Log.e("name is ",name);
            paras.put("name", name);
            paras.put("lastmessage", lastmessage);
            paras.put("receiver", receiver);
//            paras.put("dep", dob);
//            paras.put("dob", doj);
//            paras.put("doj", doj);
//            paras.put("photo", photo);
            // Calling post function from HttpCall class
            JSONObject json = new HttpCall().postForJSON(url,paras);
            try {
                if (json==null)
                    return  null;
                int success=json.getInt("success");
                Log.i("suc val int", "" + success);
                if (success==-2)
                    return null;
                if (success==-1)
                    return null;
                return json;
                // JSONArray ja= json.getJSONArray("results");
            } catch (JSONException e) {
                return null;
            }
            // TODO: register the new account here.
            //return null;
        }

        @Override
        protected void onPostExecute(final JSONObject jo) {
           Intent intent = new Intent("com.latlong.org");
            if(jo!=null) {
                intent.putExtra("json", jo.toString());
                sendBroadcast(intent);
            }
            stopSelf();
        }

        @Override
        protected void onCancelled() {
//            mAuthTask = null;
//            mProgressDialog.hide();
            stopSelf();
        }
    }
}
