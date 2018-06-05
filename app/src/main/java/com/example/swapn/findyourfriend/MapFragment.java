package com.example.swapn.findyourfriend;

import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;
    FirebaseUser cUser;

    double cUserLat;
    double cUserLong;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_map, null);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        cUser = mAuth.getCurrentUser();

        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView = mView.findViewById(R.id.map);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getActivity());

        mGoogleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        try {
            mGoogleMap.setMyLocationEnabled(true);
        }catch (SecurityException e){
            Toast.makeText(getActivity(), "Unable to aquire location. Did you give us permission to use your location?", Toast.LENGTH_SHORT).show();
        }


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot mapSnapShot: dataSnapshot.getChildren()){
                    String friendName = (String) mapSnapShot.child("name").getValue();
                    double friendLatitude;
                    double friendLongitude;
                    if (mapSnapShot.child("latit").getValue() instanceof Long) {
                        friendLatitude = ((Long) mapSnapShot.child("latit").getValue()).doubleValue();
                    }else{
                        friendLatitude = (double) mapSnapShot.child("latit").getValue();
                    }
                    if (mapSnapShot.child("longit").getValue() instanceof Long) {
                        friendLongitude = ((Long) mapSnapShot.child("longit").getValue()).doubleValue();
                    }else{
                        friendLongitude = (double) mapSnapShot.child("longit").getValue();
                    }
                    String friendAddress = (String) mapSnapShot.child("theAddress").getValue();

                    try {
                        if (((String) mapSnapShot.child("FriendID").getValue()).equals(cUser.getUid())) {
                            cUserLat = friendLatitude;
                            cUserLong = friendLongitude;
                        } else {
                            mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(friendLatitude, friendLongitude)).title(friendName).snippet(friendAddress));
                        }
                    }catch (Exception e){
//                        Toast.makeText(getContext(), "there was some problem try again later", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }

                CameraPosition fCameraPosition = CameraPosition.builder().target(new LatLng(cUserLat,cUserLong)).zoom(14).build();
                mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(fCameraPosition));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
