package gradlebuild.excel2017.debug;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main2Activity extends AppCompatActivity {
    Bitmap result = null;
    int counter=3;
    Button setWall= (Button) findViewById(R.id.setwall);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Bundle bundle=getIntent().getExtras();
        final String image=bundle.getString("url");

        ImageView imageView=(ImageView) findViewById(R.id.imageView);

        Glide.with(getApplicationContext()).load(image)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
        setWall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (counter==0) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Looper.prepare();

                            try {
                                result = Glide.
                                        with(getApplicationContext()).
                                        load(image).
                                        asBitmap().
                                        into(-1, -1).
                                        get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void dummy) {
                            if (null != result) {
                                // The full bitmap should be available here
                                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                                try {
                                    wallpaperManager.setBitmap(result);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            ;
                        }
                    }.execute();
                }
                else
                    counter--;
            }
        });
    }

}
