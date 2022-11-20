package com.example.devhecks2022maps5;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ArrayList markerPoints = new ArrayList();
    Marker marker = null;
    ArrayList<LatLng> poliLocations = new ArrayList<>();
    ArrayList<LatLng> poliLocations2 = new ArrayList<>();
    ArrayList<LatLng> poliLocations3 = new ArrayList<>();
    ArrayList<markerLocation> markerLocations = new ArrayList<>();
    ArrayList<markerLocation> markerLocations2 = new ArrayList<>();
    ArrayList<markerLocation> markerLocations3 = new ArrayList<>();
    HashSet<markerLocation> hashSet = new HashSet<>();
    Polyline polyline;
    MarkerOptions markerSpital;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String[] salutari = new String[2];
        salutari[0]="Logged in";
        salutari[1]="Smenis";
        try {
            JSONArray jsonArrayToSend = new JSONArray(salutari);
            RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.POST, "http://192.168.0.100:8001/",jsonArrayToSend,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            // Display the first 500 characters of the response string.

                            try {
                                for(int i=0;i<response.length();i++){
                                    JSONObject jsonObject = response.getJSONObject(i);

                                    Integer id = jsonObject.getInt("deviceID");
                                    String deviceName = jsonObject.getString("deviceName");
                                    String mac = jsonObject.getString("macAddress");
                                    LatLng latLng = new LatLng(jsonObject.getDouble("latitude"),jsonObject.getDouble("longitude"));
                                    markerSpital = new MarkerOptions();
                                    markerSpital.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                    markerSpital.title(deviceName);
                                    mMap.addMarker(markerSpital);
                                    markerLocations.add(new markerLocation(id,deviceName,mac,latLng));
                                }
                            } catch (JSONException e) {
                                Log.e("errorParseOnCreate",e.toString());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("errorParse",error.toString());
                }
            });

            queue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng spital = new LatLng(44.45381848453719, 26.10126692750373);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(spital, 14));
        markerPoints.add(spital);

        MarkerOptions markerSpitalSpital = new MarkerOptions();
        markerSpitalSpital.position(spital).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        markerSpitalSpital.title("Spitalul Floreasca");
        googleMap.addMarker(markerSpitalSpital);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(@NonNull LatLng latLng) {
                googleMap.clear();
                if (marker != null) {
                    marker.remove();
                    markerPoints.remove(1);

                    poliLocations.clear();
                    poliLocations2.clear();
                    poliLocations3.clear();

                    polyline.remove();

                    markerLocations2.clear();
                    markerLocations3.clear();

                    hashSet.clear();
                }

                MarkerOptions markerSpitalSpital = new MarkerOptions();
                markerSpitalSpital.position(spital).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                markerSpitalSpital.title("Spitalul Floreasca");
                googleMap.addMarker(markerSpitalSpital);

                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                marker = googleMap.addMarker(markerOption);

                markerPoints.add(latLng);


                LatLng origin = (LatLng) markerPoints.get(0);
                LatLng dest = (LatLng) markerPoints.get(1);
                poliLocations.add(origin);

                String originString=Double.toString(origin.latitude)+","+Double.toString(origin.longitude);
                String destString=Double.toString(dest.latitude)+","+Double.toString(dest.longitude);

                String baselink="https://maps.googleapis.com/maps/api/directions/json?origin=";
                String url = baselink+originString+"&destination="+destString+"&key=AIzaSyDGIOJMDLNcJQDFpxuC1_UruUHHbOFhkAk";

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
                                        JSONObject jsonObjectEndLocation = jsonObject3.getJSONObject("end_location");
                                        LatLng end_location = new LatLng(jsonObjectEndLocation.getDouble("lat"), jsonObjectEndLocation.getDouble("lng"));
                                        poliLocations.add(end_location);
                                    }

                                    Integer sizeSmecherRau = poliLocations.size();
                                    for(int i=0;i<sizeSmecherRau;i++){
                                        poliLocations3.add(poliLocations.get(i));
                                        if(i+1==sizeSmecherRau)
                                            break;
                                        Double calculLat = (poliLocations.get(i).latitude+poliLocations.get(i+1).latitude)/2;
                                        Double calculLong = (poliLocations.get(i).longitude+poliLocations.get(i+1).longitude)/2;
                                        poliLocations3.add(new LatLng(calculLat,calculLong));
                                        }

                                    //TEST
                                    poliLocations.clear();
                                    sizeSmecherRau = poliLocations3.size();
                                    for(int i=0;i<sizeSmecherRau;i++){
                                        poliLocations.add(poliLocations3.get(i));
                                        if(i+1==sizeSmecherRau)
                                            break;
                                        Double calculLat = (poliLocations3.get(i).latitude+poliLocations3.get(i+1).latitude)/2;
                                        Double calculLong = (poliLocations3.get(i).longitude+poliLocations3.get(i+1).longitude)/2;
                                        poliLocations.add(new LatLng(calculLat,calculLong));
                                    }


                                    for(int i=0;i<poliLocations.size();i++)
                                    {
                                        polyline = googleMap.addPolyline(new PolylineOptions()
                                                .clickable(true)
                                                .addAll(poliLocations));
                                    }
                                    for(int i=0;i<poliLocations.size();i++){
                                        String bufferLatitudePoliLocation = new DecimalFormat("##.###").format(poliLocations.get(i).latitude).replace(",",".");
                                        String bufferLongitudePoliLocation = new DecimalFormat("##.###").format(poliLocations.get(i).longitude).replace(",",".");
                                         poliLocations2.add(new LatLng(Double.parseDouble(bufferLatitudePoliLocation),Double.parseDouble(bufferLongitudePoliLocation)));
                                    }


                                    markerLocations2.addAll(markerLocations);

                                    Integer numarSmek = markerLocations2.size();
                                    Log.e("numarSmek",numarSmek.toString());

                                    for(int i=0;i<markerLocations2.size();i++){
                                        String bufferLatitudeMarkerLocations = new DecimalFormat("##.###").format(markerLocations.get(i).location.latitude).replace(",",".");
                                        String bufferLongitudeMarkerLocations = new DecimalFormat("##.###").format(markerLocations.get(i).location.longitude).replace(",",".");
                                        for(int j=0;j<poliLocations2.size();j++) {
                                            Double bufferLatitutePoliLocation = poliLocations2.get(j).latitude;
                                            Double bufferLongitudePoliLocation = poliLocations2.get(j).longitude;
                                            if(Double.parseDouble(bufferLatitudeMarkerLocations)== bufferLatitutePoliLocation && Double.parseDouble(bufferLongitudeMarkerLocations)== bufferLongitudePoliLocation) {
                                                hashSet.add(markerLocations2.get(i));
                                                //markerLocations2.remove(i);
                                                //if(i == markerLocations2.size())
                                                //    break;
                                                //j=0;
                                            }
                                        }
                                    }

                                    markerLocations3.addAll(hashSet);

                                    for(int i=0;i<markerLocations3.size();i++) {
                                        MarkerOptions markerSpital = new MarkerOptions();
                                        markerSpital.position(markerLocations3.get(i).location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                        markerSpital.title(markerLocations3.get(i).name);
                                        mMap.addMarker(markerSpital);
                                    }

                                    JSONArray jsonArrayToSend = new JSONArray();
                                    try {
                                        for(int i=0;i<markerLocations3.size();i++) {
                                            JSONObject jsonObjectToSend = new JSONObject();
                                            Log.e("TEST",markerLocations3.get(i).id.toString()+"|"+markerLocations3.get(i).name.toString()+"|"+markerLocations3.get(i).mac.toString());
                                            jsonObjectToSend.put("deviceID", markerLocations3.get(i).id);
                                            jsonObjectToSend.put("deviceName",markerLocations3.get(i).name);
                                            jsonObjectToSend.put("macAddress",markerLocations3.get(i).mac);
                                            jsonObjectToSend.put("latitude",markerLocations3.get(i).location.latitude);
                                            jsonObjectToSend.put("longitude",markerLocations3.get(i).location.longitude);
                                            jsonArrayToSend.put(i,jsonObjectToSend);
                                        }
                                        Log.e("celMaiSmek",jsonArrayToSend.toString());
                                        RequestQueue queue2 = Volley.newRequestQueue(MapsActivity.this);
                                        JsonArrayRequest jsonObjectRequest2 = new JsonArrayRequest(Request.Method.POST, "http://192.168.0.100:8001/",jsonArrayToSend,
                                                new Response.Listener<JSONArray>() {
                                                    @Override
                                                    public void onResponse(JSONArray response) {
                                                        // Display the first 500 characters of the response string.

                                                        Log.e("response",response.toString());

                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.e("errorPRIMITKAKA",error.toString());
                                            }
                                        });

                                        queue2.add(jsonObjectRequest2);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
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




            }
        });
    }

}