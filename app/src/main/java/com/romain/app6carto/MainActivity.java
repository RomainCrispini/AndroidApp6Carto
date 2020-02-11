package com.romain.app6carto;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private LocationManager lm;

    // Récupération de mon composant graphique: le fragment
    private MapFragment mapFragment;
    private GoogleMap googleMap;

    private static final int PERMS_CALL_ID = 1234;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.map);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();



    }

    private void checkPermission(){

        /**
         * On est obligé de checker les permissions qui donnent accès à la position du téléphone
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, PERMS_CALL_ID);
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        // On cherche à acquérir le locationManager, c'est une récupération
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Si mon capteur GPS est activé, je peux m'abonner aux events, le this est la classe MainActivity
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        }
        if(lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 10000, 0, this);
        }
        if(lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
        }

        loadMap();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMS_CALL_ID){
            checkPermission();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Si le LocationManager a été initialisé, on retire l'écouteur de l'ensemble des providers
        if(lm != null){
            lm.removeUpdates(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void loadMap(){
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {

                MainActivity.this.googleMap = googleMap;
                googleMap.moveCamera(CameraUpdateFactory.zoomBy(15));
                googleMap.setMyLocationEnabled(true);
                googleMap.addMarker(new MarkerOptions().position(new LatLng(43.799345, 6.725426)).title("Infini Software"));
            }
        });
    }


    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Cette méthode est obsolète
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double lattitude = location.getLatitude();
        double longitude = location.getLongitude();

        Toast.makeText(this, "Location : " + lattitude + "/" + longitude, Toast.LENGTH_LONG).show();
        if(googleMap != null){
            LatLng googleLocation = new LatLng(lattitude, longitude);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(googleLocation));
        }
    }


}
