package www.seu.com.lab4_sc;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ShowImage extends AppCompatActivity {

    String id;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        Bundle b= getIntent().getExtras();
        id=b.getString("imageid");
        imageView=(ImageView)findViewById(R.id.showimg);
        getImage();
    }

    private void getImage() {

        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ProgressDialog loading;
            String add;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                add=getResources().getString(R.string.get_image);
                loading = ProgressDialog.show(ShowImage.this, "Uploading...", null, true, true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
                imageView.setImageBitmap(b);
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
                add=add+"?id="+id;
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }

        GetImage gi = new GetImage();
        gi.execute(id);
    }
}
