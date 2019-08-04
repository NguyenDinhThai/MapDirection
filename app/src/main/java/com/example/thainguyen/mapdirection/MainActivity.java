package com.example.thainguyen.mapdirection;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private GoogleMap mMap;
    private ProgressDialog progressDialog;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    private double latitude,longtude;
    private Location lastLocaton;
    private LatLng direcLocation;
    private int RADIUS = 5000;
    private int PLACE_PICKER_REQUEST =1;
    private final String API_KEY = "AIzaSyB8czWQFZqqfvS83z1MXJhzz37ZeBf3JGw";
    private String nearPlace = "restaurant";
    private ImageButton imgLocation;
    private ImageButton imgNearBy;
    private ImageButton imgDirec;
    private PolylineOptions polylineOptions;
    private Object tranfer[]=new Object[2];
    ArrayList<LatLng> listPoints;
    private GetNearByPlaces getNearByPlaces= new GetNearByPlaces();
    private  TakeDirection takeDirection = new TakeDirection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(true);
        progressDialog.show();
        polylineOptions = new PolylineOptions();


        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });
    }

    private void onMyMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                init();
                progressDialog.dismiss();
                askPermissonShowMyLocation();
                Toast.makeText(MainActivity.this, "Map is ready", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void askPermissonShowMyLocation()
    {
        if(Build.VERSION.SDK_INT >=23)
        {
            int accessCoarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFine = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

            if(accessCoarse != PackageManager.PERMISSION_GRANTED || accessFine !=PackageManager.PERMISSION_GRANTED)
            {
                String[] permisson = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
                ActivityCompat.requestPermissions(this,permisson,REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
                return;
            }
        }
        this.showMyLocation();
    }
    private String getEnableLocationProvider()
    {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria,true);
        boolean enabled = locationManager.isProviderEnabled(bestProvider);
        if(!enabled)
        {
            Toast.makeText(this, "No location provider", Toast.LENGTH_SHORT).show();
            return null;
        }
        return  bestProvider;

    }
    private void showMyLocation() {

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String locationProvider = this.getEnableLocationProvider();
        if(locationProvider==null)
        {
            return;
        }
        Location myLocation = null;
        //latitude = myLocation.getLatitude();
        //longtude = myLocation.getLongitude();
        try {
            locationManager.requestLocationUpdates(locationProvider,1000,1,this);
            myLocation=locationManager.getLastKnownLocation(locationProvider);
        }catch (SecurityException e)
        {
            Toast.makeText(this, "Show location error", Toast.LENGTH_SHORT).show();
            Log.e("Location","Show My Location Error"+e.getMessage());
            e.printStackTrace();
        }
        if(myLocation != null)
        {

            LatLng latLng = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
            MarkerOptions options = new MarkerOptions();
            options.position(latLng);
            options.title("Here");
            Marker crrMarker = mMap.addMarker(options);
            crrMarker.showInfoWindow();

        }else {
            Toast.makeText(this, "Location not found ", Toast.LENGTH_SHORT).show();
        }
        lastLocaton = myLocation;
       // latitude = myLocation.getLatitude();
        //longtude = myLocation.getLongitude();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION:
                if(grantResults.length>1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED )
                {
                    Toast.makeText(this, "Permisson granted ", Toast.LENGTH_SHORT).show();
                    this.showMyLocation();
                }
                else {
                    Toast.makeText(this, "Permisson denied", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
    private void init()
    {
        clikMarker();
        imgLocation = findViewById(R.id.img_location);
        imgNearBy = findViewById(R.id.img_nearby);
        imgDirec = findViewById(R.id.img_direc);
        imgLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMyLocation();
            }
        });
        imgNearBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String url= getURL(lastLocaton.getLatitude(),lastLocaton.getLongitude(),nearPlace);
                tranfer[0] = mMap;
                tranfer[1]  = url;
                getNearByPlaces.execute(tranfer);
                Toast.makeText(MainActivity.this, "Searching"+nearPlace, Toast.LENGTH_SHORT).show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setEnabled(false);
                        Toast.makeText(MainActivity.this, "Find Near Place is disabled", Toast.LENGTH_SHORT).show();
                    }
                },10000);// 10s stop

            }
        });
        imgDirec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng myLocation = new LatLng(lastLocaton.getLatitude(),lastLocaton.getLongitude());
                try {
                    LatLng ute = direcLocation;
                    String url = getURLDirection(myLocation,ute);
                    takeDirection.execute(url);
                    /*
                    if(ute != null) {
                   // draw polyline on map
                        polylineOptions.add(myLocation, ute).width(10).color(Color.RED);
                        mMap.addPolyline(polylineOptions);
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(myLocation)             // Sets the center of the map to location user
                                .zoom(15)                   // Sets the zoom
                                .bearing(90)                // Sets the orientation of the camera to east
                                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        Toast.makeText(MainActivity.this, "Directing..", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(MainActivity.this, "No destination", Toast.LENGTH_SHORT).show();
                    }*/
                }catch (NullPointerException e)
                {
                    Toast.makeText(MainActivity.this, "No destination", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
    private String getURL(double lat,double lng,String nearPlace)
    {
        StringBuilder builder  = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        builder.append("location=").append(lat).append(",").append(lng);
        builder.append("&radius=").append(RADIUS);
        builder.append("&type=").append(nearPlace);
        builder.append("&sensor=true");
        builder.append("&key=").append(API_KEY);
        Log.d("GoogleMapsActivity", "url = " + builder.toString());
        return builder.toString();
    }

    private String getURLDirection(LatLng origin, LatLng dest) {
        //Value of origin
        String str_org = "origin=" + origin.latitude +","+origin.longitude;
        //Value of destination
        String str_dest = "destination=" + dest.latitude+","+dest.longitude;
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode for find direction
        String mode = "mode=driving";
        //Build the full param
        String param = str_org +"&" + str_dest + "&" +sensor+"&" +mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param;
        return url;
    }
    private void clikMarker()
    {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(Marker marker) {
               final Marker crrMarker  = marker;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Add to  your favorite place?");
                builder.setCancelable(false);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        crrMarker.getPosition();
                        direcLocation = crrMarker.getPosition();
                        MarkerOptions options = new MarkerOptions();
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        options.snippet("I like this place");
                        options.position(crrMarker.getPosition());
                        Marker marker1 = mMap.addMarker(options);
                        marker1.showInfoWindow();
                        Toast.makeText(MainActivity.this, "You added this places", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this,crrMarker.getTitle()+crrMarker.getPosition() , Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Toast.makeText(MainActivity.this,crrMarker.getTitle()+crrMarker.getPosition() , Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return false;

            }
        });

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    public void onClick(View view)
    {
        imgNearBy.setEnabled(false);
    }
}
