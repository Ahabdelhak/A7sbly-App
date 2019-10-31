package com.example.yourtrack;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    EditText editText ,editText2;
    TextView test, currentLoc ;

    LocationManager manager;
    Geocoder geocoder;

    double friend_lat;
    double friend_lng;

    double lat,longg;

    double distance,distanceKM;

    Button btncalcLE ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentLoc = findViewById(R.id.current);

        editText2 = findViewById(R.id.editText2);

        test=findViewById(R.id.textView3);
        btncalcLE=findViewById(R.id.button2);


        manager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            String[]perm={Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this,perm,1);
        }
        else{
            Toast.makeText(this, "gone", Toast.LENGTH_SHORT).show();
            manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1){
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                try {
                    manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, null);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
            }
            else{
                Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "got loc", Toast.LENGTH_SHORT).show();


        lat = location.getLatitude();
        longg=location.getLongitude();
//        locText.append(""+lat);
//        locText.append("\n"+longg);


        geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, longg, 1);
            //Toast.makeText(this, addresses + "", Toast.LENGTH_SHORT).show();

            //currentLoc.append("\n"+addresses.get(0).getAddressLine(0));
            currentLoc.setText(addresses.get(0).getAddressLine(0));

        } catch (IOException e) {
            e.printStackTrace();
        }




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

    public void map(View view) {
        Intent in=new Intent(Intent.ACTION_VIEW, Uri.parse("geo:"+lat+","+longg));
        startActivity(in);
    }


    public void Calculate(View view) {

        try {

            List<Address> friendLoc = geocoder.getFromLocationName(editText2.getText().toString(), 1);

            Address addr = friendLoc.get(0);

            friend_lat = addr.getLatitude();

            friend_lng = addr.getLongitude();


            Toast.makeText(this, friend_lat +" " +   friend_lng , Toast.LENGTH_SHORT).show();


            LatLng startLatLng = new LatLng(lat, longg);
            LatLng endLatLng = new LatLng(friend_lat, friend_lng);
            distance = SphericalUtil.computeDistanceBetween(startLatLng, endLatLng);
            distanceKM = Double.parseDouble(String.valueOf(distance * 0.001));

            test.setText("" + distanceKM + " K.M");

            btncalcLE.setEnabled(true);

            //Assume that every kilo take 0.06 litre

            double litre = distanceKM*0.06;
            System.out.println("Litre " + litre);

            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+lat+","+longg+"&daddr="+friend_lat+","+friend_lng;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(Intent.createChooser(intent, "Select an application"));


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //calc LE
    public void calc(View view) {

        //Assume that every kilo take 0.06 litre
        double litre = distanceKM*0.06;
        System.out.println("Litre " + litre);

        //Assume litre equal 6.75
        double Le = litre*6.75;
        test.setText("" + Le + " L.E");

    }
}
