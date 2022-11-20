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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    ArrayList markerPoints = new ArrayList();
    Marker marker = null;
    ArrayList<poliLocation> poliLocations = new ArrayList<poliLocation>();
    ArrayList<poliLocation> poliLocations3 = new ArrayList<poliLocation>();
    ArrayList<markerLocation> markerLocations = new ArrayList<markerLocation>();
    ArrayList<LatLng> poliLocations2 = new ArrayList<LatLng>();
    ArrayList<markerLocation> markerLocations2 = new ArrayList<markerLocation>();
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
                    polyline.remove();
                    markerLocations.clear();

                    /*
                    for(int i=0;i<markerLocations.size();i++) {
                        MarkerOptions markerSpital = new MarkerOptions();
                        markerSpital.position(markerLocations.get(i).location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                        markerSpital.title(markerLocations.get(i).name);
                        mMap.addMarker(markerSpital);
                    }
                    */
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
                                        JSONObject jsonObjectStartLocation = jsonObject3.getJSONObject("start_location");
                                        JSONObject jsonObjectEndLocation = jsonObject3.getJSONObject("end_location");
                                        LatLng start_location = new LatLng(jsonObjectStartLocation.getDouble("lat"), jsonObjectStartLocation.getDouble("lng"));
                                        LatLng end_location = new LatLng(jsonObjectEndLocation.getDouble("lat"), jsonObjectEndLocation.getDouble("lng"));
                                        poliLocations.add(new poliLocation(start_location,end_location));
                                    }

                                    /*
                                    Integer sizeSmecherRau = poliLocations.size();
                                    for(int i=0;i+2<sizeSmecherRau;i++){
                                        poliLocations3.add(new poliLocation(new LatLng(poliLocations.get(i).start.latitude,poliLocations.get(i).start.longitude),new LatLng(poliLocations.get(i).end.latitude,poliLocations.get(i).end.longitude)));
                                        Double calcul1 = poliLocations.get(i).end.latitude+poliLocations.get(i+1).start.latitude;
                                        Double calcul2 = poliLocations.get(i).end.longitude+poliLocations.get(i+1).start.longitude;
                                        poliLocations3.add(new poliLocation(new LatLng(poliLocations.get(i).start.latitude,poliLocations.get(i).start.longitude),new LatLng(calcul1/2,calcul2/2)));
                                            i++;
                                        }
                                    */
                                    for(int i=0;i<poliLocations.size();i++){
                                        polyline = googleMap.addPolyline(new PolylineOptions()
                                                .clickable(true)
                                                .add(poliLocations.get(i).start,
                                                        poliLocations.get(i).end));
                                    }
                                    for(int i=0;i<poliLocations.size();i++){
                                        String bufferLatitudePoliLocation = new DecimalFormat("##.##").format(poliLocations.get(i).end.latitude).replace(",",".");
                                        String bufferLongitudePoliLocation = new DecimalFormat("##.##").format(poliLocations.get(i).end.longitude).replace(",",".");
                                         poliLocations2.add(new LatLng(Double.parseDouble(bufferLatitudePoliLocation),Double.parseDouble(bufferLongitudePoliLocation)));
                                    }
                                    for(int i=0;i<markerLocations.size();i++){
                                        String bufferLatitudeMarkerLocations = new DecimalFormat("##.##").format(markerLocations.get(i).location.latitude).replace(",",".");
                                        String bufferLongitudeMarkerLocations = new DecimalFormat("##.##").format(markerLocations.get(i).location.longitude).replace(",",".");
                                        for(int j=0;j<poliLocations.size();j++) {
                                            Double bufferLatitutePoliLocation = poliLocations2.get(j).latitude;
                                            Double bufferLongitudePoliLocation = poliLocations2.get(j).longitude;
                                            if(Double.parseDouble(bufferLatitudeMarkerLocations)== bufferLatitutePoliLocation && Double.parseDouble(bufferLongitudeMarkerLocations)== bufferLongitudePoliLocation) {
                                                markerLocations2.add(markerLocations.get(i));
                                            }
                                        }
                                    }
                                    for(int i=0;i<markerLocations2.size();i++) {
                                        MarkerOptions markerSpital = new MarkerOptions();
                                        markerSpital.position(markerLocations2.get(i).location).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                        markerSpital.title(markerLocations2.get(i).name);
                                        mMap.addMarker(markerSpital);
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

                JSONArray jsonArrayToSend = new JSONArray();
                JSONObject jsonObjectToSend = new JSONObject();
                try {
                    for(int i=0;i<markerLocations2.size();i++) {
                        jsonObjectToSend.put("deviceID", markerLocations2.get(i).id);
                        jsonObjectToSend.put("deviceName",  markerLocations2.get(i).name);
                        jsonObjectToSend.put("macAdress",markerLocations2.get(i).mac);
                        jsonObjectToSend.put("latitude",markerLocations2.get(i).location.latitude);
                        jsonObjectToSend.put("longitude",markerLocations2.get(i).location.longitude);
                        jsonArrayToSend.put(i,jsonObjectToSend);
                    }
                    JSONObject jsonObjectToSend2 = new JSONObject();
                    jsonObjectToSend2.put("Nelu",jsonArrayToSend);
                    Log.e("celMaiSmek",jsonObjectToSend2.toString());
                    RequestQueue queue2 = Volley.newRequestQueue(MapsActivity.this);
                    JsonObjectRequest jsonObjectRequest2 = new JsonObjectRequest(Request.Method.POST, "http://192.168.0.100:8001/",jsonObjectToSend2,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
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


            }
        });
    }

}