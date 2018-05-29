package com.example.kim.goldentime;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.example.kim.goldentime.AED.AEDDTO;
import com.example.kim.goldentime.AED.AEDParsing;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class AEDMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager manager;
    double latitude = 0.0, longitude = 0.0;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_aedmaps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        startLocationService(); //gps
    }

    private void startLocationService() {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = 1000;
        float minDistance = 1;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mLocationListener);
        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, mLocationListener);
    }

    private void stopLocationService(){
        manager.removeUpdates(mLocationListener);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            latLng = new LatLng(latitude, longitude);

            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            MarkerOptions gpsMarker = new MarkerOptions();
            gpsMarker.position(latLng);// 위도 • 경도
            gpsMarker.title("현재 위치");// 제목 미리보기
            mMap.addMarker(gpsMarker).showInfoWindow();

            AEDParsing parser = new AEDParsing(latLng);
            ArrayList<AEDDTO> list = null;
            try {
                list = parser.apiParserSearch();
            } catch (Exception e) {
                e.printStackTrace();
            }

            for (AEDDTO entity : list) {
                MarkerOptions AEDMarker = new MarkerOptions();
                AEDMarker.position(new LatLng(entity.getYpos(), entity.getXpos()));
                AEDMarker.title(entity.getName());
                AEDMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.aed_heart));

                mMap.addMarker(AEDMarker);
            }
            stopLocationService();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
}
