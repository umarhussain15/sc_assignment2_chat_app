package www.seu.com.lab4_sc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String MY_PREFS_NAME = "com.lab4.abc.xyz.aaa";
    Button Signup, Login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Signup = (Button)findViewById(R.id.btnsignup);
        Signup.setOnClickListener(this);
        Login = (Button)findViewById(R.id.btnlogin);
        Login.setOnClickListener(this);
        setTitle("Test Application");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnlogin:
                Intent i= new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
                break;
            case R.id.btnsignup:
                Intent j= new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(j);
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this, "Location Allowed", Toast.LENGTH_SHORT).show();
            /*HERE PERMISSION IS ALLOWED.
            *
            * YOU SHOULD CODE HERE*/


                } else {

                    Toast.makeText(MainActivity.this, "Permission deny to Access Location", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
