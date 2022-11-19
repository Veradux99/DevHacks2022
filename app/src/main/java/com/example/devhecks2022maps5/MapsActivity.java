package com.example.devhecks2022maps5;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.devhecks2022maps5.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ArrayList markerPoints = new ArrayList();
    Marker marker = null;
    ArrayList<poliLocation> poliLocations = new ArrayList<poliLocation>();
    Polyline polyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng spital = new LatLng(44.45381848453719, 26.10126692750373);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spital, 14));
        markerPoints.add(spital);

        MarkerOptions markerSpital = new MarkerOptions();
        markerSpital.position(spital).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerSpital.title("Spitalul Floreasca");
        googleMap.addMarker(markerSpital);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                if (marker != null) {
                    marker.remove();
                    markerPoints.remove(1);
                    poliLocations.clear();
                    polyline.remove();
                    mMap.clear();
                }
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                marker = googleMap.addMarker(markerOption);

                markerPoints.add(latLng);


                LatLng origin = (LatLng) markerPoints.get(0);
                LatLng dest = (LatLng) markerPoints.get(1);

                String originString=Double.toString(origin.latitude)+","+Double.toString(origin.longitude);
                String destString=Double.toString(dest.latitude)+","+Double.toString(dest.longitude);

                //RequestQueue  queue = Volley.newRequestQueue(this)
                //https://maps.googleapis.com/maps/api/directions/json?origin=44.45381848453719,26.10126692750373&destination=44.461089833543376,26.09665554016829&key=AIzaSyDGIOJMDLNcJQDFpxuC1_UruUHHbOFhkAk
                String baselink="https://maps.googleapis.com/maps/api/directions/json?origin=";
                String url = baselink+originString+"&destination="+destString+"&key=AIzaSyDGIOJMDLNcJQDFpxuC1_UruUHHbOFhkAk";
                //Log.e("url",url);

                RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Display the first 500 characters of the response string.
                                try {
                                    JSONArray jsonArray = response.getJSONArray("routes");
                                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                                    JSONArray jsonArray2 = jsonObject.getJSONArray("legs");
                                    JSONObject jsonObject2 = jsonArray2.getJSONObject(0);
                                    JSONArray jsonArray3 = jsonObject2.getJSONArray("steps");
                                    for(int i=0;i<jsonArray3.length();i++) {
                                        JSONObject jsonObject3 = jsonArray3.getJSONObject(i);
                                        JSONObject jsonObjectStartLocation = jsonObject3.getJSONObject("start_location");
                                        JSONObject jsonObjectEndLocation = jsonObject3.getJSONObject("end_location");
                                        LatLng start_location = new LatLng(jsonObjectStartLocation.getDouble("lat"), jsonObjectStartLocation.getDouble("lng"));
                                        LatLng end_location = new LatLng(jsonObjectEndLocation.getDouble("lat"), jsonObjectEndLocation.getDouble("lng"));
                                        poliLocations.add(new poliLocation(start_location,end_location));
                                    }
                                    Integer integer = poliLocations.size();
                                    Log.e("Start",integer.toString());
                                    for(int i=0;i<poliLocations.size();i++){
                                        polyline = googleMap.addPolyline(new PolylineOptions()
                                                .clickable(true)
                                                .add(poliLocations.get(i).start,
                                                        poliLocations.get(i).end));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error",error.toString());
                    }
                });

                queue.add(jsonObjectRequest);

                /*
                for(int i=0;i<poliLocations.size();i++){
                    Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                            .clickable(true)
                            .addAll((Iterable<LatLng>) poliLocations.get(i)));
                }
                */
            }
        });
    }

}