package gradlebuild.excel2017.debug;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        onCreate(Bundle.EMPTY);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent=new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("url",images.get(position));
                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchImages();


    }

    private void fetchImages() {

        pDialog.setMessage("Downloading json...");
        pDialog.show();

        /*Sample response format
        [{
            "name": "Deadpool",
            "url": {
                "small": "https://api.androidhive.info/images/glide/small/deadpool.jpg",
                "medium": "https://api.androidhive.info/images/glide/medium/deadpool.jpg",
                "large": "https://api.androidhive.info/images/glide/large/deadpool.jpg"
            },
            "timestamp": "February 12, 2016"
        },
        {
            "name": "Batman vs Superman",
            "url": {
                "small": "https://api.androidhive.info/images/glide/small/bvs.png",
                "medium": "https://api.androidhive.info/images/glide/medium/bvs.png",
                "large": "https://api.androidhive.info/images/glide/large/bvs.png"
            },
            "timestamp": "March 25, 2016"
        }]
         */

        JsonArrayRequest req = new JsonArrayRequest(getString(R.string.downlink),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                JSONObject url = object.getJSONObject("url");
                                images.add(url.getString("large"));

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);

    }
}