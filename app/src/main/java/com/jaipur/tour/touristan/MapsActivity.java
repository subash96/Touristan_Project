package com.jaipur.tour.touristan;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.jaipur.tour.touristan.Model.MyPlaces;
import com.jaipur.tour.touristan.Model.Results;
import com.jaipur.tour.touristan.Remote.IGoogleAPIService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MapStyleOptions;
import android.content.res.Resources;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener
        {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private double latitude,longtitude;
    private Location lastLocation;
    private Marker currentLocationMarker;
    public  static final int REQUEST_LOCATION_CODE=99;
    private ArrayList<LatLng> points; //added
    Polyline line; //added
    LatLng latLng;
    public int i,k;
    IGoogleAPIService mService;
    ImageButton res,hotel,tour;
    MyPlaces currentPlace;


            private static final String TAG = MapsActivity.class.getSimpleName();



            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        points = new ArrayList<LatLng>();
        res=findViewById(R.id.res);
        tour=findViewById(R.id.tour);
        hotel=findViewById(R.id.hotel);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mService=Common.getGoogleAPIService();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }

     /*   res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearbyPlace("Restaurant");
            }
        });



        hotel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearbyPlace("Hotel");
            }
        });
        */

        i=0;
        k=0;
    }

    private void nearbyPlace(final String placeType)
    {
        mMap.clear();
        String url=getUrl(latitude,longtitude,placeType);
        mService.getNearByPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                currentPlace = response.body();

                if(response.isSuccessful())
                {
                    for(int i=0;i<response.body().getResults().length;i++) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        Results googlePlace = response.body().getResults()[i];
                        double lat = Double.parseDouble(String.valueOf(googlePlace.getGeometry().getLocation().getLat()));
                        double longitude = Double.parseDouble(String.valueOf(googlePlace.getGeometry().getLocation().getLng()));
                        String placeName = googlePlace.getName();
                        String vicinity = googlePlace.getVicinity();
                        LatLng latLng = new LatLng(lat, longitude);
                        markerOptions.position(latLng);
                        markerOptions.title(placeName);
                        if (placeType.equals("restaurant")) {

                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_rest));}



                        else if (placeType.equals("lodging")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hotel));
                        }
                        else if (placeType.equals("point_of_interest")) {
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hotel));
                        }
                        markerOptions.snippet(String.valueOf(i));
                        mMap.addMarker(markerOptions);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                    }
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {

            }
        });


    }
    private String getUrl(double latitude, double longtitude, String placeType)
    {
      StringBuilder googlePlacesUrl=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
      googlePlacesUrl.append("location="+latitude+","+longtitude);
      googlePlacesUrl.append("&radius="+10000);
      googlePlacesUrl.append("&type="+placeType);
      googlePlacesUrl.append("&sensor=true");
      googlePlacesUrl.append("&key="+"AIzaSyCpMHXstpX5vfd00JbwJA1h2VPXIYa-yEE");
      Log.d("getUrl",googlePlacesUrl.toString());
      return googlePlacesUrl.toString();
    }

            @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_map));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
                // Position the map's camera near Sydney, Australia.
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(-34, 151)));

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
        {
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Common.currentResult = currentPlace.getResults()[Integer.parseInt(marker.getSnippet())];
                    startActivity(new Intent(MapsActivity.this,ViewPlace.class));
                    return true;
                }
            });
        }

    }
    protected  synchronized  void buildGoogleApiClient()
    {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        client.connect();
    }
    @Override
    public void onLocationChanged(Location location) {
        k++;
        lastLocation=location;
        if(currentLocationMarker!=null)
        {
            currentLocationMarker.remove();

        }
        latitude=location.getLatitude();
        longtitude=location.getLongitude();
        latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions().position(latLng).title("Your location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentLocationMarker=mMap.addMarker(markerOptions);
        //points.add(latLng); //added
       // redrawLine();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        if(client!=null)
        {

            LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        }





    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_LOCATION_CODE:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    {

                        if(client==null)
                        {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }

                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

        }
        return;
    }

    public boolean checkLocationPermission()
    {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);
            }
            else
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);

            }
            return  false;
        }
        else
        {
            return true;
        }

    }
           // key=AIzaSyCpMHXstpX5vfd00JbwJA1h2VPXIYa-yEE
    @Override
    public void onConnected(@Nullable Bundle bundle) {

        locationRequest=new LocationRequest();
        locationRequest.setInterval(500);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
        }
    }
    @Override
    public void onConnectionSuspended(int i) {

        client.connect();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


    }
    public void hotel(View v)
    {
        nearbyPlace("lodging");
    }
    public void res(View v)
    {
        nearbyPlace("restaurant");
    }
    public void tou(View v) {nearbyPlace("point_of_interest");}
 /*   private void redrawLine(){
        mMap.clear();
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (int z = 0; z < points.size(); z++) {
            LatLng point = points.get(z);
            options.add(point);
        }
        addMarker(); //add Marker in current position
        line = mMap.addPolyline(options); //add Polyline
        addMarker();
    }
    */
  /*  public void addMarker()
    {
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(""+lastLocation.getLatitude()+","+lastLocation.getLongitude());
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentLocationMarker=mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
    }
    */

    public void stop(View v)
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(client,this);


    }





}

