package www.seu.com.lab4_sc;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BowserUsers extends AppCompatActivity {

    ListView lv;
    ListAdapter la;
    ArrayList<HashMap<String, String>> userslist;
    private ProgressDialog mProgressDialog;
    private int REQUEST_EXIT=99;
    private AlertDialog ad;
    int selected=4;
    String gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_course);
        mProgressDialog= new ProgressDialog(this);
        lv= (ListView)findViewById(R.id.collegelist);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                HashMap<String, String> h = (HashMap<String, String>) adapter.getItemAtPosition(position);
                Intent ebook = new Intent(BowserUsers.this, Message.class);
                // get id of book and send it to EditBook activity
                ebook.putExtra("username", h.get("username"));
                startActivity(ebook);
//                startActivityForResult(ebook, REQUEST_EXIT);
            }
        });
        new GetUsers().execute();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse_user_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_users:
                final String[] choiceList =
                        {"200m", "400m" ,"450m","600m", "800m" , "1600m","2000m" };
                // Create AlertDialog and set title
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Select Radius");
                builder.setSingleChoiceItems(choiceList,selected,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // get the selected item and set corresponding string to that
                                selected=item;
                                String radius="0";
                                switch (item){
                                    case 0:
                                        radius="200";
                                        break;
                                    case 1:
                                        radius="400";
                                        break;
                                    case 2:
                                        radius="450";
                                        break;
                                    case 3:
                                        radius="600";
                                        break;
                                    case 4:
                                        radius="800";
                                        break;
                                    case 5:
                                        radius="1600";
                                        break;
                                    case 6:
                                        radius="2000";
                                }
                                // call to server with dummy data

                                // Call to server with current location
//                                new GetUsers(mLastLocation.getLatitude()+"",
//                                          mLastLocation.getLongitude()+"",radius).execute();

                                // hide the dialog
                                ad.dismiss();
                                SharedPreferences ed=getSharedPreferences(MainActivity.MY_PREFS_NAME,MODE_PRIVATE);

                                Intent j= new Intent(BowserUsers.this,NearbyUsers.class);
                                j.putExtra("radius",radius);
                                j.putExtra("username", ed.getString("username", null));
                                startActivity(j);
                            }
                        });
                ad = builder.create();
                ad.show();
                return true;
            case R.id.gender_search:

                final String[] genderchoice =
                        {"Male", "Female"  };
                // Create AlertDialog and set title
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Select Gender");
                builder1.setSingleChoiceItems(genderchoice,-1,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                // get the selected item and set corresponding string to that


                                switch (item){
                                    case 0:
                                        gender="Male";
                                        break;
                                    case 1:
                                        gender="Female";
                                        break;

                                }

                                ad.dismiss();
                                final String[] choiceList =
                                        {"200m", "400m" ,"450m","600m", "800m" , "1600m","2000m" };
                                // Create AlertDialog and set title
                                AlertDialog.Builder builder = new AlertDialog.Builder(BowserUsers.this);
                                builder.setTitle("Select Radius");
                                builder.setSingleChoiceItems(choiceList, selected,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int item) {
                                                // get the selected item and set corresponding string to that
                                                selected = item;
                                                String radius = "0";
                                                switch (item) {
                                                    case 0:
                                                        radius = "200";
                                                        break;
                                                    case 1:
                                                        radius = "400";
                                                        break;
                                                    case 2:
                                                        radius = "450";
                                                        break;
                                                    case 3:
                                                        radius = "600";
                                                        break;
                                                    case 4:
                                                        radius = "800";
                                                        break;
                                                    case 5:
                                                        radius = "1600";
                                                        break;
                                                    case 6:
                                                        radius = "2000";
                                                }
                                                // call to server with dummy data

                                                // Call to server with current location
//                                new GetUsers(mLastLocation.getLatitude()+"",
//                                          mLastLocation.getLongitude()+"",radius).execute();

                                                // hide the dialog
                                                ad.dismiss();
                                                SharedPreferences ed = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE);

                                                Intent j = new Intent(BowserUsers.this, NearbyUsers.class);
                                                j.putExtra("radius", radius);
                                                j.putExtra("username", ed.getString("username", null));
                                                j.putExtra("gender",gender);
                                                startActivity(j);
                                            }
                                        });
                                ad = builder.create();
                                ad.show();
                            }
                        });
                ad = builder1.create();
                ad.show();
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }
    public class GetUsers extends AsyncTask<Void, Void, String> {
        String username;
        public GetUsers() {
            super();
            SharedPreferences ed=getSharedPreferences(MainActivity.MY_PREFS_NAME,MODE_PRIVATE);
           username=ed.getString("username",null);
        }

        @Override
        protected void onPreExecute() {
            userslist = new ArrayList<>();
//            super.onPreExecute();
//            mProgressDialog= new ProgressDialog(AddBook.this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {

            HashMap<String, String> paras = new HashMap<>();
            paras.put("username", username);
            JSONObject jsoncol = new HttpCall().postForJSON(getResources().getString(R.string.retireveusers), paras);

            try{
                if(jsoncol==null ){
                    return "error-j";
                }
                int successcol=jsoncol.getInt("success");
                if (successcol==-2 )
                    return "error";
                if (successcol==-1 )
                    return jsoncol.getString("errors");
                Log.e("JSON",jsoncol.toString());
                JSONArray jcor= jsoncol.getJSONArray("results");
                // build an hashmap from books list
                for(int i=0;i<jcor.length();i++){
                    JSONObject t= jcor.getJSONObject(i);
                    HashMap<String,String> map=new HashMap<>();
                    map.put("gender",t.getString("gender"));
                    map.put("username",t.getString("username"));
                    userslist.add(map);
                }
                Log.d("count", jsoncol.getString("count"));
                Log.d("hc",userslist.size()+"");
                return "result";

            }
            catch(JSONException e){
                Log.e("Async Task Error", e.toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            switch (s){
//                case "noresult":
//                    lv.setAdapter(null);
//                    empty.setVisibility(View.VISIBLE);
//                    break;
                case "result":
//                    empty.setVisibility(View.INVISIBLE);
//                    // creating adpater to add data to list.
                    la=new SimpleAdapter(BowserUsers.this,
                            userslist,R.layout.listitemc,
                            new String[]{"username","gender"},new int[]{R.id.Name,R.id.Gender});
                    ((BaseAdapter) la).notifyDataSetChanged();

                    lv.setAdapter(la);
                    break;
            }
            mProgressDialog.hide();

            super.onPostExecute(s);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
