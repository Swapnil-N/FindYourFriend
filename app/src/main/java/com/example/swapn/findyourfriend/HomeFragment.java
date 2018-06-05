package com.example.swapn.findyourfriend;

import android.content.Context;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    DatabaseReference databaseReference;
    FirebaseAuth mAuth;

    ListView listView;
    ArrayList<Friend> friendArrayList;
    FriendAdapter friendAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);


        listView = view.findViewById(R.id.friendListView);
        friendArrayList = new ArrayList<>();
        friendAdapter = new FriendAdapter(getActivity(), R.layout.list_layout,friendArrayList);
        listView.setAdapter(friendAdapter);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                friendArrayList.clear();
                for (DataSnapshot messageSnapShot: dataSnapshot.getChildren()) {
                    String friendName = (String) messageSnapShot.child("name").getValue();
                    double friendLatitude;
                    double friendLongitude;
                    if (messageSnapShot.child("latit").getValue() instanceof Long) {
                        friendLatitude = ((Long) messageSnapShot.child("latit").getValue()).doubleValue();
                    }else{
                        friendLatitude = (double) messageSnapShot.child("latit").getValue();
                    }
                    if (messageSnapShot.child("longit").getValue() instanceof Long) {
                        friendLongitude = ((Long) messageSnapShot.child("longit").getValue()).doubleValue();
                    }else{
                        friendLongitude = (double) messageSnapShot.child("longit").getValue();
                    }

                    String friendAddress = (String) messageSnapShot.child("theAddress").getValue();

                    friendArrayList.add(new Friend(friendName,friendLatitude,friendLongitude,friendAddress));

                    friendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return view;
    }

    public class FriendAdapter extends ArrayAdapter{

        Context context;
        List<Friend> friendList;

        public FriendAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List objects) {
            super(context, resource, objects);
            this.context = context;
            friendList = objects;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layoutView = inflater.inflate(R.layout.list_layout, null);
            Friend currentFriend = friendList.get(position);

            TextView uNameTextView = layoutView.findViewById(R.id.uNameTextView);
            TextView uLatTextView = layoutView.findViewById(R.id.uLatTextView);
            TextView uLongTextView = layoutView.findViewById(R.id.uLongTextView);
            TextView uAddTextView = layoutView.findViewById(R.id.uAddTextView);

            uNameTextView.setText(currentFriend.getName());
            uLatTextView.setText("Latitude: "+currentFriend.getLatit());
            uLongTextView.setText("Longitude: "+currentFriend.getLongit());
            uAddTextView.setText(currentFriend.getTheAddress());

            return layoutView;
        }
    }

}
