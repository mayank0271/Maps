package com.example.user.googlemapsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{




    //vars

    private GoogleMap mMap;
    MarkerOptions markerOptions;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, @DrawableRes int vectorDrawableResourceId) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.ic_pin_drop_black_24dp);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorDrawableResourceId);
        vectorDrawable.setBounds(20, 20, vectorDrawable.getIntrinsicWidth() + 40, vectorDrawable.getIntrinsicHeight() + 20);
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
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

        mMap = googleMap;
        LatLng SIT = new LatLng(13.3268979,77.1239061);
        mMap.addMarker(new MarkerOptions().position(SIT).title("Marker at SIT"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SIT,15));
        new GetPlaces().execute();

        }

        private class GetPlaces extends AsyncTask<String,Void,ArrayList<String>>{

            ArrayList<String> latlnglist = new ArrayList<String>();
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                Toast.makeText(MapsActivity.this,"Map of Tumkur",Toast.LENGTH_SHORT);

            }

            @Override
            protected ArrayList<String> doInBackground(String... voids) {
                HttpHandler hh = new HttpHandler();
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=13.3268979,77.1239061&radius=5000&type=restaurant&keyword=cruise&key=AIzaSyDzNyIfF8qGcKXLm6qWftKu-eI76dH1RbA";
                String jsonString = hh.makeServiceCall(url);

                if(jsonString != null){

                    try {
                        JSONObject jsonObject = new JSONObject(jsonString);
                        //getting JSON Array node
                        JSONArray places = jsonObject.getJSONArray("results");

                        for(int i=0;i<places.length();i++){
                            JSONObject jobject = places.getJSONObject(i);
                            JSONObject jsonGeometry = jobject.getJSONObject("geometry").getJSONObject("location");
                            String name = jobject.getString("name");
                            String lat = jsonGeometry.getString("lat");
                            String lng = jsonGeometry.getString("lng");
                            latlnglist.add(name);
                            latlnglist.add(lat);
                            latlnglist.add(lng);
                        }



                    } catch (JSONException e) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Json parsing array",Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Json cannot be taken",Toast.LENGTH_SHORT).show();
                        }
                    });

                }



                return latlnglist;
            }

            @Override
            protected void onPostExecute(ArrayList<String> result) {
                super.onPostExecute(result);

                for(int i=0;i<result.size();i=i+3){
                    LatLng restaurant = new LatLng(Double.parseDouble(result.get(i+1)),Double.parseDouble(result.get(i+2)));
                    markerOptions = new MarkerOptions().position(restaurant).title(result.get(i));
                    markerOptions.icon(bitmapDescriptorFromVector(getApplicationContext(),R.drawable.ic_restaurant));
                    mMap.addMarker(markerOptions);
                    mMap.addCircle(new CircleOptions().center(new LatLng(Double.parseDouble(result.get(i+1)),Double.parseDouble(result.get(i+2)))).radius(50).strokeWidth(R.color.colorAccent).fillColor(R.color.colorPrimaryDark));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(restaurant));



                }




            }
        }


    }




