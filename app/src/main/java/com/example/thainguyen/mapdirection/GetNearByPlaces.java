package com.example.thainguyen.mapdirection;

import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class GetNearByPlaces extends AsyncTask<Object,String,String> {

    String data;
    GoogleMap googleMap;
    String url;
    @Override
    protected String doInBackground(Object... objects) {
        googleMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        DownloadURL downloadURL = new DownloadURL();

        try {
            data = downloadURL.readURl(url);
        }catch (IOException e)
        {
            e.printStackTrace();
        }

        return data;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> listNear = null;
        DataParse dataParse = new DataParse();
        listNear = dataParse.parse(s);
        DisplayNearbyPlace(listNear);

    }
    private void DisplayNearbyPlace(List<HashMap<String,String>> nearList)
    {
        for(int i=0;i < nearList.size();i++)
        {
            MarkerOptions options = new MarkerOptions();

            HashMap<String,String> getNearBy = nearList.get(i);
            String nameplace = getNearBy.get("place_name");
            String vicinity = getNearBy.get("vicinity");
            double lat = Double.parseDouble(getNearBy.get("lat"));
            double lng = Double.parseDouble(getNearBy.get("lng"));

            LatLng latLng = new LatLng(lat,lng);
            options.position(latLng);
            options.title(nameplace + ":" + vicinity);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            googleMap.addMarker(options);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
        }
    }
}
